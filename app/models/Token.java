package models;

import com.avaje.ebean.Model;
import org.apache.commons.mail.EmailException;
import play.Configuration;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Token extends Model {

    // Reset tokens will expire after a day.
    private static final int EXPIRATION_DAYS = 1;

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public Long usuarioId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    public enum TypeToken {
        password("reset"), email("email");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }

    }

    /**
     * @return true if the reset token is too old to use, false otherwise.
     */
    public boolean isExpired() {
        return dateCreation != null && dateCreation.before(expirationTime());
    }

    /**
     * @return a date before which the password link has expired.
     */
    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION_DAYS);
        return cal.getTime();
    }

    /**
     * Return a new Token.
     *
     * @param usuario 
     * @param type  type of token
     * @param email email for a token change email
     * @return a reset token
     */
    private Token getNewToken(Usuario usuario, TypeToken type, String email) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.usuarioId = usuario.getId();
        token.type = type;
        token.email = email;
        token.save();
        return token;
    }

    /**
     * Retrieve a token by id and type.
     *
     * @param token token Id
     * @param type  type of token
     * @return a resetToken
     */
    public static Token findByTokenAndType(String token, TypeToken type) {
        return find.where().eq("token", token).eq("type", type).findUnique();
    }

    // -- Queries
    public static Finder<String, Token> find = new Finder<String, Token>(Token.class);

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user the current user
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public void sendMailResetPassword(Usuario user, MailerClient mc) throws EmailException, MalformedURLException {
        sendMail(user, TypeToken.password, null, mc);
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param usuario  the current user
     * @param type  token type
     * @param email email for a change email token
     * @throws java.net.MalformedURLException if token is wrong.
     */
    private void sendMail(Usuario usuario, TypeToken type, String email, MailerClient mc) throws EmailException, MalformedURLException {

        Token token = getNewToken(usuario, type, email);
        String externalServer = Configuration.root().getString("server.hostname");

        String subject = null;
        String message = null;
        String toMail = null;

        // Should use reverse routing here.
        String urlString = urlString = "http://" + externalServer + "/" + type.urlPath + "/" + token.token;
        URL url = new URL(urlString); // validate the URL

        switch (type) {
            case password:
                subject = Messages.get("mail.reset.ask.subject");
                message = Messages.get("mail.reset.ask.message", url.toString());
                toMail = usuario.getEmail();
                break;
            case email:
                subject = Messages.get("mail.change.ask.subject");
                message = Messages.get("mail.change.ask.message", url.toString());
                toMail = token.email; // == email parameter
                break;
        }

        String emailBody = views.html.email.emailSenhaBody.render(usuario,url.toString()).body();
        try {
            Email emailUser = new Email()
                    .setSubject("Cadastro na Biblioteca")
                    .setFrom("Biblioteca CIBiogás <biblioteca@email.com>")
                    .addTo(usuario.getEmail())
                    .setBodyHtml(emailBody);
            mc.send(emailUser);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

}
