package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Usuario;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Security;

import javax.annotation.Nullable;

@Security.Authenticated(Secured.class)
public class EventoController extends Controller {

    /**
     * @return a object user authenticated
     */
    @Nullable
    private Usuario atual() {
        String username = session().get("email");

        try {
            //retorna o usuário atual que esteja logado no sistema
            return Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                    .setParameter("email", username)
                    .findUnique();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return null;
        }
    }


}
