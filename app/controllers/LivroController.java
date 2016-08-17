package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Livro;
import models.Usuario;
import play.Logger;
import play.Play;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.validators.LivroFormData;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;
import static views.validators.ValidaPDF.isPDF2;

@Security.Authenticated(Secured.class)
public class LivroController extends Controller {

    /**
     * metodo responsavel por modificar o titulo do arquivo
     *
     * @param str
     * @return a string formatada
     */
    private static String formatarTitulo(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ","-").toLowerCase();
    }

    /**
     * @return a object user authenticated
     */
    @Nullable
    private Usuario atual() {
        String username = session().get("email");

        try {
            //retorna o usuário atual que esteja logado no sistema
            return Ebean.createQuery(Usuario.class, "find usuario where email = :email")
                    .setParameter("email", username)
                    .findUnique();
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * @return livro form if auth OK or not autorizado form is auth KO
     */
    public Result telaNovo() {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<LivroFormData> livroForm = form(LivroFormData.class);

        return ok(views.html.admin.livros.create.render(livroForm));
    }

    /**
     * Retrieve a list of all livros
     *
     * @return a list of all livros in a render template
     */
    public Result telaLista() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            List<Livro> livro = Ebean.find(Livro.class).findList();
            return ok(views.html.admin.livros.list.render(livro,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * @return render a detail form with a livro data
     */
    public Result telaDetalhe(Long id) {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            Livro livro = Ebean.find(Livro.class, id);

            if (livro == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
            }

            return ok(views.html.admin.livros.detail.render(livro));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * @return render edit form with a livro data
     */
    public Result telaEditar(Long id) {

        String mensagem;
        String tipoMensagem;

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

            //apos o objeto ser instanciado passamos os dados para o livroformdata e os dados serao carregados no form edit
            Form<LivroFormData> formData = Form.form(LivroFormData.class).fill(livroFormData);

            return ok(views.html.admin.livros.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        }

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
            try {
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
                String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");
                String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("titulo");

                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String pdf = arquivoTitulo + extensaoPadraoDePdfs;

                    livro.setNomeArquivo(pdf);

                    String tipoDeConteudo = arquivo.getContentType();

                    File file = arquivo.getFile();

                    if (!isPDF2(file)) {
                        formData.reject("Arquivo PDF Inválido");
                        return badRequest(views.html.admin.livros.create.render(formData));
                    }

                    if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                        if (file.renameTo(new File(diretorioDePdfsLivros,pdf))) {
                            Logger.info("File Livro is created!");
                        } else {
                            Logger.error("Failed to create file Livro!");
                            formData.reject("Erro ao salvar o arquivo PDF. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.livros.create.render(formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato PDF é aceito");
                        return badRequest(views.html.admin.livros.create.render(formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato PDF");
                    return badRequest(views.html.admin.livros.create.render(formData));
                }

                livro.setDataCadastro(new Date());
                livro.save();
                return created(views.html.mensagens.livro.cadastrado.render(livro.getTitulo()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.livros.create.render(formData));

            }

        }

    }

    /**
     * Update a livro from id
     *
     * @param id identiricador
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
            try {
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

                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String pdf = arquivoTitulo + extensaoPadraoDePdfs;

                    livro.setNomeArquivo(pdf);

                    String tipoDeConteudo = arquivo.getContentType();

                    File file = arquivo.getFile();

                    if (!isPDF2(file)) {
                        formData.reject("Arquivo PDF Inválido");
                        return badRequest(views.html.admin.livros.edit.render(id,formData));
                    }

                    String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");
                    String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

                    //necessario para excluir o livro antigo
                    File pdfAntigo = new File(diretorioDePdfsLivros,livroBusca.getNomeArquivo());

                    if (pdfAntigo.delete()) {
                        Logger.info("File Livro is deleted!");
                    } else {
                        Logger.error("Failed to edit file Livro!");
                    }

                    if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                        if (file.renameTo(new File(diretorioDePdfsLivros,pdf))) {
                            Logger.info("File Livro is edited!");
                        } else {
                            Logger.error("Failed to edit file Livro!");
                            formData.reject("Erro ao salvar o arquivo PDF. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.livros.edit.render(id,formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato PDF é aceito");
                        return badRequest(views.html.admin.livros.edit.render(id,formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato PDF");
                    return badRequest(views.html.admin.livros.edit.render(id,formData));
                }

                livro.setId(id);
                livro.setDataAlteracao(new Date());
                livro.update();
                tipoMensagem = "Sucesso";
                mensagem = "Artigo atualizado com sucesso.";
                return ok(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
                return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
            }
        }
    }

    /**
     * Remove a livro from a id
     *
     * @param id identificador
     * @return ok livro removed
     */
    public Result remover(Long id) {

        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

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

        try {
            //busca o artigo para ser excluido
            Livro livro = Ebean.find(Livro.class, id);

            if (livro == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Livro não encontrado"));
            }

            String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");

            File pdf = new File(diretorioDePdfsLivros,livro.getNomeArquivo());

            Ebean.delete(livro);

            if (pdf.delete()) {
                Logger.info("File Livro is deleted!");
            } else {
                Logger.error("Failed to delete file Livro!");
            }

            mensagem = "Livro excluído com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.livro.mensagens.render(mensagem,tipoMensagem));
        }

    }

    /**
     * Retrieve a list of all livros
     *
     * @return a list of all livros in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Livro.class).findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }

    }

    /**
     * return the pdf file from nomeArquivo
     *
     * @param nomeArquivo nome
     * @return ok pdf by name
     */
    public Result pdf(String nomeArquivo) {

        String diretorioDePdfsLivros = Play.application().configuration().getString("diretorioDePdfsLivros");

        try {

            File pdf = new File(diretorioDePdfsLivros,nomeArquivo);

            return ok(new FileInputStream(pdf)).as("application/pdf");
        } catch (FileNotFoundException e) {
            Logger.error(e.getMessage());
            return notFound(views.html.mensagens.erro.naoEncontrado.render(nomeArquivo));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("app.error")));
        }

    }

}
