import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EnviarCorreoTest {

    public static void main(String[] args) {

        // 1. Configurar las propiedades del servidor SMTP de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // Puerto TLS
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Requerido para TLS

        // Datos de autenticación
        final String usuario = "maxibruni01@gmail.com";
        final String contraseniaApp = "stcm ghdo lbxa iilu"; // Tu contraseña de aplicación

        // 2. Crear la sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, contraseniaApp);
            }
        });

        try {
            // 3. Crear el mensaje de correo
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usuario));

            // Destinatario
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("destinatario@correo.com")
            );

            // Asunto y cuerpo (puedes usar HTML)
            message.setSubject("Prueba de correo desde Java y Gmail");
            message.setContent("<h1>Hola!</h1><p>Este es un correo de prueba enviado desde <b>Java</b> usando SMTP de Gmail.</p>", "text/html; charset=utf-8");

            // 4. Enviar el correo
            Transport.send(message);

            System.out.println("¡El correo electrónico se envió correctamente!");

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

