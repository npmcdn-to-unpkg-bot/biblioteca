package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Curso;
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
import views.validators.CursoFormData;

import javax.annotation.Nullable;
import java.io.File;
import java.text.Normalizer;
import java.util.List;

import static play.data.Form.form;

public class CursoController extends Controller {

    /**
     * metodo responsavel por modificar o titulo do arquivo
     *
     * @param str
     * @return a string formatada
     */
    @Security.Authenticated(Secured.class)
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
     * @return curso form if auth OK or not authorized
     */
    @Security.Authenticated(Secured.class)
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

        Form<CursoFormData> cursoForm = form(CursoFormData.class);

        return ok(views.html.admin.cursos.create.render(cursoForm));
    }

    /**
     * Retrieve a list of all cursos
     *
     * @return a list of all cursos in a render template
     */
    @Security.Authenticated(Secured.class)
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
            List<Curso> cursos = Ebean.find(Curso.class).findList();
            return ok(views.html.admin.cursos.list.render(cursos,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render a detail form with a curso data
     */
    @Security.Authenticated(Secured.class)
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
            Curso curso = Ebean.find(Curso.class, id);

            if (curso == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Curso não encontrado"));
            }

            return ok(views.html.admin.cursos.detail.render(curso));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render edit form with a curso data
     */
    @Security.Authenticated(Secured.class)
    public Result telaEditar(Long id) {
        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuário não autenticado";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            //logica onde instanciamos um objeto evento que esteja cadastrado na base de dados
            CursoFormData cursoFormData = (id == 0) ? new CursoFormData() : models.Curso.makeCursoFormData(id);

            //apos o objeto ser instanciado novomos os dados para o eventoformdata e os dados serao carregados no form edit
            Form<CursoFormData> formData = Form.form(CursoFormData.class).fill(cursoFormData);

            return ok(views.html.admin.cursos.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Save a curso
     *
     * @return a render view to inform OK
     */
    @Security.Authenticated(Secured.class)
    public Result inserir() {
        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<CursoFormData> formData = Form.form(CursoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.cursos.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.cursos.create.render(formData));
        }

        //se existir erros nos campos do formulario retorne o CursoFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.cursos.create.render(formData));
        } else {
            try {
                //Converte os dados do formularios para uma instancia do Curso
                Curso curso = Curso.makeInstance(formData.get());

                //faz uma busca na base de dados do curso
                Curso cursoBusca = Ebean.find(Curso.class).where().eq("nome", formData.data().get("nome")).findUnique();

                if (cursoBusca != null) {
                    formData.reject("O Curso '" + cursoBusca.getNome() + "' já esta Cadastrado!");
                    return badRequest(views.html.admin.cursos.create.render(formData));
                }

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");
                String diretorioDeFotosCursos = Play.application().configuration().getString("diretorioDeFotosCursos");
                String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("nome");

                    //solucao para tirar os espacos em branco, acentos do nome do arquivo e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    curso.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        if (file.renameTo(new File(diretorioDeFotosCursos,jpg))) {
                            Logger.info("File Curso is created!");
                        } else {
                            Logger.error("Failed to create file Cursp!");
                            formData.reject("Erro ao salvar o arquivo JPEG. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.cursos.create.render(formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.cursos.create.render(formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.cursos.create.render(formData));
                }

                curso.save();
                return created(views.html.mensagens.curso.cadastrado.render(curso.getNome()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.cursos.create.render(formData));
            }

        }
    }

    /**
     * Update a curso from id
     *
     * @param id
     * @return a curso updated with a form
     */
    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {
        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<CursoFormData> formData = Form.form(CursoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.cursos.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.cursos.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver retorna o formulario com os erros e caso não tiver continua o processo de alteracao do curso
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.cursos.edit.render(id,formData));
        } else {
            try {
                Curso cursoBusca = Ebean.find(Curso.class, id);

                if (cursoBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Curso não encontrado"));
                }

                //Converte os dados do formularios para uma instancia do Curso
                Curso curso = Curso.makeInstance(formData.get());

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("nome");

                    //solucao para tirar os espacos em branco do nome do arquivo, acentos e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    curso.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();

                    String diretorioDeFotosCursos = Play.application().configuration().getString("diretorioDeFotosCursos");
                    String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                    //necessario para excluir o arquivo jpeg antigo
                    File jpgAntigo = new File(diretorioDeFotosCursos,cursoBusca.getNomeCapa());

                    if (jpgAntigo.delete()) {
                        Logger.info("File Curso is deleted!");
                    } else {
                        Logger.error("Failed to edit file Curso!");
                    }

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        if (file.renameTo(new File(diretorioDeFotosCursos,jpg))) {
                            Logger.info("File Curso is edited!");
                        } else {
                            Logger.error("Failed to edit file Curso!");
                            formData.reject("Erro ao salvar o arquivo JPEG. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.cursos.edit.render(id,formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.cursos.edit.render(id,formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.cursos.edit.render(id,formData));
                }

                curso.setId(id);
                curso.update();
                tipoMensagem = "Sucesso";
                mensagem = "Curso atualizado com sucesso.";
                return ok(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
                return badRequest(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
            }
        }
    }

    /**
     * Remove a curso from a id
     *
     * @param id identificador
     * @return ok curso removed
     */
    @Security.Authenticated(Secured.class)
    public Result remover(Long id) {

        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuario não autenticado";
            tipoMensagem = "Erro";
            return notFound(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        }

        try {
            //busca o curso para ser excluido
            Curso curso = Ebean.find(Curso.class, id);

            if (curso == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Curso não encontrado"));
            }

            String diretorioDeFotosCursos = Play.application().configuration().getString("diretorioDeFotosCursos");

            //necessario para excluir o curso
            File jpg = new File(diretorioDeFotosCursos,curso.getNomeCapa());

            Ebean.delete(curso);

            if (jpg.delete()) {
                Logger.info("File Curso is deleted!");
            } else {
                Logger.error("Failed to delete file Curso!");
            }

            mensagem = "Curso excluído com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.curso.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Retrieve a list of all cursos
     *
     * @return a list of all cursos in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Curso.class)
                    .order()
                    .asc("dataInicio")
                    .findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }
    }
}
