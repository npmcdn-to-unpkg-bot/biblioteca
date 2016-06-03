package controllers;

import akka.util.Crypt;
import com.avaje.ebean.Ebean;
import models.Usuario;
import models.Usuarios;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

public class LoginController extends Controller {

    private static DynamicForm form = Form.form();

    /**
     * @return autenticado form if auth OK or login form is auth KO
     */
    public Result telaLogin() {
        String username = session().get("email");

        try {
            //busca o usuário atual que esteja logado no sistema
            Usuario usuarioAtual = Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                    .setParameter("email", username)
                    .findUnique();

            if (usuarioAtual != null) {
                return ok(views.html.mensagens.info.autenticado.render(username));
            }
        }catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

        return ok(views.html.login.render(form));
    }

    /**
     * @return autenticado form if auth OK
     */
    public Result telaAutenticado() {
        String username = session().get("email");
        return ok(views.html.mensagens.info.autenticado.render(username));
    }

    /**
     * @return logout form
     */
    public Result telaLogout() {
        return ok(views.html.mensagens.info.logout.render());
    }

    /**
     * Handle login form submission.
     *
     * @return  if auth OK or login form if auth KO
     */
    public Result autenticar() {

        Form<DynamicForm.Dynamic> requestForm = form.bindFromRequest();

        String email = requestForm.data().get("email");
        String senha = requestForm.data().get("senha");

        if (email.equals("") || senha.equals("")) {
            DynamicForm formDeErro = form.fill(requestForm.data());
            formDeErro.reject(Messages.get("login.error.field"));
            return badRequest(views.html.login.render(formDeErro));
        }

        F.Option<Usuario> talvesUmUsuario = Usuarios.existe(email, Crypt.sha1(senha));

        if (talvesUmUsuario.isDefined()) {
            if (!talvesUmUsuario.get().getValidado()) {
                DynamicForm formDeErro = form.fill(requestForm.data());
                formDeErro.reject(Messages.get("login.error.confirm"));
                return badRequest(views.html.login.render(formDeErro));
            }
            session().put("email", talvesUmUsuario.get().getEmail());
            return redirect(routes.LoginController.telaAutenticado());
        }

        DynamicForm formDeErro = form.fill(requestForm.data());
        formDeErro.reject(Messages.get("login.error"));

        return forbidden(views.html.login.render(formDeErro));
    }

    /**
     * @return redirect telaLogout
     */
    public Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

}
