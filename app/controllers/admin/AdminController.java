package controllers.admin;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Usuario;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class AdminController extends Controller {

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
     * show inicio page
     *
     * @return inicio page if user auth
     */
    public Result inicio() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        String username = usuarioAtual.getEmail();

        //verificar se o usuario atual encontrado é administrador caso nao retorna a pagina de nao autorizado
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        return ok(views.html.admin.inicio.render(username));
    }
}
