package classes;

public class Cliente {
    private int id;
    private String nome;
    private String telefone;
    private String email;

    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public void setTelefone(String telefone) { 
        this.telefone = telefone; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    
    public int getId() { 
        return id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public String getTelefone() { 
        return telefone; 
    }
    
    public String getEmail() { 
        return email; 
    }
    

    public Object[] obterDados() {
        return new Object[] { nome, telefone, email };
    }
}