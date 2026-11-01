package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendaBanco {
    private Conexao conexao;
    private Connection conn;

    public VendaBanco() {
        this.conexao = new Conexao();
        this.conn = this.conexao.getConexao();
    }

    // ============================================================
    // READ — lista vendas com o nome do cliente. 
    // ============================================================
    public List<Venda> getVendas() {
        String sql = "SELECT v.id_venda, v.id_cliente, c.nome AS nome_cliente, v.data_venda, v.total " +
                     "FROM venda v JOIN cliente c ON v.id_cliente = c.id_cliente " +
                     "ORDER BY v.id_venda DESC";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Venda> lista = new ArrayList<>();
            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id_venda"));
                v.setIdCliente(rs.getInt("id_cliente"));
                v.setNomeCliente(rs.getString("nome_cliente"));
                v.setDataVenda(rs.getString("data_venda"));
                v.setTotal(rs.getDouble("total"));
                lista.add(v);
            }
            return lista;
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar vendas. " + ex.getMessage());
            return null;
        }
    }

    // ============================================================
    // READ — itens de uma venda. 
    // ============================================================
    public List<ItemVenda> getItensVenda(int idVenda) {
        String sql = "SELECT iv.id_item, iv.id_venda, iv.id_produto, p.nome AS nome_produto, " +
                     "iv.quantidade, iv.preco_unitario, iv.subtotal " +
                     "FROM item_venda iv JOIN produto p ON iv.id_produto = p.id_produto " +
                     "WHERE iv.id_venda = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idVenda);
            ResultSet rs = stmt.executeQuery();
            List<ItemVenda> itens = new ArrayList<>();
            while (rs.next()) {
                ItemVenda iv = new ItemVenda();
                iv.setId(rs.getInt("id_item"));
                iv.setIdVenda(rs.getInt("id_venda"));
                iv.setIdProduto(rs.getInt("id_produto"));
                iv.setNomeProduto(rs.getString("nome_produto"));
                iv.setQuantidade(rs.getInt("quantidade"));
                iv.setPrecoUnitario(rs.getDouble("preco_unitario"));
                iv.setSubtotal(rs.getDouble("subtotal"));
                itens.add(iv);
            }
            return itens;
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar itens da venda. " + ex.getMessage());
            return null;
        }
    }

    // ============================================================
    // AUXILIAR: versão interna que LANÇA exceção  
    // (usada dentro de transação: se falhar, tem que propagar). 
    // ============================================================
    private List<ItemVenda> buscarItensDaVenda(int idVenda) throws SQLException {
        String sql = "SELECT id_item, id_venda, id_produto, quantidade, preco_unitario, subtotal " +
                     "FROM item_venda WHERE id_venda = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idVenda);
        ResultSet rs = stmt.executeQuery();
        List<ItemVenda> itens = new ArrayList<>();
        while (rs.next()) {
            ItemVenda iv = new ItemVenda();
            iv.setId(rs.getInt("id_item"));
            iv.setIdVenda(rs.getInt("id_venda"));
            iv.setIdProduto(rs.getInt("id_produto"));
            iv.setQuantidade(rs.getInt("quantidade"));
            iv.setPrecoUnitario(rs.getDouble("preco_unitario"));
            iv.setSubtotal(rs.getDouble("subtotal"));
            itens.add(iv);
        }
        return itens;
    }

    // ============================================================
    // AUXILIAR: valida se há estoque suficiente
    // (retorna null se OK, ou mensagem de erro se faltar). 
    // ============================================================
    private String validarEstoque(List<ItemVenda> itens) throws SQLException {
        String sql = "SELECT nome, estoque FROM produto WHERE id_produto = ?";
        for (ItemVenda item : itens) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, item.getIdProduto());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int disponivel = rs.getInt("estoque");
                if (disponivel < item.getQuantidade()) {
                    return "Estoque insuficiente para '" + rs.getString("nome") +
                           "'. Disponível: " + disponivel +
                           ", solicitado: " + item.getQuantidade();
                }
            } else {
                return "Produto ID " + item.getIdProduto() + " não encontrado.";
            }
        }
        return null;
    }

    // ============================================================
    // AUXILIAR: baixa o estoque, subtraindo a quantidade. 
    // ============================================================
    private void baixarEstoque(List<ItemVenda> itens) throws SQLException {
        String sql = "UPDATE produto SET estoque = estoque - ? WHERE id_produto = ?";
        for (ItemVenda item : itens) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, item.getQuantidade());
            stmt.setInt(2, item.getIdProduto());
            stmt.execute();
        }
    }

    // ============================================================
    // AUXILIAR: devolve o estoque, somando a quantidade. 
    // ============================================================
    private void devolverEstoque(List<ItemVenda> itens) throws SQLException {
        String sql = "UPDATE produto SET estoque = estoque + ? WHERE id_produto = ?";
        for (ItemVenda item : itens) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, item.getQuantidade());
            stmt.setInt(2, item.getIdProduto());
            stmt.execute();
        }
    }

    // ============================================================
    // CREATE — insere venda + itens + abaixa o estoque  
    // (lança RuntimeException em caso de erro). 
    // ============================================================
    public void inserir(Venda venda, List<ItemVenda> itens) {
        // 1.) Valida estoque ANTES de abrir a transação. 
        try {
            String erro = validarEstoque(itens);
            if (erro != null) {
                throw new RuntimeException(erro);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao validar estoque: " + ex.getMessage());
        }

        String sqlVenda = "INSERT INTO venda (id_cliente, total) VALUES (?, ?)";
        String sqlItem  = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            // 2.) Insere a venda e recupera o ID gerado. 
            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setInt(1, venda.getIdCliente());
            stmtVenda.setDouble(2, venda.getTotal());
            stmtVenda.execute();

            ResultSet rsKeys = stmtVenda.getGeneratedKeys();
            int idVenda = 0;
            if (rsKeys.next()) idVenda = rsKeys.getInt(1);

            // 3.) Insere os itens. 
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
            for (ItemVenda item : itens) {
                stmtItem.setInt(1, idVenda);
                stmtItem.setInt(2, item.getIdProduto());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.setDouble(5, item.getSubtotal());
                stmtItem.execute();
            }

            // 4. ) Baixa o estoque de cada produto, dentro da mesma transação. 
            baixarEstoque(itens);

            conn.commit();
            System.out.println("Venda registrada com sucesso (ID " + idVenda + ").");

        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException e2) { }
            throw new RuntimeException("Erro ao registrar venda: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    // ============================================================
    // UPDATE — atualiza venda + ajusta o estoque.  
    // ============================================================
    public void editar(Venda venda, List<ItemVenda> itens) {
        String sqlVenda    = "UPDATE venda SET id_cliente = ?, total = ? WHERE id_venda = ?";
        String sqlDelItens = "DELETE FROM item_venda WHERE id_venda = ?";
        String sqlItem     = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            // 1.) Busca os itens antigos da venda. 
            List<ItemVenda> itensAntigos = buscarItensDaVenda(venda.getId());

            // 2.) Devolve o estoque dos itens antigos, somando de volta. 
            devolverEstoque(itensAntigos);

            // 3.) Valida o estoque dos novos itens, agora que os antigos já voltaram. 
            String erro = validarEstoque(itens);
            if (erro != null) {
                conn.rollback();
                throw new RuntimeException(erro);
            }

            // 4.) Atualiza a venda. 
            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda);
            stmtVenda.setInt(1, venda.getIdCliente());
            stmtVenda.setDouble(2, venda.getTotal());
            stmtVenda.setInt(3, venda.getId());
            stmtVenda.execute();

            // 5.) Apaga os itens antigos.
            PreparedStatement stmtDel = conn.prepareStatement(sqlDelItens);
            stmtDel.setInt(1, venda.getId());
            stmtDel.execute();

            // 6.) Insere os novos itens. 
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
            for (ItemVenda item : itens) {
                stmtItem.setInt(1, venda.getId());
                stmtItem.setInt(2, item.getIdProduto());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.setDouble(5, item.getSubtotal());
                stmtItem.execute();
            }

            // 7.) Baixa o estoque dos novos itens. 
            baixarEstoque(itens);

            conn.commit();
            System.out.println("Venda atualizada com sucesso.");

        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException e2) { }
            throw new RuntimeException("Erro ao atualizar venda: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    // ============================================================
    // DELETE — exclui venda + devolve o estoque. 
    // ============================================================
    public void excluir(int id) {
        String sql = "DELETE FROM venda WHERE id_venda = ?";

        try {
            conn.setAutoCommit(false);

            // 1.) Busca os itens da venda. 
            List<ItemVenda> itens = buscarItensDaVenda(id);

            // 2.) Devolve o estoque de todos eles, somando todos. 
            devolverEstoque(itens);

            // 3.) Exclui a venda. 
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();

            conn.commit();
            System.out.println("Venda excluída com sucesso.");

        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException e2) { }
            throw new RuntimeException("Erro ao excluir venda: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    // ============================================================
    // READ — relatório da view. 
    // ============================================================
    public List<Object[]> getRelatorio() {
        String sql = "SELECT * FROM vw_relatorio_vendas ORDER BY cliente";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Object[]> linhas = new ArrayList<>();
            while (rs.next()) {
                Object[] linha = new Object[] {
                    rs.getString("cliente"),
                    rs.getString("item"),
                    rs.getString("marca"),
                    rs.getString("categoria"),
                    rs.getDouble("preco"),
                    rs.getInt("quantidade"),
                    rs.getDouble("total")
                };
                linhas.add(linha);
            }
            return linhas;
        } catch (SQLException ex) {
            System.out.println("Erro ao gerar relatório. " + ex.getMessage());
            return null;
        }
    }
}