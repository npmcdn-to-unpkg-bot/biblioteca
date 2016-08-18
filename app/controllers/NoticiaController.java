package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Noticia;
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
import views.validators.NoticiaFormData;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class NoticiaController extends Controller {

    /**
     * metodo responsavel por modificar o titulo do arquivo
     *
     * @param str titulo do arquivo a ser modificado
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
     * @return noticia form if auth OK or not authorized
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

        Form<NoticiaFormData> noticiaForm = form(NoticiaFormData.class);

        return ok(views.html.admin.noticias.create.render(noticiaForm));
    }

    /**
     * @return render a detail form with a noticia data
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
            Noticia noticia = Ebean.find(Noticia.class, id);

            if (noticia == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Notícia não encontrada"));
            }

            return ok(views.html.admin.noticias.detail.render(noticia));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render edit form with a noticia data
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
            return badRequest(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            //logica onde instanciamos um objeto noticia que esteja cadastrado na base de dados
            NoticiaFormData noticiaFormData = (id == 0) ? new NoticiaFormData() : models.Noticia.makeNoticiaFormData(id);

            //apos o objeto ser instanciado levamos os dados para o Noticiaformdata e os dados serao carregados no form edit
            Form<NoticiaFormData> formData = Form.form(NoticiaFormData.class).fill(noticiaFormData);

            return ok(views.html.admin.noticias.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Retrieve a list of all noticias
     *
     * @return a list of all noticias in a render template
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
            List<Noticia> noticias = Ebean.find(Noticia.class).findList();
            return ok(views.html.admin.noticias.list.render(noticias,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * Save a noticia
     *
     * @return a render view to inform OK
     */
    @Security.Authenticated(Secured.class)
    public Result inserir() {
        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<NoticiaFormData> formData = Form.form(NoticiaFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.noticias.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.noticias.create.render(formData));
        }

        //se existir erros nos campos do formulario retorne o PublicacaoFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.noticias.create.render(formData));
        } else {
            try {
                //Converte os dados do formularios para uma instancia de Noticia
                Noticia noticia = Noticia.makeInstance(formData.get());

                //faz uma busca na base de dados de publicacao
                Noticia noticiaBusca = Ebean.find(Noticia.class).where().eq("titulo", formData.data().get("titulo")).findUnique();

                if (noticiaBusca != null) {
                    formData.reject("A Noticia '" + noticiaBusca.getTitulo() + "' já esta Cadastrada!");
                    return badRequest(views.html.admin.noticias.create.render(formData));
                }

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");
                String diretorioDeFotosNoticias = Play.application().configuration().getString("diretorioDeFotosNoticias");
                String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("titulo");

                    //solucao para tirar os espacos em branco, acentos do titulo do arquivo e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    noticia.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        if (file.renameTo(new File(diretorioDeFotosNoticias,jpg))) {
                            Logger.info("File Noticia is created!");
                        } else {
                            Logger.error("Failed to create file Noticia!");
                            formData.reject("Erro ao salvar o arquivo JPEG. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.noticias.create.render(formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.noticias.create.render(formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.noticias.create.render(formData));
                }

                noticia.setDataCadastro(new Date());
                noticia.save();
                return created(views.html.mensagens.noticia.cadastrado.render(noticia.getTitulo()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.noticias.create.render(formData));
            }

        }
    }

    /**
     * Update a noticia from id
     *
     * @param id variavel identificadora
     * @return a noticia updated with a form
     */
    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {
        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<NoticiaFormData> formData = Form.form(NoticiaFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.noticias.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.noticias.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver retorna o formulario com os erros e caso não tiver continua o processo de alteracao do publicacoes
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.noticias.edit.render(id,formData));
        } else {
            try {
                Noticia noticiaBusca = Ebean.find(Noticia.class, id);

                if (noticiaBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Notícia não encontrada"));
                }

                //Converte os dados do formularios para uma instancia do Publicacao
                Noticia noticia = Noticia.makeInstance(formData.get());

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");
                String diretorioDeFotosNoticias = Play.application().configuration().getString("diretorioDeFotosNoticias");
                String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("titulo");

                    //solucao para tirar os espacos em branco do titulo do arquivo, acentos e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    noticia.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();

                    //necessario para excluir o arquivo jpeg antigo
                    File jpgAntigo = new File(diretorioDeFotosNoticias,noticiaBusca.getNomeCapa());

                    if (jpgAntigo.delete()) {
                        Logger.info("File Noticia is deleted!");
                    } else {
                        Logger.error("Failed to edit file Noticia!");
                    }

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        if (file.renameTo(new File(diretorioDeFotosNoticias,jpg))) {
                            Logger.info("File Noticia is edited!");
                        } else {
                            Logger.error("Failed to edit file Noticia!");
                            formData.reject("Erro ao salvar o arquivo JPEG. Verifique se foi criado as pastas no servidor!");
                            return badRequest(views.html.admin.noticias.edit.render(id,formData));
                        }
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.noticias.edit.render(id,formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.noticias.edit.render(id,formData));
                }

                noticia.setId(id);
                noticia.setDataAlteracao(new Date());
                noticia.update();
                tipoMensagem = "Sucesso";
                mensagem = "Notícia atualizada com sucesso.";
                return ok(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
                return badRequest(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
            }
        }
    }

    /**
     * Remove a noticia from a id
     *
     * @param id variavel identificadora
     * @return ok noticia removed
     */
    @Security.Authenticated(Secured.class)
    public Result remover(Long id) {

        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuário não autenticado";
            tipoMensagem = "Erro";
            return notFound(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
        }

        try {
            //busca o publicacoes para ser excluido
            Noticia noticia = Ebean.find(Noticia.class, id);

            if (noticia == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Notícia não encontrada"));
            }

            String diretorioDeFotosNoticias = Play.application().configuration().getString("diretorioDeFotosNoticias");

            //necessario para excluir a foto das noticias
            File jpg = new File(diretorioDeFotosNoticias,noticia.getNomeCapa());

            Ebean.delete(noticia);

            if (jpg.delete()) {
                Logger.info("File Noticia is deleted!");
            } else {
                Logger.error("Failed to delete file Noticia!");
            }

            mensagem = "Notícia excluída com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.noticia.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Retrieve a list of all noticia
     *
     * @return a list of all noticia in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Noticia.class)
                    .order()
                    .desc("dataCadastro")
                    .findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }
    }

    /**
     * return the jpeg from a nameFile
     *
     * @param nomeArquivo variavel string
     * @return ok jpeg by name
     */
    public Result jpg(String nomeArquivo) {

        String diretorioDeFotosNoticias = Play.application().configuration().getString("diretorioDeFotosNoticias");

        try {
            File jpg = new File(diretorioDeFotosNoticias,nomeArquivo);
            return ok(new FileInputStream(jpg)).as("image/jpeg");
        } catch (FileNotFoundException e) {
            Logger.error(e.toString());
            return notFound(e.toString());
        } catch (Exception e) {
            Logger.error(e.toString());
            return badRequest(Messages.get("app.error"));
        }

    }


}
