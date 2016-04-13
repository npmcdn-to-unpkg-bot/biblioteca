package controllers.senha;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import models.Token;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.MalformedURLException;

public class SenhaController extends Controller {

    @Inject
    MailerClient mailerClient;

    private static DynamicForm form = Form.form();

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
     * Display the reset password confirm form.
     */
    public Result resetConfirmaTela() {
        return ok(views.html.senha.confirma.render());
    }

    /**
     * Send a mail with the reset link.
     *
     * @return info error page with flash error
     */
    public Result runPassword() throws EmailException, MalformedURLException {

        String mensagem = "";
        String tipoMensagem = "";

        Usuario usuario = atual();

        try {
            Token t = new Token();
            t.sendMailResetPassword(usuario,mailerClient);
            return ok();
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            mensagem = "Impossível validar a URL";
            tipoMensagem = "Erro";
        }
        return badRequest(views.html.mensagens.info.reset.render(mensagem,tipoMensagem));
    }

    /**
     *
     * verifica atraves do token recebido se ele e valido, se ele for valido retorna a pagina de alterar a senha
     * @param token from the url
     * @return password change page if success or a error page if a KO
     */
    public Result reset(String token) {

        String mensagem;
        String tipoMensagem;

        if (token == null) {
            mensagem = "O token é nulo!";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.info.reset.render(mensagem,tipoMensagem));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);

        if (resetToken == null) {
            mensagem = "O reset token é nulo";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.info.reset.render(mensagem,tipoMensagem));
        }

        if (resetToken.isExpired()) {
            mensagem = "O token expirou!";
            tipoMensagem = "Invalido";
            return badRequest(views.html.mensagens.info.reset.render(mensagem,tipoMensagem));
        }

        DynamicForm formDeErro = new DynamicForm();
        return ok(views.html.senha.altera.render(formDeErro,token));
    }

    /**
     *
     * realiza o reset da senha usando o token enviado por email para o usuario
     * @param token from the url
     * @throws EmailException Exception when sending mail
     */
    public Result runReset(String token) throws EmailException {
        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        if (formPreenchido.hasErrors()) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("O campo senha não pode estar vazio!");
            return badRequest(views.html.senha.altera.render(formDeErro,token));
        }

        try {
            Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);
            if (resetToken == null) {
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Reset token vazio!");
                return badRequest(views.html.senha.altera.render(formDeErro,token));
            }

            if (resetToken.isExpired()) {
                resetToken.delete();
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Token expirou!");
                return badRequest(views.html.senha.altera.render(formDeErro,token));
            }

            // check email
            Usuario usuario = Ebean.find(Usuario.class, resetToken.usuarioId);
            if (usuario == null) {
                // display no detail (email unknown for example) to
                // avoir check email by foreigner
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Usuário não encontrado!");
                return badRequest(views.html.senha.altera.render(formDeErro,token));
            }

            String senha = formPreenchido.data().get("confirm_senha");
            usuario.mudarSenha(senha);

            String mensagem = "";
            String tipoMensagem = "";

            // Send email saying that the password has just been changed.
            enviarEmailConfirmacao(usuario);
            mensagem = "Senha alterada com sucesso!";
            tipoMensagem = "Validado";
            return ok(views.html.mensagens.info.reset.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Usuário não encontrado!");
            return badRequest(views.html.senha.altera.render(formDeErro,token));
        }

    }

    /**
     * Send the email.
     *
     * @param usuario created
     * @throws EmailException Exception when sending mail
     */
    private void enviarEmailConfirmacao(Usuario usuario) throws EmailException {
        String emailBody = views.html.email.emailSenhaAlteradaBody.render(usuario).body();
        try {
            Email emailUser = new Email()
                    .setSubject("Cadastro na Biblioteca")
                    .setFrom("Biblioteca CIBiogás <biblioteca@email.com>")
                    .addTo(usuario.getEmail())
                    .setBodyHtml(emailBody);
            mailerClient.send(emailUser);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

}
