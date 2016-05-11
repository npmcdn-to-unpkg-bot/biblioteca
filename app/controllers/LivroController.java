package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Livro;
import models.Usuario;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class LivroController extends Controller {

    String mensagem = "";
    String tipoMensagem = "";

    public Form<Livro> livroForm = Form.form(Livro.class);

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
     * @return autenticado form if auth OK or login form is auth KO
     */
    public Result novoTela() {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<Livro> livroForm = form(Livro.class);

        return ok(views.html.admin.livros.create.render(livroForm));
    }

    public Result inserir() {
        Form<Livro> livroForm = form(Livro.class).bindFromRequest();

        if(livroForm.hasErrors()) {
            livroForm.reject("Ocorreu um erro, verifique caso tenha algum campo vazio.");
            return badRequest(views.html.admin.livros.create.render(livroForm));
        }

        Livro livro = livroForm.get();

        livro.setDataCadastro(new Date());
        livro.save();

        String titulo = livro.getTitulo();

        return created(views.html.mensagens.livro.cadastrado.render(titulo));
    }

    /**
     * Retrieve a list of all artigos
     *
     * @return a list of all artigos in a render template
     */
    public Result telaLista() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        List<Livro> livro = Ebean.find(Livro.class).findList();
        return ok(views.html.admin.livros.list.render(livro,""));
    }

    /**
     * @return render a detail form with a artigo data
     */
    public Result telaDetalhe(Long id) {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Livro livro = Ebean.find(Livro.class, id);

        if (livro == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
        }

        return ok(views.html.admin.livros.detail.render(livro));
    }

    /**
     * @return render edit form with a artigo data
     */
    public Result telaEditar(Long id) {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<Livro> livroForm = form(Livro.class).fill(Livro.find.byId(id));


        if (livroForm == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
        }

        return ok(views.html.admin.livros.edit.render(id,livroForm));
    }

    public Result editar(Long id) {
        return TODO;
    }

    public Result remover(Long id) {

        //busca o artigo para ser excluido
        Livro livro = Ebean.find(Livro.class, id);

        if (livro == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
        }

        try {
            Ebean.delete(livro);
            mensagem = "Livro excluído com sucesso";
            tipoMensagem = "Sucesso";
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
    }

}
