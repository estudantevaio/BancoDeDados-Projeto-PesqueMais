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

    // READ: Lista todas as vendas com o nome do cliente. 
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

    // READ: Busca os itens de uma venda específica.
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

    // CREATE: Insere a venda e todos os itens em uma única transação.
    public void inserir(Venda venda, List<ItemVenda> itens) {
        String sqlVenda = "INSERT INTO venda (id_cliente, total) VALUES (?, ?)";
        String sqlItem  = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            // 1.) Desliga o autocommit: Nada é gravado até o commit().
            conn.setAutoCommit(false);

            // 2.) Insere a venda e recupera o ID gerado automaticamente.
            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setInt(1, venda.getIdCliente());
            stmtVenda.setDouble(2, venda.getTotal());
            stmtVenda.execute();

            ResultSet rsKeys = stmtVenda.getGeneratedKeys();
            int idVenda = 0;
            if (rsKeys.next()) {
                idVenda = rsKeys.getInt(1); // ID da venda recém-criada
            }

            // 3.) Insere cada item vinculado à venda.
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
            for (ItemVenda item : itens) {
                stmtItem.setInt(1, idVenda);
                stmtItem.setInt(2, item.getIdProduto());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.setDouble(5, item.getSubtotal());
                stmtItem.execute();
            }

            // 4.) Confirma: Tudo é gravado junto no banco.
            conn.commit();
            System.out.println("Venda registrada com sucesso (ID " + idVenda + ").");

        } catch (SQLException ex) {
            // Se qualquer coisa der errado, desfaz tudo.
            try {
                conn.rollback();
                System.out.println("Transação desfeita: " + ex.getMessage());
            } catch (SQLException e2) {
                System.out.println("Erro no rollback: " + e2.getMessage());
            }
        } finally {
            // Devolve a conexão ao modo normal.
            try { 
                conn.setAutoCommit(true); 
            } catch (SQLException e) {
                
            }
        }
    }

    // UPDATE: Atualiza a venda: Apaga os itens antigos e reagrava a transação. 
    public void editar(Venda venda, List<ItemVenda> itens) {
        String sqlVenda    = "UPDATE venda SET id_cliente = ?, total = ? WHERE id_venda = ?";
        String sqlDelItens = "DELETE FROM item_venda WHERE id_venda = ?";
        String sqlItem     = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda);
            stmtVenda.setInt(1, venda.getIdCliente());
            stmtVenda.setDouble(2, venda.getTotal());
            stmtVenda.setInt(3, venda.getId());
            stmtVenda.execute();

            // Apaga os itens antigos.
            PreparedStatement stmtDel = conn.prepareStatement(sqlDelItens);
            stmtDel.setInt(1, venda.getId());
            stmtDel.execute();

            // Regrava os itens novos .
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
            for (ItemVenda item : itens) {
                stmtItem.setInt(1, venda.getId());
                stmtItem.setInt(2, item.getIdProduto());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.setDouble(5, item.getSubtotal());
                stmtItem.execute();
            }

            conn.commit();
            System.out.println("Venda atualizada com sucesso.");

        } catch (SQLException ex) {
            try {
                conn.rollback();
                System.out.println("Transação desfeita: " + ex.getMessage());
            } catch (SQLException e2) {
                System.out.println("Erro no rollback: " + e2.getMessage());
            }
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    // DELETE: Exclui a venda.
    public void excluir(int id) {
        String sql = "DELETE FROM venda WHERE id_venda = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            System.out.println("Venda excluída com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir venda. " + ex.getMessage());
        }
    }

    // READ: Relatório que usa a VIEW criada no banco, retornando as linhas prontas para a JTable.
    public List<Object[]> getRelatorio() {
        String sql = "SELECT * FROM vw_relatorio_vendas ORDER BY cliente";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Object[]> linhas = new ArrayList<>();
            while (rs.next()) {
                // Cada linha do relatório é um array de 7 colunas na ordem da view
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