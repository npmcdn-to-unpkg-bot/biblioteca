package controllers;

import actions.Secured;
import akka.util.Crypt;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import play.Configuration;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static play.data.Form.form;

public class UsuarioController extends Controller {

    @Inject
    MailerClient mailerClient;

    private static DynamicForm form = Form.form();

    public Form<Usuario> usuarioForm = Form.form(Usuario.class);

    String mensagem = "";
    String tipoMensagem = "";
    String usuarioEmail = "";

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    private Usuario buscaPorEmail(String email) {
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email = :email)");
        query.setParameter("email", email);
        return query.findUnique();
    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    private Usuario buscaPorConfirmacaoToken(String token) {
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (confirmacao_token = :confirmacao_token)");
        query.setParameter("confirmacao_token", token);
        return query.findUnique();
    }

    /**
     * Valid an account with the url in the confirm mail.
     *
     * @param token a token attached to the user we're confirming.
     * @return Confirmationpage
     */
    public Result confirma(String token) {

        Usuario usuario = buscaPorConfirmacaoToken(token);

        if (usuario == null) {
            mensagem = "Seu código de ativação é inválido ou expirou!";
            tipoMensagem = "Invalido";
            return badRequest(views.html.mensagens.info.confirma.render(mensagem,tipoMensagem,usuarioEmail));
        }

        if (usuario.getValidado()) {
            mensagem = "Esta conta de usuário já foi confirmada!";
            tipoMensagem = "Validado";
            return badRequest(views.html.mensagens.info.confirma.render(mensagem,tipoMensagem,usuarioEmail));
        }

        try {
            if (usuario.confirmado(usuario)) {
                enviarEmailConfirmacao(usuario);
                mensagem = "Sua conta foi ativada com sucesso!";
                tipoMensagem = "Sucesso";
                usuarioEmail = usuario.getEmail();
                return badRequest(views.html.mensagens.info.confirma.render(mensagem,tipoMensagem,usuarioEmail));
            } else {
                mensagem = "Erro de confirmação do cadastro do usuário!";
                tipoMensagem = "Erro";
                return badRequest(views.html.mensagens.info.confirma.render(mensagem,tipoMensagem,usuarioEmail));
            }
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
        }

        return badRequest(views.html.mensagens.info.confirma.render(mensagem,tipoMensagem,usuarioEmail));

    }

