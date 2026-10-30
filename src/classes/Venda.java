package classes;

public class Venda {
    private int id;
    private int idCliente;
    private String nomeCliente;   
    private String dataVenda;
    private double total;

    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setIdCliente(int idCliente) { 
        this.idCliente = idCliente; 
    }
    
    public void setNomeCliente(String nomeCliente) { 
        this.nomeCliente = nomeCliente; 
    }
    
    public void setDataVenda(String dataVenda) { 
        this.dataVenda = dataVenda; 
    }
    
    public void setTotal(double total) { 
        this.total = total; 
    }

    
    public int getId() { 
        return id; 
    }
    
    public int getIdCliente() { 
        return idCliente; 
    }
    
    public String getNomeCliente() { 
        return nomeCliente; 
    }
    
    public String getDataVenda() { 
        return dataVenda; 
    }
    
    public double getTotal() { 
        return total; 
    }

    
    public Object[] obterDados() {
        return new Object[] { nomeCliente, dataVenda, total };
    }
}