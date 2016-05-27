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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public Result telaNovo() {
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

        String mensagem = "";
        String tipoMensagem = "";

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuário não autenticado";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            //logica onde instanciamos um objeto livro que esteja cadastrado na base de dados
            LivroFormData livroFormData = (id == 0) ? new LivroFormData() : models.Livro.makeLivroFormData(id);

            //apos o objeto ser instanciado passamos os dados para o livroformdata e os dados sera carregados no form edit
            Form<LivroFormData> formData = Form.form(LivroFormData.class).fill(livroFormData);

            return ok(views.html.admin.livros.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
        }
        return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));

    }

    /**
     * Save Livro
     *
     * @return a render view to inform CREATED
     */
    public Result inserir() {

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
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

        //se existir erros nos campos do formulario retorne o LivroFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.livros.create.render(formData));
        }
        else {
            //Converte os dados do formularios para uma instancia do Livro
            Livro livro = Livro.makeInstance(formData.get());

            //faz uma busca na base de dados do livro
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
     * Update a livro from id
     *
     * @param id
     * @return a livro updated with a form
     */
    public Result editar(Long id) {

        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formario atraves de uma requisicao e realiza a validacao dos campos
        Form<LivroFormData> formData = Form.form(LivroFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.livros.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.livros.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver erros retorna o formulario com os erros caso não tiver continua o processo de alteracao do livro
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.livros.edit.render(id,formData));
        } else {
            Livro livroBusca = Ebean.find(Livro.class, id);

            if (livroBusca == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
            }

            //Converte os dados do formularios para uma instancia do Livro
            Livro livro = Livro.makeInstance(formData.get());

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

                //necessario para excluir o livro antigo
                File pdfAntigo = new File(diretorioDePdfsLivros,livroBusca.getTitulo()+extensaoPadraoDePdfs);

                //exclui o artigo antigo
                pdfAntigo.delete();

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

            try {
                livro.setId(id);
                livro.setDataAlteracao(new Date());
                livro.update();
                tipoMensagem = "Sucesso";
                mensagem = "Artigo atualizado com sucesso.";
                return ok(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.toString());
            }

            return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        }
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

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");
        String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

        if (usuarioAtual == null) {
            mensagem = "Usuario não autenticado";
            tipoMensagem = "Erro";
            return notFound(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }

        //busca o artigo para ser excluido
        Livro livro = Ebean.find(Livro.class, id);

        if (livro == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
        }

        File pdf = new File(diretorioDePdfsLivros,livro.getTitulo()+extensaoPadraoDePdfs);

        try {
            Ebean.delete(livro);
            pdf.delete();
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
        return ok(Json.toJson(Ebean.find(Livro.class).findList()));
    }

    /**
     * return the pdf from a titulo
     *
     * @param titulo
     * @return ok pdf by name
     */
    public Result pdf(String titulo) {

        String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");
        String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

        File pdf = new File(diretorioDePdfsLivros,titulo+extensaoPadraoDePdfs);

        try {
            return ok(new FileInputStream(pdf));
        } catch (FileNotFoundException e) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render(titulo+extensaoPadraoDePdfs+" não foi encontrado"));
        } catch (Exception e) {
            return badRequest("Erro interno de sistema.");
        }

    }

}
