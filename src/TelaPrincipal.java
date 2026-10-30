import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TelaPrincipal extends javax.swing.JFrame {

    public TelaPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Pesque Mais");
        setSize(440, 360);
        setLocationRelativeTo(null);              // Centraliza na tela. 
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Fecha o programa todo. 
        setLayout(null);                          // Posicionamento manual. 

        JLabel lblTitulo = new JLabel("Pesque Mais");
        lblTitulo.setBounds(20, 15, 380, 30);
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo);

        JButton btnProdutos = new JButton("Produtos");
        btnProdutos.setBounds(80, 70, 260, 45);
        btnProdutos.addActionListener(e -> new TelaProduto().setVisible(true));
        add(btnProdutos);

        JButton btnClientes = new JButton("Clientes");
        btnClientes.setBounds(80, 130, 260, 45);
        btnClientes.addActionListener(e -> new TelaCliente().setVisible(true));
        add(btnClientes);

        JButton btnVendas = new JButton("Vendas");
        btnVendas.setBounds(80, 190, 260, 45);
        btnVendas.addActionListener(e -> new TelaVenda().setVisible(true));
        add(btnVendas);

        JButton btnRelatorio = new JButton("Relatório de Vendas");
        btnRelatorio.setBounds(80, 250, 260, 45);
        btnRelatorio.addActionListener(e -> new TelaRelatorio().setVisible(true));
        add(btnRelatorio);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}