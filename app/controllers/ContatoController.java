package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Contato;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.i18n.Messages;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class ContatoController extends Controller {

    @Inject
    MailerClient mailerClient;

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
     * Save a contact
     *
     * @return a contatc json
     */
    public Result inserir() {

        try {
            Contato contato = Json.fromJson(request().body().asJson(), Contato.class);

            contato.setDataCadastro(new Date());

            Ebean.save(contato);

            enviarEmail(contato);

            return created(Json.toJson(contato));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Messages.get("app.error"));
        }

    }

    /**
     * Retrieve a list of all contacts
     *
     * @return a list of all contacts in json
     */
    @Security.Authenticated(Secured.class)
    public Result buscaTodos() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        try {
            return ok(Json.toJson(Ebean.find(Contato.class).findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * Retrieve a list of contacts from a filter
     *
     * @param filtro
     * @return a list of filter contacts in json
     */
    @Security.Authenticated(Secured.class)
    public Result filtra(String filtro) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador.");
        }

        try {
            Query<Contato> query = Ebean.createQuery(Contato.class, "find contato where (email like :email or nome like :nomeContato)");
            query.setParameter("email", "%" + filtro + "%");
            query.setParameter("nomeContato", "%" + filtro + "%");
            List<Contato> filtroDeContatos = query.findList();

            return ok(Json.toJson(filtroDeContatos));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * Retrieve a contact from id
     *
     * @param id
     * @return a contact json
     */
    @Security.Authenticated(Secured.class)
    public Result buscaPorId(Long id) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        try {
            //busca o contato
            Contato contato = Ebean.find(Contato.class, id);

            if (contato == null) {
                return notFound("Contato não encontrado");
            }

            return ok(Json.toJson(contato));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * Remove a contact from a id
     *
     * @param id
     * @return ok contact on json
     */
    @Security.Authenticated(Secured.class)
    public Result remover(Long id) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        try {
            //busca o contato para ser excluido
            Contato contato = Ebean.find(Contato.class, id);

            if (contato == null) {
                return notFound("Contato não encontrado");
            }

            Ebean.delete(contato);
            return ok(Json.toJson(contato));
        }  catch (Exception e) {
            Logger.info(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * Send the confirm mail.
     *
     * @param contato created
     * @throws EmailException Exception when sending mail
     */
    private void enviarEmail(Contato contato) throws EmailException {
        String emailContatoBody = views.html.email.emailContatoBody.render(contato).body();
        try {
            Email emailUser = new Email()
                    .setSubject(contato.getAssunto())
                    .setFrom(contato.getEmail())
                    .addTo("Biblioteca CIBiogás <haroldoramirez@gmail.com>")
                    .setBodyHtml(emailContatoBody);
            mailerClient.send(emailUser);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }
}
