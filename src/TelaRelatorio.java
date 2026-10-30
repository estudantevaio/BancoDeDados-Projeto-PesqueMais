import classes.VendaBanco;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaRelatorio extends javax.swing.JFrame {

    private JTable tblRelatorio;
    private JScrollPane scroll;
    private JButton btnAtualizar;
    private JLabel lblTitulo;

    private VendaBanco banco = new VendaBanco();

    public TelaRelatorio() {
        initComponents();
        carregarRelatorio();
    }

    private void initComponents() {
        setTitle("Relatório de Vendas - Pesque Mais");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        lblTitulo = new JLabel("Relatório de Vendas - Pesque Mais: Cliente - Item - Marca - Categoria - Preço - Quantidade - Total");
        lblTitulo.setBounds(20, 15, 900, 25);
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 14));
        add(lblTitulo);

        btnAtualizar = new JButton("Atualizar Relatório");
        btnAtualizar.setBounds(20, 50, 200, 32);
        btnAtualizar.addActionListener(e -> carregarRelatorio());
        add(btnAtualizar);

        tblRelatorio = new JTable();
        tblRelatorio.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Cliente", "Item", "Marca", "Categoria", "Preço", "Quantidade", "Total"}));
        scroll = new JScrollPane(tblRelatorio);
        scroll.setBounds(20, 95, 950, 450);
        add(scroll);
    }

    private void carregarRelatorio() {
        DefaultTableModel modelo = (DefaultTableModel) tblRelatorio.getModel();
        modelo.setRowCount(0);
        // getRelatorio() retorna List<Object[]> já pronto da VIEW do banco. 
        List<Object[]> linhas = banco.getRelatorio();
        if (linhas == null) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o relatório. ", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Object[] linha : linhas) {
            modelo.addRow(linha);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TelaRelatorio().setVisible(true));
    }
}