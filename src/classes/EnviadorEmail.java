package classes;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EnviadorEmail {

    private static final String REMETENTE = "faculdade0003@gmail.com";
    private static final String SENHA_APP = "jgqm iwzw nsww mabe";

    public static void enviar(String destinatario, String assunto, String corpo, String caminhoAnexo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); 

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, SENHA_APP);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);

            // Parte 1: Corpo do e-mail. 
            MimeBodyPart corpoPart = new MimeBodyPart();
            corpoPart.setText(corpo);

            // Parte 2: Anexo (o PDF). 
            MimeBodyPart anexoPart = new MimeBodyPart();
            if (caminhoAnexo != null && !caminhoAnexo.isEmpty()) {
                anexoPart.attachFile(caminhoAnexo); // Anexa o arquivo PDF. 
            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(corpoPart);
            if (caminhoAnexo != null && !caminhoAnexo.isEmpty()) {
                multipart.addBodyPart(anexoPart);
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("O e-mail foi enviado com sucesso para: " + destinatario);

        } catch (Exception e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }
}