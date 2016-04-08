package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Token extends Model {

    // Reset tokens will expire after a day.
    private static final int EXPIRATION_DAYS = 1;

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

    private enum TypeToken {
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

}