    /**
     * Send the email.
     *
     * @param usuario created
     * @throws EmailException Exception when sending mail
     */
    private void enviarEmailToken(Usuario usuario) throws EmailException, MalformedURLException {
        String urlString = "http://" + Configuration.root().getString("server.hostname");
        urlString += "/confirma/" + usuario.getConfirmacaoToken();
        URL url = new URL(urlString); // validar a URL, e vai retornar throw se estiver errada

        String emailConfirmacaoBody = views.html.email.emailConfirmacaoBody.render(usuario,url.toString()).body();

        try {
            Email emailUser = new Email()
                .setSubject("Cadastro na Biblioteca - Confirme seu email")
                .setFrom("Biblioteca CIBiogás <biblioteca@email.com>")
                .addTo(usuario.getEmail())
                .setBodyHtml(emailConfirmacaoBody);
            mailerClient.send(emailUser);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * Send the email.
     *
     * @param usuario created
     * @throws EmailException Exception when sending mail
     */
    private void enviarEmailConfirmacao(Usuario usuario) throws EmailException {
        String emailBody = views.html.email.emailBody.render(usuario).body();
        try {
            Email emailUser = new Email()
                    .setSubject("Biblioteca Digital CIBiogás - Bem vindo")
                    .setFrom("Biblioteca CIBiogás <biblioteca@email.com>")
                    .addTo(usuario.getEmail())
                    .setBodyHtml(emailBody);
            mailerClient.send(emailUser);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * @return cadastrado form if register success
     */
    public Result telaCadastrado() {
        String username = session().get("email");
        return ok(views.html.mensagens.info.cadastrado.render(username));
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
    private Usuario atual() {
        String username = session().get("email");

        //retorna o usuário atual que esteja logado no sistema
        return Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();
    }

    /**
     * Save a user
     *
     * @return ok user json
     */
    public Result inserir(){

        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String email = formPreenchido.data().get("email");
        String senha = Crypt.sha1(formPreenchido.data().get("confirm_senha"));
        String nome = formPreenchido.data().get("nome");

        //valida se o email e a senha não estejam vazios
        if (email.equals("") || senha.equals("")) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Email ou Senha não podem estar vazios!");
            return badRequest(views.html.login.render(formDeErro));
        }

        //faz uma busca na base de dados do usuario
        Usuario usuarioBusca = Ebean.find(Usuario.class).where().eq("email", formPreenchido.data().get("email")).findUnique();

        if (usuarioBusca != null) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Usuário '" + usuarioBusca.getEmail() + "' já esta Cadastrado!");
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        Usuario novo = new Usuario();
        novo.setEmail(email);
        novo.setSenha(senha);
        novo.setNome(nome);
        novo.setStatus(true);
        novo.setDataCadastro(new Date());
        novo.setPrivilegio(2);
        novo.setConfirmacaoToken(UUID.randomUUID().toString());

        try {
            Ebean.save(novo);
            enviarEmailToken(novo);
        } catch (Exception e) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Erro interno de sistema!");
            Logger.info(e.getMessage());
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        String username = novo.getEmail();

        return ok(views.html.mensagens.info.cadastrado.render(username));
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
        } catch (Exception e) {
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
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        //busca todos os usuários menos o usuário padrão do sistema
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email != 'admin')");
        List<Usuario> filtroDeUsuarios = query.findList();

        return ok(Json.toJson(filtroDeUsuarios));
    }

    /**
     * Retrieve a list of all usuarios
     *
     * @return a list of all usuarios in a render template
     */
    @Security.Authenticated(Secured.class)
    public Result lista() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        //busca todos os usuários menos o usuário padrão do sistema
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email != 'admin')");
        List<Usuario> filtroDeUsuarios = query.findList();

        return ok(views.html.admin.usuarios.list.render(filtroDeUsuarios,""));
    }

    /**
     * Remove a user from a id
     *
     * @param id
     * @return ok user on json
     */
    @Security.Authenticated(Secured.class)
    public Result remover(Long id) {

        String mensagem = "";
        String tipoMensagem = "";

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuario não autenticado";
            tipoMensagem = "Erro";
            return notFound(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        //verifica para nao excluir o usuario admin
        if (!usuarioAtual.getEmail().equals("admin")) {
            mensagem = "Não autorizado!";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        //busca o usuario para ser excluido
        Usuario usuario = Ebean.find(Usuario.class, id);

        if (usuario == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        //caso o usuario administrador quere excluir outro administrador enquanto estiver autenticado
        if (usuarioAtual.getEmail().equals(usuario.getEmail())) {
            mensagem = "Não excluir seu próprio usuário enquanto ele estiver autenticado.";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        try {
            Ebean.delete(usuario);
            mensagem = "Usuário excluído com sucesso";
            tipoMensagem = "Sucesso";
        }  catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
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

        //remove o usuario logado da lista dos filtrados
        filtroDeUsuarios.remove(usuarioAtual);

        return ok(Json.toJson(filtroDeUsuarios));
    }

    /**
     * @return detail form with a user
     */
    @Security.Authenticated(Secured.class)
    public Result detalhe(Long id) {
        Usuario usuario = Ebean.find(Usuario.class, id);

        if (usuario == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        return ok(views.html.admin.usuarios.detail.render(usuario));
    }

    /**
     * @return edit form with a user
     */
    @Security.Authenticated(Secured.class)
    public Result telaEditar(Long id) {

        Form<Usuario> usuarioForm = form(Usuario.class).fill(Usuario.find.byId(id));


        if (usuarioForm == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        return ok(views.html.admin.usuarios.edit.render(id,usuarioForm));
    }

    /**
     * Update a user from id
     *
     * @param id
     * @return a user updated with a form
     */
    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {

        Usuario usuarioBusca = Ebean.find(Usuario.class, id);

        if (usuarioBusca == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        Form<Usuario> form = usuarioForm.fill(Usuario.find.byId(id)).bindFromRequest();

        try {
            Usuario usuario = form.get();

            if (usuarioBusca.getValidado() == true) {
                //precisa setar o id, pois por algum motivo ele se perde no formulário
                usuario.setId(id);
                usuario.setValidado(true);
            }

            String senha = Crypt.sha1(usuario.getSenha());
            usuario.setSenha(senha);
            usuario.setDataAlteracao(new Date());
            usuario.update();
            tipoMensagem = "Sucesso";
            mensagem = "Usuário atualizado com sucesso.";
        } catch (IllegalStateException e) {
            usuarioForm.reject("Os campos nome ou email não podem estar vazios!");
            return badRequest(views.html.admin.usuarios.edit.render(id,usuarioForm));
        } catch (Exception e) {
            tipoMensagem = "Erro";
            mensagem = "Erro Interno de sistema.";
            return badRequest(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.usuario.mensagens.render(mensagem,tipoMensagem));
    }

}
