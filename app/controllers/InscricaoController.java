package controllers;

import play.data.Form;
import play.mvc.Result;
import views.validators.LivroFormData;

import static play.data.Form.form;
import static play.mvc.Results.ok;

public class InscricaoController {

    /**
     * @return livro form if auth OK or not autorizado form is auth KO
     */
    public Result telaInscricao() {
//        //busca o usuário atual que esteja logado no sistema
//        Usuario usuarioAtual = atual();
//
//        if (usuarioAtual == null) {
//            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
//        }
//
//        //verificar se o usuario atual encontrado é administrador
//        if (usuarioAtual.getPrivilegio() != 1) {
//            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
//        }
//
//        Form<LivroFormData> livroForm = form(LivroFormData.class);

        Form<LivroFormData> livroForm = form(LivroFormData.class);

        return ok(views.html.inscricao.inscricao.render(livroForm));
    }
}
