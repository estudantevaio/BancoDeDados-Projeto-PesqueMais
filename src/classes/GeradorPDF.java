package classes;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.List;

public class GeradorPDF {

    public static void gerarComprovante(Venda venda, List<ItemVenda> itens) {
        Document document = new Document();
        try {
            // Cria o arquivo "comprovante_venda_ID.pdf" na pasta do projeto. 
            String nomeArquivo = "comprovante_venda_" + venda.getId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            
            document.open();
            
            // Cabeçalho. 
            document.add(new Paragraph("=== COMPROVANTE DE VENDA ==="));
            document.add(new Paragraph("Cliente: " + venda.getNomeCliente()));
            document.add(new Paragraph("Data: " + venda.getDataVenda()));
            document.add(new Paragraph(" ")); // Linha em branco
            
            // Itens da venda. 
            document.add(new Paragraph("--- ITENS ---"));
            for (ItemVenda item : itens) {
                document.add(new Paragraph(
                    item.getNomeProduto() + " | Qtd: " + item.getQuantidade() + 
                    " | Unit: R$ " + item.getPrecoUnitario() + 
                    " | Subtotal: R$ " + item.getSubtotal()
                ));
            }
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TOTAL: R$ " + venda.getTotal()));
            
            document.close();
            System.out.println("PDF gerado com sucesso: " + nomeArquivo);
            
        } catch (Exception e) {
            System.out.println("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}