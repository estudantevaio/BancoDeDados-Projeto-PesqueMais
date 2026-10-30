package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoBanco {
    private Conexao conexao;
    private Connection conn;

    public ProdutoBanco() {
        this.conexao = new Conexao();
        this.conn = this.conexao.getConexao();
    }

    public List<Produto> getProdutos() {
        String sql = "SELECT * FROM produto";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            List<Produto> lista = new ArrayList<>();
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("id_produto"));
                p.setNome(rs.getString("nome"));
                p.setMarca(rs.getString("marca"));
                p.setCategoria(rs.getString("categoria"));
                p.setPreco(rs.getDouble("preco"));
                p.setEstoque(rs.getInt("estoque"));
                p.setFoto(rs.getString("foto"));
                lista.add(p);
            }
            return lista;
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar produtos. " + ex.getMessage());
            return null;
        }
    }

    public void inserir(Produto p) {
        String sql = "INSERT INTO produto (nome, marca, categoria, preco, estoque, foto) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getMarca());
            stmt.setString(3, p.getCategoria());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setString(6, p.getFoto()); 
            stmt.execute();
            System.out.println("Produto inserido com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao inserir produto. " + ex.getMessage());
        }
    }

    public void editar(Produto p) {
        String sql = "UPDATE produto SET nome = ?, marca = ?, categoria = ?, preco = ?, estoque = ?, foto = ? WHERE id_produto = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getMarca());
            stmt.setString(3, p.getCategoria());
            stmt.setDouble(4, p.getPreco());
            stmt.setInt(5, p.getEstoque());
            stmt.setString(6, p.getFoto());
            stmt.setInt(7, p.getId());
            stmt.execute();
            System.out.println("Produto atualizado com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar produto. " + ex.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            System.out.println("Produto excluído com sucesso.");
        } catch (SQLException ex) {
            
            System.out.println("Erro ao excluir produto. " + ex.getMessage());
        }
    }
}