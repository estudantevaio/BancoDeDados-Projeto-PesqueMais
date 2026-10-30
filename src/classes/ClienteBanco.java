package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteBanco {
    private Conexao conexao;
    private Connection conn;

    public ClienteBanco() {
        this.conexao = new Conexao();
        this.conn = this.conexao.getConexao();
    }

    public List<Cliente> getClientes() {
        String sql = "SELECT * FROM cliente";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            List<Cliente> lista = new ArrayList<>();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                c.setEmail(rs.getString("email"));
                lista.add(c);
            }
            return lista;
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar clientes. " + ex.getMessage());
            return null;
        }
    }

    public void inserir(Cliente c) {
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";

        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTelefone());
            stmt.setString(3, c.getEmail());
            stmt.execute();
            System.out.println("Cliente inserido com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao inserir cliente. " + ex.getMessage());
        }
    }

    public void editar(Cliente c) {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id_cliente = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTelefone());
            stmt.setString(3, c.getEmail());
            stmt.setInt(4, c.getId());
            stmt.execute();
            System.out.println("Cliente atualizado com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar cliente. " + ex.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            System.out.println("Cliente excluído com sucesso.");
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir cliente. " + ex.getMessage());
        }
    }
}