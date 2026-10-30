package classes;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Nicholas
 */
public class Conexao {
    public Connection getConexao() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pescaria?useTimezone=true&serverTimezone=UTC", "root", ""); 
    
            System.out.println("Conexão realizada com sucesso. ");
            return conn;
        }  
        catch(Exception e) {
            System.out.println("Erro ao conectar ao banco de dados. " + e.getMessage());
            return null;
        }
    }    
}
