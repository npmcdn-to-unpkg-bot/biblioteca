package controllers.senha;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import models.Token;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.MalformedURLException;

public class SenhaController extends Controller {

    @Inject
    MailerClient mailerClient;

    /**
     * @return a object user authenticated
     */
    private Usuario atual() {
        String username = session().get("email");

        //retorna o usuário atual que esteja logado no sistema
        return Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();
    }

    /**
     * Display the reset password form.
     *
     * @return reset password form
     */
    public Result resetTela() {
        return ok(views.html.senha.reset.render());
    }

    /**
     * Send a mail with the reset link.
     *
     * @return password page with flash error or success
     */
    public Result runPassword() throws EmailException, MalformedURLException {

        Usuario usuario = atual();

        try {
            Token t = new Token();
            t.sendMailResetPassword(usuario,mailerClient);
            //flash("success", Messages.get("resetpassword.mailsent"));
            return ok();
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            //flash("error", Messages.get("error.technical"));
        }
        return badRequest();
    }
}
