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
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class ArtigoController extends Controller {

    String mensagem = "";
    String tipoMensagem = "";

    private static DynamicForm form = Form.form();

    public Form<Artigo> artigoForm = Form.form(Artigo.class);

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

        return ok(views.html.admin.artigos.create.render(form));
    }

    /**
     * Save a artigo
     *
     * @return a render view to inform OK
     */
    public Result inserir() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String titulo = formPreenchido.data().get("titulo");
        String resumo = formPreenchido.data().get("resumo");

        //valida se o email e a senha não estejam vazios
        if (titulo.equals("") || resumo.equals("")) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Título ou Resumo não podem estar vazios!");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
        }

        //faz uma busca do artigo na base de dados
        Artigo artigoBusca = Ebean.find(Artigo.class).where().eq("titulo", formPreenchido.data().get("titulo")).findUnique();

        if (artigoBusca != null) {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Artigo '" + artigoBusca.getTitulo() + "' já esta Cadastrado!");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
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
                return badRequest(views.html.admin.artigos.create.render(formDeErro));
            }
        } else {
            DynamicForm formDeErro = form.fill(formPreenchido.data());
            formDeErro.reject("Selecione um arquivo no formato PDF");
            return badRequest(views.html.admin.artigos.create.render(formDeErro));
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

        return ok(views.html.mensagens.artigo.cadastrado.render(titulo));
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

        List<Artigo> artigos = Ebean.find(Artigo.class).findList();
        return ok(views.html.admin.artigos.list.render(artigos,""));
    }

    /**
     * Retrieve a list of all artigos
     *
     * @return a list of all artigos in json
     */
    public Result buscaTodos() {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
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

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
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
     * @return ok artigo removed
     */
    public Result remover(Long id) {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        String diretorioDePdfs = Play.application().configuration().getString("diretorioDePdfs");
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
        Artigo artigo = Ebean.find(Artigo.class, id);

        if (artigo == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
        }

        File pdf = new File(diretorioDePdfs,artigo.getTitulo()+extensaoPadraoDePdfs);

        try {
            Ebean.delete(artigo);
            pdf.delete();
            mensagem = "Artigo excluído com sucesso";
            tipoMensagem = "Sucesso";
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
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
            return notFound(views.html.mensagens.erro.naoEncontrado.render(titulo+extensaoPadraoDePdfs+" não foi encontrado"));
        } catch (Exception e) {
            return badRequest("Erro interno de sistema.");
        }

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

        Artigo artigo = Ebean.find(Artigo.class, id);

        if (artigo == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
        }

        return ok(views.html.admin.artigos.detail.render(artigo));
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

        Form<Artigo> artigoForm = form(Artigo.class).fill(Artigo.find.byId(id));


        if (artigoForm == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Artigo não encontrado"));
        }

        return ok(views.html.admin.artigos.edit.render(id,artigoForm));
    }

    /**
     * Update a artigo from id
     *
     * @param id
     * @return a artigo updated with a form
     */
    public Result editar(Long id) {

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Artigo artigoBusca = Ebean.find(Artigo.class, id);

        if (artigoBusca == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Usuário não encontrado"));
        }

        Form<Artigo> form = artigoForm.fill(Artigo.find.byId(id)).bindFromRequest();

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
                artigoForm.reject("Apenas arquivos em formato PDF é aceito");
                return badRequest(views.html.admin.artigos.edit.render(id,artigoForm));
            }
        } else {
            artigoForm.reject("Selecione um arquivo no formato PDF");
            return badRequest(views.html.admin.artigos.edit.render(id,artigoForm));
        }

        try {
            Artigo artigo = form.get();
            artigo.setId(id);
            artigo.setDataAlteracao(new Date());
            artigo.update();
            tipoMensagem = "Sucesso";
            mensagem = "Artigo atualizado com sucesso.";
        } catch (IllegalStateException e) {
            artigoForm.reject("Os campos título, resumo não podem estar vazios!");
            return badRequest(views.html.admin.artigos.edit.render(id,artigoForm));
        } catch (Exception e) {
            tipoMensagem = "Erro";
            mensagem = "Erro Interno de sistema.";
            Logger.info(e.getMessage());
            return badRequest(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
        }

        return ok(views.html.mensagens.artigo.mensagens.render(mensagem,tipoMensagem));
    }

}
