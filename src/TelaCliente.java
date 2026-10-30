import classes.Cliente;
import classes.ClienteBanco;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaCliente extends javax.swing.JFrame {

    // Componentes. 
    private JLabel lblNome, lblTelefone, lblEmail;
    private JTextField txtNome, txtTelefone, txtEmail;
    private JButton btnNovo, btnSalvar, btnAlterar, btnExcluir;
    private JTable tblCliente;
    private JScrollPane scroll;

    // Estado da tela. 
    private ClienteBanco banco = new ClienteBanco();
    private List<Cliente> clientes;
    private int idSelecionado = 0;  // 0 significa "Nenhum selecionado".

    public TelaCliente() {
        initComponents();
        carregarTabela();  // Já carrega os clientes ao abrir. 
    }

    private void initComponents() {
        setTitle("Cadastro de Clientes");
        setSize(720, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fecha só essa janela. 
        setLayout(null);

        // ----- Campos -----
        lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 80, 25);
        add(lblNome);
        txtNome = new JTextField();
        txtNome.setBounds(110, 20, 580, 25);
        add(txtNome);

        lblTelefone = new JLabel("Telefone:");
        lblTelefone.setBounds(20, 55, 80, 25);
        add(lblTelefone);
        txtTelefone = new JTextField();
        txtTelefone.setBounds(110, 55, 250, 25);
        add(txtTelefone);

        lblEmail = new JLabel("E-mail:");
        lblEmail.setBounds(380, 55, 60, 25);
        add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(440, 55, 250, 25);
        add(txtEmail);

        // ----- Botões CRUD -----
        btnNovo = new JButton("Novo");
        btnNovo.setBounds(20, 100, 120, 30);
        btnNovo.addActionListener(e -> limparFormulario());
        add(btnNovo);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(150, 100, 120, 30);
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(280, 100, 120, 30);
        btnAlterar.addActionListener(e -> alterar());
        add(btnAlterar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(410, 100, 120, 30);
        btnExcluir.addActionListener(e -> excluir());
        add(btnExcluir);

        // ----- Tabela -----
        tblCliente = new JTable();
        tblCliente.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Nome", "Telefone", "E-mail"}));
        // Ao clicar em uma linha, os dados aparecem nos respectivos campos.
        tblCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selecionarLinha();
            }
        });
        scroll = new JScrollPane(tblCliente);
        scroll.setBounds(20, 150, 670, 340);
        add(scroll);
    }

    // READ: Preenche a tabela com os clientes do banco. 
    private void carregarTabela() {
        DefaultTableModel modelo = (DefaultTableModel) tblCliente.getModel();
        modelo.setRowCount(0);  // Limpa a tabela antes de recarregar. 
        clientes = banco.getClientes();
        if (clientes == null) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Cliente c : clientes) {
            modelo.addRow(c.obterDados());  // Usa obterDados() do modelo. 
        }
    }

    // Ao clicar em uma linha, os dados aparecem nos respectivos campos. 
    private void selecionarLinha() {
        int linha = tblCliente.getSelectedRow();
        if (linha >= 0 && linha < clientes.size()) {
            Cliente c = clientes.get(linha);
            idSelecionado = c.getId();
            txtNome.setText(c.getNome());
            txtTelefone.setText(c.getTelefone());
            txtEmail.setText(c.getEmail());
        }
    }

    // CREATE: Salva um novo cliente. 
    private void salvar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = new Cliente();
        c.setNome(txtNome.getText());
        c.setTelefone(txtTelefone.getText());
        c.setEmail(txtEmail.getText());
        banco.inserir(c);
        carregarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Cliente cadastrado.");
    }

    // UPDATE: Altera o cliente selecionado. 
    private void alterar() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = new Cliente();
        c.setId(idSelecionado);
        c.setNome(txtNome.getText());
        c.setTelefone(txtTelefone.getText());
        c.setEmail(txtEmail.getText());
        banco.editar(c);
        carregarTabela();
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Cliente alterado.");
    }

    // DELETE: Exclui o cliente selecionado. 
    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "Excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            banco.excluir(idSelecionado);
            carregarTabela();
            limparFormulario();
        }
    }

    private void limparFormulario() {
        idSelecionado = 0;
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        tblCliente.clearSelection();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TelaCliente().setVisible(true));
    }
}