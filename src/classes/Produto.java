package classes;

public class Produto {
    private int id;
    private String nome;
    private String marca;
    private String categoria;
    private double preco;
    private int estoque;
    private String foto;

    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setNome(String nome) {
        this.nome = nome; 
    }
    
    public void setMarca(String marca) { 
        this.marca = marca; 
    }
    
    public void setCategoria(String categoria) { 
        this.categoria = categoria; 
    }
    
    public void setPreco(double preco) { 
        this.preco = preco; 
    }
    
    public void setEstoque(int estoque) { 
        this.estoque = estoque; 
    }
    
    public void setFoto(String foto) { 
        this.foto = foto; 
    }
 

    public int getId() { 
        return id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public String getMarca() { 
        return marca; 
    }
    
    public String getCategoria() { 
        return categoria; 
    }
    
    public double getPreco() { 
        return preco; 
    }
    
    public int getEstoque() { 
        return estoque; 
    }
    
    public String getFoto() { 
        return foto; 
    }
    

    // Preenchimento das tabelas.
    public Object[] obterDados() {
        return new Object[] { nome, marca, categoria, preco, estoque, foto };
    }
}