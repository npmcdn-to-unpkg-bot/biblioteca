package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import jsmessages.JsMessages;
import jsmessages.JsMessagesFactory;
import jsmessages.japi.Helper;
import models.Usuario;
import play.Logger;
import play.Play;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class Application extends Controller {


    private final JsMessages jsMessages;

    @Inject
    public Application(JsMessagesFactory jsMessagesFactory) {
        jsMessages = jsMessagesFactory.all();
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

    /**
     * responsavel por enviar a message correta atraves do contexto ou seja envia todos os messages necessarios para utilizar no template do angular
     */
    public Result jsMessages() {
        return ok(jsMessages.apply(Scala.Option("window.Messages"), Helper.messagesFromCurrentHttpContext()));
    }

    /**
     * responsavel por modificar o idioma da aplicacao
     */
    public Result mudaIdioma(){
        String lang = request().getQueryString("lang");
        response().setCookie(Play.langCookieName(),lang);
        return redirect(routes.Application.index());
    }

    /**
     * show index page
     *
     * @return index page if user auth or not auth
     */
    public Result index() {
        String username = session().get("email");

        Integer privilegio = 0;

        if (username == null) {
            username = "";
            return ok(views.html.index.render(username, privilegio));
        }

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return ok(views.html.index.render(username, privilegio));
        }

        privilegio = usuarioAtual.getPrivilegio();

        return ok(views.html.index.render(username, privilegio));

    }

}
