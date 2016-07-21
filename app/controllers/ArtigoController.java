package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Artigo;
import models.Usuario;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.annotation.Nullable;
import javax.persistence.PersistenceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class ArtigoController extends Controller {

    private static DynamicForm form = Form.form();

    private Form<Artigo> artigoForm = Form.form(Artigo.class);

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
     * @return autenticado form if auth OK or login form is auth KO
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

        return ok(views.html.admin.artigos.create.render(form));
    }

    /**
     * Retrieve a list of all artigos
     *
     * @return a list of all artigos in a render template
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
            List<Artigo> artigos = Ebean.find(Artigo.class).findList();
            return ok(views.html.admin.artigos.list.render(artigos,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * @return render a detail form with a artigo data
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
            Artigo artigo = Ebean.find(Artigo.class, id);

            if (artigo == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
            }

            return ok(views.html.admin.artigos.detail.render(artigo));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * @return render edit form with a artigo data
     */
    public Result telaEditar(Long id) {

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
            Form<Artigo> artigoForm = form(Artigo.class).fill(Artigo.find.byId(id));


            if (artigoForm == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
            }

            return ok(views.html.admin.artigos.edit.render(id,artigoForm));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }

    }

    /**
     * Save a artigo
     *
     * @return a render view to inform OK
     */
    public Result inserir() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String titulo = formPreenchido.data().get("titulo");
        String resumo = formPreenchido.data().get("resumo");

        //valida se o titulo e o resumo não estejam vazios
        if (titulo.equals("") || resumo.equals("") || titulo.isEmpty() || resumo.isEmpty()) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Título ou Resumo não podem estar vazios!");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
        }

        if (titulo.length() > 150) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Título não pode passar de 150 caractéres");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
        }

        if (resumo.length() > 254) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Resumo não pode passar de 254 caractéres");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
        }

        try {
            //faz uma busca do artigo na base de dados
            Artigo artigoBusca = Ebean.find(Artigo.class).where().eq("titulo", formPreenchido.data().get("titulo")).findUnique();

            if (artigoBusca != null) {
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Artigo '" + artigoBusca.getTitulo() + "' já esta Cadastrado!");
                return badRequest(views.html.admin.artigos.create.render(formDeErro));
            }

            Artigo novo = new Artigo();

            novo.setTitulo(titulo);
            novo.setResumo(resumo);
            novo.setDataCadastro(new Date());

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

            String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

            if (arquivo != null) {
                String arquivoTitulo = form().bindFromRequest().get("titulo");

                arquivoTitulo = formatarTitulo(arquivoTitulo);

                String pdf = arquivoTitulo + extensaoPadraoDePdfs;

                novo.setNomeArquivo(pdf);

                String tipoDeConteudo = arquivo.getContentType();

                File file = arquivo.getFile();

                String diretorioDePdfsArtigos = Play.application().configuration().getString("diretorioDePdfsArtigos");
                String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

                if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                    file.renameTo(new File(diretorioDePdfsArtigos,pdf));
                } else {
                    DynamicForm formDeErro = form.fill(formPreenchido.data());
                    formDeErro.reject("Apenas arquivos em formato PDF é aceito");
                    return badRequest(views.html.admin.artigos.create.render(formDeErro));
                }
            } else {
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Selecione um arquivo no formato PDF");
                return badRequest(views.html.admin.artigos.create.render(formDeErro));
            }

            Ebean.save(novo);
            return created(views.html.mensagens.artigo.cadastrado.render(titulo));
        } catch (Exception e) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Erro interno de sistema!");
            Logger.error(e.getMessage());
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
        }

    }

    /**
     * Update a artigo from id
     *
     * @param id
     * @return a artigo updated with a form
     */
    public Result editar(Long id) {

        String mensagem;
        String tipoMensagem;

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

            Artigo artigoBusca = Ebean.find(Artigo.class, id);

            if (artigoBusca == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
            }

            Form<Artigo> form = artigoForm.fill(Artigo.find.byId(id)).bindFromRequest();

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

            String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

            Artigo artigo = form.get();

            if (arquivo != null) {
                String arquivoTitulo = form().bindFromRequest().get("titulo");

                arquivoTitulo = formatarTitulo(arquivoTitulo);

                String pdf = arquivoTitulo + extensaoPadraoDePdfs;

                artigo.setNomeArquivo(pdf);

                String tipoDeConteudo = arquivo.getContentType();

                File file = arquivo.getFile();

                String diretorioDePdfsArtigos = Play.application().configuration().getString("diretorioDePdfsArtigos");
                String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

                //necessario para excluir o artigo antigo
                File pdfAntigo = new File(diretorioDePdfsArtigos,artigoBusca.getNomeArquivo());

                //exclui o artigo antigo
                pdfAntigo.delete();

                if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                    file.renameTo(new File(diretorioDePdfsArtigos,pdf));
                } else {
                    Form<Artigo> formDeErro = artigoForm.fill(Artigo.find.byId(id));
                    formDeErro.reject("Apenas arquivos em formato PDF é aceito");
                    return badRequest(views.html.admin.artigos.edit.render(id,formDeErro));
                }
            }else {
                Form<Artigo> formDeErro = artigoForm.fill(Artigo.find.byId(id));
                formDeErro.reject("Selecione um arquivo em formato PDF");
                return badRequest(views.html.admin.artigos.edit.render(id,formDeErro));
            }

            if (artigo.getTitulo().isEmpty() || artigo.getResumo().isEmpty()) {
                Form<Artigo> formDeErro = artigoForm.fill(Artigo.find.byId(id));
                formDeErro.reject("O campo 'Título' ou 'Resumo' não podem estar vazios");
                return badRequest(views.html.admin.artigos.edit.render(id,formDeErro));
            }

            artigo.setId(id);
            artigo.setDataAlteracao(new Date());
            artigo.update();
            tipoMensagem = "Sucesso";
            mensagem = "Artigo atualizado com sucesso.";

            return ok(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        } catch (PersistenceException e) {
            Form<Artigo> formDeErro = artigoForm.fill(Artigo.find.byId(id));
            formDeErro.reject("O campo 'Resumo' suporta no máximo 254 caractéres");
            Logger.error(e.getMessage());
            return badRequest(views.html.admin.artigos.edit.render(id,formDeErro));
        }  catch (Exception e) {
            tipoMensagem = "Erro";
            mensagem = "Erro Interno de sistema.";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Remove a artigo from a id
     *
     * @param id
     * @return ok artigo removed
     */
    public Result remover(Long id) {

        String mensagem;
        String tipoMensagem;

        String diretorioDePdfsArtigos = Play.application().configuration().getString("diretorioDePdfsArtigos");

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
            Artigo artigo = Ebean.find(Artigo.class, id);

            if (artigo == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
            }

            File pdf = new File(diretorioDePdfsArtigos,artigo.getNomeArquivo());

            Ebean.delete(artigo);

            pdf.delete();

            mensagem = "Artigo excluído com sucesso";
            tipoMensagem = "Sucesso";
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
    }

    /**
     * Retrieve a list of all artigos
     *
     * @return a list of all artigos in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Artigo.class).findList()));
        } catch (Exception e) {
            return badRequest(Json.toJson(Messages.get("app.error")));
        }

    }

    /**
     * return the pdf from a nameFile
     *
     * @param nomeArquivo
     * @return ok pdf by name
     */
    public Result pdf(String nomeArquivo) {

        String diretorioDePdfsArtigos = Play.application().configuration().getString("diretorioDePdfsArtigos");

        try {
            File pdf = new File(diretorioDePdfsArtigos,nomeArquivo);
            return ok(new FileInputStream(pdf)).as("application/pdf");
        } catch (FileNotFoundException e) {
            Logger.error(e.getMessage());
            return notFound(views.html.mensagens.erro.naoEncontrado.render(nomeArquivo));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Messages.get("app.error"));
        }

    }

    /**
     * Retrieve a list of artigos from a filter
     *
     * @param filtro
     * @return a list of filter artigos in json
     */
    public Result filtra(String filtro) {
        try {
            Query<Artigo> query = Ebean.createQuery(Artigo.class, "find artigo where (titulo like :titulo)");
            query.setParameter("titulo", "%" + filtro + "%");
            List<Artigo> filtroDeArtigos = query.findList();

            return ok(Json.toJson(filtroDeArtigos));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("app.error")));
        }

    }

}
