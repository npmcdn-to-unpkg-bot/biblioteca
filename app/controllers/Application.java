package controllers;


import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Usuario;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Application extends Controller {

    /**
     * show index page
     *
     * @return index page if user auth or not auth
     */
    public Result index() {
        String username = session().get("email");

        Integer privilegio = 0;

        if (username == null) {
            return ok(views.html.index.render(username, privilegio));
        }

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        if (usuarioAtual == null) {
            return ok(views.html.index.render(username, privilegio));
        }

        privilegio = usuarioAtual.getPrivilegio();

        return ok(views.html.index.render(username, privilegio));

    }

    /**
     * show version of play page
     *
     * @return version page if user auth or not auth
     */
    @Security.Authenticated(Secured.class)
    public Result sobre() {
        return ok(views.html.admin.sobre.render(play.core.PlayVersion.current()));
    }

}
