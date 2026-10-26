package TPGrupalOO2Hito2Grupo04.services.implementation;

import TPGrupalOO2Hito2Grupo04.services.IMailService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service("mailService")
public class MailService implements IMailService {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    @Value("${mail.smtp.from}")
    private String fromAddress;

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    @Override
    public void enviarCorreoCredenciales(String destinatario, String usuario, String contrasenia) {
        Properties props = getMailProperties();

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Credenciales de acceso - Food Truck App");

            String htmlBody = String.format(
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2>Bienvenido/a!</h2>" +
                    "<p>Tu cuenta ha sido creada exitosamente. A continuación encontrarás tus credenciales de acceso:</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                    "<p><strong>Usuario:</strong> %s</p>" +
                    "<p><strong>Contraseña:</strong> %s</p>" +
                    "</div>" +
                    "<p>Te recomendamos cambiar tu contraseña después de tu primer inicio de sesión.</p>" +
                    "</body>" +
                    "</html>",
                    usuario, contrasenia
            );

            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Correo de credenciales enviado correctamente a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo de credenciales: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
