package controllers;

import actions.PlayAuthenticatedSecured;
import akka.util.Crypt;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Usuario;
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
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

/**
 * Created by haroldo on 25/01/16.
 */
public class UsuarioController extends Controller {

//    @Inject
//    MailerClient mailerClient;

    private static DynamicForm form = Form.form();

    public Result telaCadastrado() {
        String username = session().get("email");
        return ok(views.html.cadastrado.render(username));
    }

    public Result telaCadastro() {
        return ok(views.html.cadastro.render(form));
    }

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
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        String username = novo.getEmail();

        //Envia email teste
//        Email emailUser = new Email()
//                .setSubject("Cadastro na Biblioteca")
//                .setFrom("Biblioteca <biblioteca@email.com>")
//                .addTo("<haroldo.nobrega@cibiogas.org>")
//                .setBodyText("O Usuário foi cadastrado com sucesso!");
//        mailerClient.send(emailUser);

        return ok(views.html.cadastrado.render(username));
    }

    @Security.Authenticated(PlayAuthenticatedSecured.class)
    public Result atualizar(Long id) {
        String username = session().get("email");

        Usuario usuario = Json.fromJson(request().body().asJson(), Usuario.class);

        Usuario usuarioBusca = Ebean.find(Usuario.class, id);

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

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
            return badRequest("Erro interno de sistema!");
        }

        return ok(Json.toJson(usuario));
    }

    @Security.Authenticated(PlayAuthenticatedSecured.class)
    public Result buscaPorId(Long id) {
        String username = session().get("email");

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        Usuario usuario = Ebean.find(Usuario.class, id);

        if (usuario == null) {
            return notFound("Usuário não encontrado.");
        }

        return ok(Json.toJson(usuario));
    }

    @Security.Authenticated(PlayAuthenticatedSecured.class)
    public Result buscaTodos() {
        String username = session().get("email");

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        //busca todos os usuários menos o usuário padrão do sistema
        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email != 'admin@biblioteca.com')");
        List<Usuario> filtroDeUsuarios = query.findList();

        return ok(Json.toJson(filtroDeUsuarios));
    }

    @Security.Authenticated(PlayAuthenticatedSecured.class)
    public Result remover(Long id) {
        String username = session().get("email");

        Usuario usuario = Ebean.find(Usuario.class, id);

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() == 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        if (usuario == null) {
            return notFound("Usuário não encontrado");
        }

        //caso o usuario administrador querer excluir outro administrado
        if (usuarioAtual.getEmail().equals(usuario.getEmail())) {
            return badRequest("Não excluir seu próprio usuário enquanto ele estiver autenticado.");
        }

        try {
            Ebean.delete(usuario);
        }  catch (Exception e) {
            return badRequest("Erro interno de sistema.");
        }

        return ok(Json.toJson(usuario));
    }

    @Security.Authenticated(PlayAuthenticatedSecured.class)
    public Result filtra(String filtro) {
        String username = session().get("email");

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                .setParameter("email", username)
                .findUnique();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() == 1) {
            return badRequest("Você não tem privilégios de Administrador.");
        }

        Query<Usuario> query = Ebean.createQuery(Usuario.class, "find usuario where (email like :email)");
        query.setParameter("email", "%" + filtro + "%");
        List<Usuario> filtroDeUsuarios = query.findList();

        return ok(Json.toJson(filtroDeUsuarios));
    }
}
