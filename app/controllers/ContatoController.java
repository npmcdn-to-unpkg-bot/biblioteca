package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Contato;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class ContatoController extends Controller {

    @Inject
    MailerClient mailerClient;

    /**
     * @return a object user authenticated
     */
    private Usuario atual() {
        String username = session().get("email");

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        return usuarioAtual;
    }

    /**
     * Save a contact
     *
     * @return a contatc json
     */
    public Result inserir() {

        Contato contato = Json.fromJson(request().body().asJson(), Contato.class);

        contato.setDataCadastro(new Date());

        try {
            Ebean.save(contato);
            enviarEmail(contato);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest("Erro interno de sistema");
        }

        return created(Json.toJson(contato));
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

        return ok(Json.toJson(Ebean.find(Contato.class).findList()));
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

        Query<Contato> query = Ebean.createQuery(Contato.class, "find contato where (email like :email or nome like :nomeContato)");
        query.setParameter("email", "%" + filtro + "%");
        query.setParameter("nomeContato", "%" + filtro + "%");
        List<Contato> filtroDeContatos = query.findList();

        return ok(Json.toJson(filtroDeContatos));
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

        //busca o contato
        Contato contato = Ebean.find(Contato.class, id);

        if (contato == null) {
            return notFound("Contato não encontrado");
        }

        return ok(Json.toJson(contato));
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

        //busca o contato para ser excluido
        Contato contato = Ebean.find(Contato.class, id);

        if (contato == null) {
            return notFound("Contato não encontrado");
        }

        try {
            Ebean.delete(contato);
        }  catch (Exception e) {
            Logger.info(e.getMessage());
            return badRequest("Erro interno de sistema.");
        }

        return ok(Json.toJson(contato));
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
