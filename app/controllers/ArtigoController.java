package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Artigo;
import models.Usuario;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class ArtigoController extends Controller {

    /**
     * @return autenticado form if auth OK or login form is auth KO
     */
    public Result novoTela() {
        return ok(views.html.artigo.novo.render());
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
     * @return a artigo json
     */
    public Result inserir() {

        Artigo artigo = Json.fromJson(request().body().asJson(), Artigo.class);

        artigo.setDataCadastro(new Date());

        try {
            Ebean.save(artigo);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest("Erro interno de sistema");
        }

        return created(Json.toJson(artigo));
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
     * upload a pdf file from request
     *
     * @return ok file uploaded
     */
    public Result upload() {

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart pdf = body.getFile("pdf");
        String extensaoPadraoDePdfs = Play.application().configuration().getString("extensaoPadraoDePdfs");
        if (pdf != null) {

            String artigoId = form().bindFromRequest().get("artigoId");
            String arquivo = artigoId + extensaoPadraoDePdfs;
            String contentType = pdf.getContentType();
            File documento = pdf.getFile();

            String diretorioDePdfs = Play.application().configuration().getString("diretorioDePdfs");
            String contentTypePadraoDePdfs = Play.application().configuration().getString("contentTypePadraoDePdfs");

            if (contentType.equals(contentTypePadraoDePdfs)) {

                documento.renameTo(new File(diretorioDePdfs,arquivo));
                return ok("Arquivo carregado com sucesso");

            } else {
                return ok("Arquivo apenas com formato pdf é aceito");

            }

        } else {
            flash("error","Erro ao fazer upload");
            return redirect(routes.Application.index());
        }
    }

    /**
     * return a pdf from a id
     *
     * @param id
     * @return ok pdf
     */
    public Result pdf(Long id) throws IOException {

        String diretorioDePdfs = Play.application().configuration().getString("diretorioDePdfs");

        File pdf = new File(diretorioDePdfs,id + ".pdf");

        return ok(new FileInputStream(pdf));

    }
}
