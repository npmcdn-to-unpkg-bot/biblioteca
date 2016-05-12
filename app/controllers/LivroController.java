package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Livro;
import models.Usuario;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.validators.LivroFormData;

import java.io.File;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class LivroController extends Controller {

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

        Form<LivroFormData> livroForm = form(LivroFormData.class);

        return ok(views.html.admin.livros.create.render(livroForm));
    }

    /**
     * Save Livro
     *
     * @return a render view to inform CREATED
     */
    public Result inserir() {

        //Resgata os dados do formario atraves de uma requisicao e realiza a validacao dos campos
        Form<LivroFormData> formData = Form.form(LivroFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.livros.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.livros.create.render(formData));
        }

        if (formData.hasErrors()) {
            return badRequest(views.html.admin.livros.create.render(formData));
        }
        else {
            //Converte os dados do formularios para uma instancia do Livro
            Livro livro = Livro.makeInstance(formData.get());

            //faz uma busca na base de dados do usuario
            Livro livroBusca = Ebean.find(Livro.class).where().eq("isbn", formData.data().get("isbn")).findUnique();

            if (livroBusca != null) {
                formData.reject("O Livro '" + livroBusca.getTitulo() + "' já esta Cadastrado!");
                return badRequest(views.html.admin.livros.create.render(formData));
            }

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

            String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

            if (arquivo != null) {
                String arquivoTitulo = form().bindFromRequest().get("titulo");
                String pdf = arquivoTitulo + extensaoPadraoDePdfs;
                String tipoDeConteudo = arquivo.getContentType();
                File file = arquivo.getFile();
                String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");
                String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

                if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                    file.renameTo(new File(diretorioDePdfsLivros,pdf));
                } else {
                    formData.reject("Apenas arquivos em formato PDF é aceito");
                    return badRequest(views.html.admin.livros.create.render(formData));
                }
            } else {
                formData.reject("Selecione um arquivo no formato PDF");
                return badRequest(views.html.admin.livros.create.render(formData));
            }

            livro.setDataCadastro(new Date());

            try {
                livro.save();
                return created(views.html.mensagens.livro.cadastrado.render(livro.getTitulo()));
            } catch (Exception e) {
                Logger.error(e.toString());
            }

            formData.reject("Não foi possível cadastrar, erro interno de sistema.");
            return badRequest(views.html.admin.livros.create.render(formData));
        }
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
     * @return render a detail form with a livro data
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
     * @return render edit form with a livro data
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

    /**
     * Update a livro from id
     *
     * @param id
     * @return a livro updated with a form
     */
    public Result editar(Long id) {
        return TODO;
    }

    /**
     * Remove a livro from a id
     *
     * @param id
     * @return ok livro removed
     */
    public Result remover(Long id) {

        String mensagem = "";
        String tipoMensagem = "";

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

    /**
     * Retrieve a list of all livros
     *
     * @return a list of all livros in json
     */
    public Result buscaTodos() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        return ok(Json.toJson(Ebean.find(Livro.class).findList()));
    }

}
