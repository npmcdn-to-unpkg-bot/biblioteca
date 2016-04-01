package controllers;

import actions.Secured;
import akka.util.Crypt;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Usuario;
import models.utils.Mail;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class UsuarioController extends Controller {

    @Inject
    MailerClient mailerClient;

    private static DynamicForm form = Form.form();

    /**
     * @return cadastrado form if register success
     */
    public Result telaCadastrado() {
        String username = session().get("email");
        return ok(views.html.cadastrado.render(username));
    }

    /**
     * @return cadastro form for register a new user
     */
    public Result telaCadastro() {
        return ok(views.html.cadastro.render(form));
    }

    /**
     * @return a object user authenticated
     */
    public Usuario atual() {
        String username = session().get("email");

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        return usuarioAtual;
    }

    /**
     * Save a user
     *
     * @return a user json
     */
    public Result inserir(){
        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String email = formPreenchido.data().get("email");
        String senha = Crypt.sha1(formPreenchido.data().get("confirm_senha"));
        String nome = formPreenchido.data().get("nome");

        //valida se o email e a senha não estejam vazios
        if (email.toString() == "" || senha.toString() == "") {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Email ou Senha não podem estar vazios!");
            return badRequest(views.html.login.render(formDeErro));
        }

        //faz uma busca na base de dados do usuario
        Usuario usuarioBusca = Ebean.find(Usuario.class).where().eq("email", formPreenchido.data().get("email")).findUnique();

        if (usuarioBusca != null) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Usuário já Cadastrado");
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        Usuario novo = new Usuario();
        novo.setEmail(email);
        novo.setSenha(senha);
        novo.setNome(nome);
        novo.setStatus(true);
        novo.setDataCadastro(new Date());
        novo.setPrivilegio(2);

        try {
            Ebean.save(novo);
        } catch (Exception e) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Erro interno de sistema!");
            Logger.info(e.getMessage());
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        String username = novo.getEmail();

        return ok(views.html.cadastrado.render(username));
    }

    /**
     * Retrieve a autenticated user
     *
     * @return a user json
     */
    @Security.Authenticated(Secured.class)
    public Result autenticado() {
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        return ok(Json.toJson(usuarioAtual));
    }

    /**
     * Update a user from id
     *
     * @param id
     * @return a user updated in json
     */
    @Security.Authenticated(Secured.class)
    public Result atualizar(Long id) {

        Usuario usuario = Json.fromJson(request().body().asJson(), Usuario.class);

        Usuario usuarioBusca = Ebean.find(Usuario.class, id);

        if (usuarioBusca == null) {
            return badRequest("Usuário não encontrado.");
        }

        try {
            String senha = Crypt.sha1(usuario.getSenha());
            usuario.setSenha(senha);
            usuario.setDataAlteracao(new Date());
            Ebean.update(usuario);
            Logger.info("Usuario atualizado.");
        } catch (Exception e) {
            Logger.info(e.getMessage());
            return badRequest("Erro interno de sistema!");
        }

        return ok(Json.toJson(usuario));
    }

    /**
     * Retrieve a user from id
     *
     * @param id
     * @return a user json
     */
    @Security.Authenticated(Secured.class)
    public Result buscaPorId(Long id) {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //busca o usuário para excluir
        Usuario usuario = Ebean.find(Usuario.class, id);

        if (usuario == null) {
            return notFound("Usuário não encontrado");
        }

        /**
         * @return badrequest if user authenticated email and user not a administrator. Special case
         * verifica se o email do usuario logado no sistema é o mesmo do buscado e se ele e administrador
         */
        if (!usuarioAtual.getEmail().equals(usuario.getEmail()) && (usuarioAtual.getPrivilegio() != 1)) {
            return badRequest("Não é possível realizar esta operação");
        }

        return ok(Json.toJson(usuario));
    }

    /**
     * Retrieve a list of all users
     *
     * @return a list of all users in json
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

        //busca todos os usuários menos o usuário padrão do sistema
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email != 'admin')");
        List<Usuario> filtroDeUsuarios = query.findList();

        return ok(Json.toJson(filtroDeUsuarios));
    }

    /**
     * Remove a user from a id
     *
     * @param id
     * @return ok user on json
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

        //busca o usuario para ser excluido
        Usuario usuario = Ebean.find(Usuario.class, id);

        if (usuario == null) {
            return notFound("Usuário não encontrado");
        }

        //caso o usuario administrador querer excluir outro administrador
        if (usuarioAtual.getEmail().equals(usuario.getEmail())) {
            return badRequest("Não excluir seu próprio usuário enquanto ele estiver autenticado.");
        }

        try {
            Ebean.delete(usuario);
        }  catch (Exception e) {
            Logger.info(e.getMessage());
            return badRequest("Erro interno de sistema.");
        }

        return ok(Json.toJson(usuario));
    }

    /**
     * Retrieve a list of users from a filter
     *
     * @param filtro
     * @return a list of filter users in json
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

        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email like :email or nome like :nomeUsuario)");
        query.setParameter("email", "%" + filtro + "%");
        query.setParameter("nomeUsuario", "%" + filtro + "%");
        List<Usuario> filtroDeUsuarios = query.findList();

        //remover o usuário admin da lista dos filtrados
        filtroDeUsuarios.remove(usuarioAtual);

        return ok(Json.toJson(filtroDeUsuarios));
    }

    /**
     * Send the confirm mail.
     *
     * @param usuario created
     * @throws EmailException Exception when sending mail
     */
    private void sendMailConfirmation(Usuario usuario) throws EmailException {
        String subject = Messages.get("mail.welcome.subject");
        String message = Messages.get("mail.welcome.message");
        //TODO
        Mail.Envelop envelop = new Mail.Envelop(subject, message, usuario.getEmail());
        Mail mailer = new Mail(mailerClient);
        mailer.sendMail(envelop);
    }

}
