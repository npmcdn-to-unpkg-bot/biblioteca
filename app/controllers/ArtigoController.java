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
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class ArtigoController extends Controller {

    private static DynamicForm form = Form.form();

    /**
     * @return autenticado form if auth OK or login form is auth KO
     */
    public Result novoTela() {
        return ok(views.html.artigo.novo.render(form));
    }

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
     * Save a artigo
     *
     * @return a render view to inform OK
     */
    public Result inserir() {
        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String titulo = formPreenchido.data().get("titulo");
        String resumo = formPreenchido.data().get("resumo");

        //valida se o email e a senha não estejam vazios
        if (titulo.equals("") || resumo.equals("")) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Título ou Resumo não podem estar vazios!");
            return badRequest(views.html.artigo.novo.render(formDeErro));
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

        String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

        if (arquivo != null) {
            String arquivoTitulo = form().bindFromRequest().get("titulo");
            String pdf = arquivoTitulo + extensaoPadraoDePdfs;
            String tipoDeConteudo = arquivo.getContentType();
            File file = arquivo.getFile();
            String diretorioDePdfs = Play.application().configuration().getString("diretorioDePdfs");
            String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

            if (tipoDeConteudo.equals(contentTypePadraoDePdfs)) {
                file.renameTo(new File(diretorioDePdfs,pdf));
            } else {
                DynamicForm formDeErro = form.fill(formPreenchido.data());
                formDeErro.reject("Apenas arquivos em formato PDF é aceito");
                return badRequest(views.html.artigo.novo.render(formDeErro));
            }
        } else {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Selecione um arquivo no formato PDF");
            return badRequest(views.html.artigo.novo.render(formDeErro));
        }

        Artigo novo = new Artigo();
        novo.setTitulo(titulo);
        novo.setResumo(resumo);
        novo.setDataCadastro(new Date());

        try {
            Ebean.save(novo);
        } catch (Exception e) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Erro interno de sistema!");
            Logger.info(e.getMessage());
            return badRequest(views.html.cadastro.render(formDeErro));
        }

        return ok(views.html.mensagens.info.artigoCadastrado.render(titulo));
    }

    /**
     * Retrieve a list of all artigos
     *
     * @return a list of all artigos in json
     */
    public Result buscaTodos() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        return ok(Json.toJson(Ebean.find(Artigo.class).findList()));
    }

    /**
     * Retrieve a list of artigos from a filter
     *
     * @param filtro
     * @return a list of filter artigos in json
     */
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

        Query<Artigo> query = Ebean.createQuery(Artigo.class, "find artigo where (titulo like :titulo)");
        query.setParameter("titulo", "%" + filtro + "%");
        List<Artigo> filtroDeArtigos = query.findList();

        return ok(Json.toJson(filtroDeArtigos));
    }

    /**
     * Retrieve a artigo from id
     *
     * @param id
     * @return a artigo json
     */
    public Result buscaPorId(Long id) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //busca o contato
        Artigo artigo = Ebean.find(Artigo.class, id);

        if (artigo == null) {
            return notFound("Artigo não encontrado");
        }

        return ok(Json.toJson(artigo));
    }

    /**
     * Remove a artigo from a id
     *
     * @param id
     * @return ok artigo on json
     */
    public Result remover(Long id) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound("Usuario não autenticado");
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest("Você não tem privilégios de Administrador");
        }

        //busca o contato para ser excluido
        Artigo artigo = Ebean.find(Artigo.class, id);

        if (artigo == null) {
            return notFound("Artigo não encontrado");
        }

        try {
            Ebean.delete(artigo);
        }  catch (Exception e) {
            Logger.info(e.getMessage());
            return badRequest("Erro interno de sistema.");
        }

        return ok(Json.toJson(artigo));
    }

    /**
     * return the pdf from a titulo
     *
     * @param titulo
     * @return ok pdf by name
     */
    public Result pdf(String titulo) {

        String diretorioDePdfs = Play.application().configuration().getString("diretorioDePdfs");
        String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");

        File pdf = new File(diretorioDePdfs,titulo+extensaoPadraoDePdfs);

        try {
            return ok(new FileInputStream(pdf));
        } catch (FileNotFoundException e) {
            return notFound("Arquivo não encontrado.");
        } catch (Exception e) {
            return badRequest("Erro interno de sistema.");
        }

    }

}
