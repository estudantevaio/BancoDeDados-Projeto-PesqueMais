package classes;

public class ItemVenda {
    private int id;
    private int idVenda;
    private int idProduto;
    private String nomeProduto;   
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setIdVenda(int idVenda) { 
        this.idVenda = idVenda; 
    }
    
    public void setIdProduto(int idProduto) { 
        this.idProduto = idProduto; 
    }
    
    public void setNomeProduto(String nomeProduto) { 
        this.nomeProduto = nomeProduto; 
    }
    
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; 
    }
    
    public void setPrecoUnitario(double precoUnitario) { 
        this.precoUnitario = precoUnitario; 
    }
    
    public void setSubtotal(double subtotal) { 
        this.subtotal = subtotal; 
    }
    

    public int getId() { 
        return id; 
    }
    
    public int getIdVenda() { 
        return idVenda; 
    }
    
    public int getIdProduto() { 
        return idProduto; 
    }
    
    public String getNomeProduto() { 
        return nomeProduto; 
    }
    
    public int getQuantidade() { 
        return quantidade; 
    }
    
    public double getPrecoUnitario() { 
        return precoUnitario; 
    }
    
    public double getSubtotal() { 
        return subtotal; 
    }
    

    // Usado na JTable interna da tela de venda.
    public Object[] obterDados() {
        return new Object[] { nomeProduto, quantidade, precoUnitario, subtotal };
    }
}