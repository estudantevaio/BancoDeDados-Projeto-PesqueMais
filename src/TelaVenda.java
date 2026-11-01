import classes.Cliente;
import classes.ClienteBanco;
import classes.EnviadorEmail;
import classes.GeradorPDF;
import classes.ItemVenda;
import classes.Produto;
import classes.ProdutoBanco;
import classes.Venda;
import classes.VendaBanco;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaVenda extends javax.swing.JFrame {

    // ----- Componentes -----
    private JLabel lblCliente, lblProduto, lblQtd, lblTotalTitulo, lblTotalValor, lblItens, lblVendas;
    private JComboBox<String> cmbCliente, cmbProduto;
    private JTextField txtQtd;
    private JButton btnAdicionarItem, btnRemoverItem;
    private JButton btnNova, btnSalvar, btnAlterar, btnExcluir;
    private JTable tblItens, tblVendas;
    private JScrollPane scrollItens, scrollVendas;

    // ----- Bancos -----
    private VendaBanco banco = new VendaBanco();
    private ClienteBanco clienteBanco = new ClienteBanco();
    private ProdutoBanco produtoBanco = new ProdutoBanco();

    // ----- Estado (dados em memória) -----
    private List<Cliente> clientes;
    private List<Produto> produtos;
    private List<Venda> vendas;

    // Carrinho da venda em edição. 
    private ArrayList<ItemVenda> itensEdicao = new ArrayList<>();

    private int idVendaSelecionada = 0;   
    private int idItemSelecionado = 0;

    public TelaVenda() {
        initComponents();
        carregarCombos();
        carregarVendas();
    }

    // ============================================================
    // MONTAGEM DA TELA
    // ============================================================
    private void initComponents() {
        setTitle("PesqueMais - Registro de Vendas");
        setSize(950, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // ----- Linha 1: cliente + total -----
        lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(20, 20, 60, 25);
        add(lblCliente);

        cmbCliente = new JComboBox<>();
        cmbCliente.setBounds(80, 20, 350, 25);
        add(cmbCliente);

        lblTotalTitulo = new JLabel("Total da Venda:");
        lblTotalTitulo.setBounds(500, 20, 110, 25);
        add(lblTotalTitulo);

        lblTotalValor = new JLabel("R$ 0,00");
        lblTotalValor.setBounds(610, 20, 200, 25);
        lblTotalValor.setFont(new java.awt.Font("Arial", 1, 16));
        lblTotalValor.setForeground(new java.awt.Color(0, 100, 0));
        add(lblTotalValor);

        // ----- Linha 2: produto + quantidade + botões de item -----
        lblProduto = new JLabel("Produto:");
        lblProduto.setBounds(20, 55, 60, 25);
        add(lblProduto);

        cmbProduto = new JComboBox<>();
        cmbProduto.setBounds(80, 55, 400, 25);
        add(cmbProduto);

        lblQtd = new JLabel("Qtd:");
        lblQtd.setBounds(490, 55, 40, 25);
        add(lblQtd);

        txtQtd = new JTextField("1");
        txtQtd.setBounds(530, 55, 60, 25);
        add(txtQtd);

        btnAdicionarItem = new JButton("Adicionar Item");
        btnAdicionarItem.setBounds(600, 55, 150, 25);
        btnAdicionarItem.addActionListener(e -> adicionarItem());
        add(btnAdicionarItem);

        btnRemoverItem = new JButton("Remover Item");
        btnRemoverItem.setBounds(760, 55, 150, 25);
        btnRemoverItem.addActionListener(e -> removerItem());
        add(btnRemoverItem);

        // ----- Tabela de itens da venda em edição -----
        lblItens = new JLabel("Itens da venda em edição:");
        lblItens.setBounds(20, 90, 300, 25);
        add(lblItens);

        tblItens = new JTable();
        tblItens.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Produto", "Qtd", "Preço Unit.", "Subtotal"}));
        tblItens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int linha = tblItens.getSelectedRow();
                if (linha >= 0 && linha < itensEdicao.size()) {
                    idItemSelecionado = linha;
                }
            }
        });
        scrollItens = new JScrollPane(tblItens);
        scrollItens.setBounds(20, 115, 900, 200);
        add(scrollItens);

        // ----- Botões de ação da venda -----
        btnNova = new JButton("Nova Venda");
        btnNova.setBounds(20, 325, 130, 32);
        btnNova.addActionListener(e -> novaVenda());
        add(btnNova);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(160, 325, 130, 32);
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(300, 325, 130, 32);
        btnAlterar.addActionListener(e -> alterar());
        add(btnAlterar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(440, 325, 130, 32);
        btnExcluir.addActionListener(e -> excluir());
        add(btnExcluir);
        
        // --- Botão para gerar PDF ---
        JButton btnGerarPDF = new JButton("Gerar Comprovante PDF");
        btnGerarPDF.setBounds(580, 325, 190, 32);
        btnGerarPDF.addActionListener(e -> gerarPDF());
        add(btnGerarPDF);
        
        // --- Botão para enviar por e-mail ---
        JButton btnEnviarEmail = new JButton("Enviar por E-mail");
        btnEnviarEmail.setBounds(780, 325, 140, 32); 
        btnEnviarEmail.addActionListener(e -> enviarEmail());
        add(btnEnviarEmail);

        // ----- Tabela de vendas cadastradas (histórico) -----
        lblVendas = new JLabel("Vendas cadastradas (clique para editar/excluir):");
        lblVendas.setBounds(20, 375, 500, 25);
        add(lblVendas);

        tblVendas = new JTable();
        tblVendas.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Cliente", "Data", "Total"}));
        tblVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selecionarVenda();
            }
        });
        scrollVendas = new JScrollPane(tblVendas);
        scrollVendas.setBounds(20, 400, 900, 260);
        add(scrollVendas);
    }

    // ============================================================
    // CARREGAR COMBOS (cliente e produto)
    // ============================================================
    private void carregarCombos() {
        cmbCliente.removeAllItems();
        cmbCliente.addItem("-- Selecione um cliente --");
        clientes = clienteBanco.getClientes();
        if (clientes != null) {
            for (Cliente c : clientes) {
                cmbCliente.addItem(c.getId() + " - " + c.getNome());
            }
        }

        cmbProduto.removeAllItems();
        cmbProduto.addItem("-- Selecione um produto --");
        produtos = produtoBanco.getProdutos();
        if (produtos != null) {
            for (Produto p : produtos) {
                cmbProduto.addItem(p.getId() + " - " + p.getNome() + " (R$ " + p.getPreco() + ")");
            }
        }
    }

    // ============================================================
    // CARREGAR TABELA DE VENDAS JÁ CADASTRADAS
    // ============================================================
    private void carregarVendas() {
        DefaultTableModel modelo = (DefaultTableModel) tblVendas.getModel();
        modelo.setRowCount(0);
        vendas = banco.getVendas();
        if (vendas == null) return;
        for (Venda v : vendas) {
            modelo.addRow(v.obterDados());
        }
    }

    // ============================================================
    // ADICIONAR ITEM AO CARRINHO
    // ============================================================
        private void adicionarItem() {
        if (cmbCliente.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbProduto.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qtd;
        try {
            qtd = Integer.parseInt(txtQtd.getText().trim());
            if (qtd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um número inteiro positivo.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto p = produtos.get(cmbProduto.getSelectedIndex() - 1);

        // Valida o estoque antes de adicionar. 
        int qtdJaNoCarrinho = 0;
        for (ItemVenda iv : itensEdicao) {
            if (iv.getIdProduto() == p.getId()) {
                qtdJaNoCarrinho += iv.getQuantidade();
            }
        }
        int qtdTotal = qtdJaNoCarrinho + qtd;
        if (qtdTotal > p.getEstoque()) {
            JOptionPane.showMessageDialog(this,
                "Estoque insuficiente para '" + p.getNome() + "'.\n" +
                "Disponível: " + p.getEstoque() + "\n" +
                "Já no carrinho: " + qtdJaNoCarrinho + "\n" +
                "Você está pedindo: " + qtd,
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemVenda item = new ItemVenda();
        item.setIdProduto(p.getId());
        item.setNomeProduto(p.getNome());
        item.setQuantidade(qtd);
        item.setPrecoUnitario(p.getPreco());
        item.setSubtotal(p.getPreco() * qtd);

        itensEdicao.add(item);
        DefaultTableModel modelo = (DefaultTableModel) tblItens.getModel();
        modelo.addRow(item.obterDados());

        atualizarTotal();
        txtQtd.setText("1");
    }

    // ============================================================
    // REMOVER ITEM SELECIONADO DO CARRINHO
    // ============================================================
    private void removerItem() {
        int linha = tblItens.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        itensEdicao.remove(linha);
        ((DefaultTableModel) tblItens.getModel()).removeRow(linha);
        atualizarTotal();
    }

    // ============================================================
    // RECALCULA E EXIBE O TOTAL
    // ============================================================
    private void atualizarTotal() {
        double total = 0;
        for (ItemVenda item : itensEdicao) {
            total += item.getSubtotal();
        }
        lblTotalValor.setText(String.format("R$ %.2f", total));
    }

    // ============================================================
    // LIMPAR TUDO (venda nova)
    // ============================================================
    private void novaVenda() {
        idVendaSelecionada = 0;
        itensEdicao.clear();
        ((DefaultTableModel) tblItens.getModel()).setRowCount(0);
        cmbCliente.setSelectedIndex(0);
        cmbProduto.setSelectedIndex(0);
        txtQtd.setText("1");
        lblTotalValor.setText("R$ 0,00");
        tblVendas.clearSelection();
    }

    // ============================================================
    // SELECIONAR VENDA NA TABELA (carrega itens para edição)
    // ============================================================
    private void selecionarVenda() {
        int linha = tblVendas.getSelectedRow();
        if (linha < 0 || linha >= vendas.size()) return;
        Venda v = vendas.get(linha);
        idVendaSelecionada = v.getId();

        // Seleciona o cliente no combo pelo ID. 
        for (int i = 1; i < cmbCliente.getItemCount(); i++) {
            String item = cmbCliente.getItemAt(i);
            if (item.startsWith(v.getIdCliente() + " - ")) {
                cmbCliente.setSelectedIndex(i);
                break;
            }
        }

        // Carrega os itens da venda. 
        itensEdicao.clear();
        DefaultTableModel modelo = (DefaultTableModel) tblItens.getModel();
        modelo.setRowCount(0);
        List<ItemVenda> itensDoBanco = banco.getItensVenda(idVendaSelecionada);
        if (itensDoBanco != null) {
            for (ItemVenda iv : itensDoBanco) {
                itensEdicao.add(iv);
                modelo.addRow(iv.obterDados());
            }
        }
        atualizarTotal();
    }

    // ============================================================
    // CREATE — Salvar nova venda
    // ============================================================
        private void salvar() {
        if (idVendaSelecionada != 0) {
            JOptionPane.showMessageDialog(this, "Você está editando uma venda existente. Use 'Alterar' ou 'Nova Venda'.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbCliente.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensEdicao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item à venda.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente c = clientes.get(cmbCliente.getSelectedIndex() - 1);
        Venda v = new Venda();
        v.setIdCliente(c.getId());
        v.setTotal(calcularTotal());

        try {
            banco.inserir(v, itensEdicao);
            carregarVendas();
            carregarCombos();   // Atualiza o estoque exibido nos combos. 
            novaVenda();
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao registrar venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // UPDATE — Alterar venda selecionada
    // ============================================================
        private void alterar() {
        if (idVendaSelecionada == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para alterar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbCliente.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensEdicao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item à venda.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente c = clientes.get(cmbCliente.getSelectedIndex() - 1);
        Venda v = new Venda();
        v.setId(idVendaSelecionada);
        v.setIdCliente(c.getId());
        v.setTotal(calcularTotal());

        try {
            banco.editar(v, itensEdicao);
            carregarVendas();
            carregarCombos();
            novaVenda();
            JOptionPane.showMessageDialog(this, "Venda alterada com sucesso!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao alterar venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // DELETE — Excluir venda selecionada
    // ============================================================
        private void excluir() {
        if (idVendaSelecionada == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "Excluir esta venda e todos os seus itens? O estoque será devolvido.", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                banco.excluir(idVendaSelecionada);
                carregarVendas();
                carregarCombos();
                novaVenda();
                JOptionPane.showMessageDialog(this, "Venda excluída. Estoque devolvido.");
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao excluir venda", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Utilitário: Soma todos os subtotais. 
    private double calcularTotal() {
        double t = 0;
        for (ItemVenda iv : itensEdicao) t += iv.getSubtotal();
        return t;
    }
    
    // ============================================================
    // GERAÇÃO DE PDF
    // ============================================================
    private void gerarPDF() {
        // Verifica se uma venda foi selecionada na tabela. 
        if (idVendaSelecionada == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para gerar o comprovante.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Encontra o objeto Venda correspondente ao ID selecionado. 
        Venda vendaParaPDF = null;
        for (Venda v : vendas) {
            if (v.getId() == idVendaSelecionada) {
                vendaParaPDF = v;
                break;
            }
        }

        if (vendaParaPDF == null) {
            JOptionPane.showMessageDialog(this, "Erro ao localizar os dados da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Busca a lista de itens atualizada do banco. 
        List<ItemVenda> itensParaPDF = banco.getItensVenda(idVendaSelecionada);
    
        // Chama o gerador. 
        if (itensParaPDF != null) {
            GeradorPDF.gerarComprovante(vendaParaPDF, itensParaPDF);
            JOptionPane.showMessageDialog(this, "Comprovante PDF gerado na pasta do projeto!");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao carregar os itens da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ============================================================
    // ENVIO DE E-MAIL
    // ============================================================
    private void enviarEmail() {
        // 1. Verifica se uma venda está selecionada. 
        if (idVendaSelecionada == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para enviar o comprovante.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Pede o e-mail do destinatário. 
        String emailDestinatario = JOptionPane.showInputDialog(this, "Digite o e-mail do destinatário:");
        if (emailDestinatario == null || emailDestinatario.trim().isEmpty()) {
            return; // Usuário cancelou ou não digitou nada. 
        }

        // 3. Encontra os dados da venda selecionada. 
        Venda vendaParaEmail = null;
        for (Venda v : vendas) {
            if (v.getId() == idVendaSelecionada) {
                vendaParaEmail = v;
                break;
            }
        }
        if (vendaParaEmail == null) {
            JOptionPane.showMessageDialog(this, "Erro ao localizar os dados da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Define o caminho do anexo PDF (o mesmo gerado anteriormente). 
        String caminhoAnexo = "comprovante_venda_" + vendaParaEmail.getId() + ".pdf";

        // 5. Chama o enviador. 
        try {
            EnviadorEmail.enviar(emailDestinatario, "PesqueMais - Comprovante de Compra", "Olá. \n\nSegue em anexo o comprovante da sua compra.", caminhoAnexo);
            JOptionPane.showMessageDialog(this, "E-mail enviado com sucesso para " + emailDestinatario);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao enviar e-mail: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TelaVenda().setVisible(true));
    }
}
