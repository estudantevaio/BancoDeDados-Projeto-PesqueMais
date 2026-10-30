import classes.Produto;
import classes.ProdutoBanco;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class TelaProduto extends javax.swing.JFrame {

    // Componentes. 
    private JLabel lblNome, lblMarca, lblCategoria, lblPreco, lblEstoque, lblFoto;
    private JTextField txtNome, txtMarca, txtCategoria, txtPreco, txtEstoque;
    private JButton btnEscolherFoto, btnNovo, btnSalvar, btnAlterar, btnExcluir;
    private JTable tblProduto;
    private JScrollPane scroll;

    // Estado. 
    private ProdutoBanco banco = new ProdutoBanco();
    private List<Produto> produtos;
    private int idSelecionado = 0;
    private String fotoSelecionada = null;

    public TelaProduto() {
        initComponents();
        garantirPastaFotos();
        carregarTabela();
    }

    // Se a pasta "fotos" não existir, cria automaticamente. 
    private void garantirPastaFotos() {
        File pasta = new File("fotos");
        if (!pasta.exists()) pasta.mkdirs();
    }

    private void initComponents() {
        setTitle("Cadastro de Produtos - Pesque Mais");
        setSize(940, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // ----- Linha 1: nome + marca -----
        lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 60, 25);
        add(lblNome);
        txtNome = new JTextField();
        txtNome.setBounds(80, 20, 350, 25);
        add(txtNome);

        lblMarca = new JLabel("Marca:");
        lblMarca.setBounds(450, 20, 60, 25);
        add(lblMarca);
        txtMarca = new JTextField();
        txtMarca.setBounds(510, 20, 200, 25);
        add(txtMarca);

        // ----- Linha 2: categoria + preço + estoque -----
        lblCategoria = new JLabel("Categoria:");
        lblCategoria.setBounds(20, 55, 70, 25);
        add(lblCategoria);
        txtCategoria = new JTextField();
        txtCategoria.setBounds(90, 55, 150, 25);
        add(txtCategoria);

        lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(260, 55, 50, 25);
        add(lblPreco);
        txtPreco = new JTextField();
        txtPreco.setBounds(310, 55, 110, 25);
        add(txtPreco);

        lblEstoque = new JLabel("Estoque:");
        lblEstoque.setBounds(440, 55, 70, 25);
        add(lblEstoque);
        txtEstoque = new JTextField();
        txtEstoque.setBounds(510, 55, 100, 25);
        add(txtEstoque);

        // ----- Área da foto (canto direito) -----
        lblFoto = new JLabel();
        lblFoto.setBounds(730, 20, 180, 180);
        lblFoto.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        lblFoto.setHorizontalAlignment(JLabel.CENTER);
        lblFoto.setText("(sem foto)");
        add(lblFoto);

        btnEscolherFoto = new JButton("Escolher Foto");
        btnEscolherFoto.setBounds(730, 210, 180, 30);
        btnEscolherFoto.addActionListener(e -> escolherFoto());
        add(btnEscolherFoto);

        // ----- Botões CRUD -----
        btnNovo = new JButton("Novo");
        btnNovo.setBounds(20, 110, 110, 30);
        btnNovo.addActionListener(e -> limparFormulario());
        add(btnNovo);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(140, 110, 110, 30);
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(260, 110, 110, 30);
        btnAlterar.addActionListener(e -> alterar());
        add(btnAlterar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(380, 110, 110, 30);
        btnExcluir.addActionListener(e -> excluir());
        add(btnExcluir);

        // ----- Tabela -----
        tblProduto = new JTable();
        tblProduto.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Nome", "Marca", "Categoria", "Preço", "Estoque", "Foto"}));
        tblProduto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selecionarLinha();
            }
        });
        scroll = new JScrollPane(tblProduto);
        scroll.setBounds(20, 260, 890, 290);
        add(scroll);
    }

    // READ. 
    private void carregarTabela() {
        DefaultTableModel modelo = (DefaultTableModel) tblProduto.getModel();
        modelo.setRowCount(0);
        produtos = banco.getProdutos();
        if (produtos == null) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar os produtos. ", "Erro! ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Produto p : produtos) {
            modelo.addRow(p.obterDados());
        }
    }

    private void selecionarLinha() {
        int linha = tblProduto.getSelectedRow();
        if (linha >= 0 && linha < produtos.size()) {
            Produto p = produtos.get(linha);
            idSelecionado = p.getId();
            txtNome.setText(p.getNome());
            txtMarca.setText(p.getMarca());
            txtCategoria.setText(p.getCategoria());
            txtPreco.setText(String.valueOf(p.getPreco()));
            txtEstoque.setText(String.valueOf(p.getEstoque()));
            fotoSelecionada = p.getFoto();
            mostrarFoto(fotoSelecionada);   // Atualiza o JLabel com a imagem. 
        }
    }

    // Exibe a foto no JLabel, com a foto redimensionada para caber. 
    private void mostrarFoto(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.trim().isEmpty()) {
            lblFoto.setIcon(null);
            lblFoto.setText("(sem foto)");
            return;
        }
        File arq = new File("fotos/" + nomeArquivo);
        if (!arq.exists()) {
            lblFoto.setIcon(null);
            lblFoto.setText("(sem foto)");
            return;
        }
        ImageIcon icon = new ImageIcon(arq.getAbsolutePath());
        Image img = icon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(img));
        lblFoto.setText("");
    }

    // Abre o seletor de arquivos e copia a imagem para a pasta "fotos/". 
    private void escolherFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif"));
        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File origem = chooser.getSelectedFile();
            File destino = new File("fotos/" + origem.getName());
            try {
                Files.copy(origem.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                fotoSelecionada = origem.getName();
                mostrarFoto(fotoSelecionada);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao copiar a imagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // CREATE. 
    private void salvar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Produto p = new Produto();
            p.setNome(txtNome.getText());
            p.setMarca(txtMarca.getText());
            p.setCategoria(txtCategoria.getText());
            p.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            p.setEstoque(Integer.parseInt(txtEstoque.getText().trim()));
            p.setFoto(fotoSelecionada);   // A inclusão da foto é opcional. 
            banco.inserir(p);
            carregarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Produto cadastrado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e estoque devem ser números.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // UPDATE. 
    private void alterar() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Produto p = new Produto();
            p.setId(idSelecionado);
            p.setNome(txtNome.getText());
            p.setMarca(txtMarca.getText());
            p.setCategoria(txtCategoria.getText());
            p.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            p.setEstoque(Integer.parseInt(txtEstoque.getText().trim()));
            p.setFoto(fotoSelecionada);
            banco.editar(p);
            carregarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Produto alterado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e estoque devem ser números.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DELETE. 
    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "Excluir este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            banco.excluir(idSelecionado);
            carregarTabela();
            limparFormulario();
        }
    }

    private void limparFormulario() {
        idSelecionado = 0;
        fotoSelecionada = null;
        txtNome.setText("");
        txtMarca.setText("");
        txtCategoria.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
        lblFoto.setIcon(null);
        lblFoto.setText("(sem foto)");
        tblProduto.clearSelection();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TelaProduto().setVisible(true));
    }
}