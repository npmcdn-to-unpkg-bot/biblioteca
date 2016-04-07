package models;

import com.avaje.ebean.Model;
import play.Configuration;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.annotation.Nullable;
import javax.inject.Inject;
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

    @Inject
    MailerClient mailerClient;

    // Reset tokens will expire after a day.
    private static final int EXPIRATION_DAYS = 1;
    
    private enum TypeToken {
        password("reset"), email("email");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }

    }

    @Id
    private String token;

    @Constraints.Required
    @Formats.NonEmpty
    private Long usuarioId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    private TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreation;

    @Constraints.Required
    @Formats.NonEmpty
    private String email;

    // -- Queries
    @SuppressWarnings("unchecked")
    public Finder<String, Token> find = new Finder(String.class, Token.class);

    /**
     * Retrieve a token by id and type.
     *
     * @param token token Id
     * @param type  type of token
     * @return a resetToken
     */
    public Token findByTokenAndType(String token, TypeToken type) {
        return find.where().eq("token", token).eq("type", type).findUnique();
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

}
