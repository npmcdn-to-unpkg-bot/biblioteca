package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Publicacao;
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
import views.validators.PublicacaoFormData;

import javax.annotation.Nullable;
import java.io.File;
import java.text.Normalizer;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class PublicacaoController extends Controller {

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
     * @return publicacao form if auth OK or not authorized
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

        Form<PublicacaoFormData> publicacaoForm = form(PublicacaoFormData.class);

        return ok(views.html.admin.publicacoes.create.render(publicacaoForm));
    }

    /**
     * Retrieve a list of all publicacoes
     *
     * @return a list of all publicacoes in a render template
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
            List<Publicacao> publicacoes = Ebean.find(Publicacao.class).findList();
            return ok(views.html.admin.publicacoes.list.render(publicacoes,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render a detail form with a publicacao data
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
            Publicacao publicacao = Ebean.find(Publicacao.class, id);

            if (publicacao == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Publicação não encontrada"));
            }

            return ok(views.html.admin.publicacoes.detail.render(publicacao));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render edit form with a publicacao data
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
            //logica onde instanciamos um objeto publicacoes que esteja cadastrado na base de dados
            PublicacaoFormData publicacaoFormData = (id == 0) ? new PublicacaoFormData() : models.Publicacao.makePublicacaoFormData(id);

            //apos o objeto ser instanciado levamos os dados para o Publicacaoformdata e os dados serao carregados no form edit
            Form<PublicacaoFormData> formData = Form.form(PublicacaoFormData.class).fill(publicacaoFormData);

            return ok(views.html.admin.publicacoes.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Save a publicacao
     *
     * @return a render view to inform OK
     */
    @Security.Authenticated(Secured.class)
    public Result inserir() {
        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<PublicacaoFormData> formData = Form.form(PublicacaoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.publicacoes.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.publicacoes.create.render(formData));
        }

        //se existir erros nos campos do formulario retorne o PublicacaoFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.publicacoes.create.render(formData));
        } else {
            try {
                //Converte os dados do formularios para uma instancia de Publicacao
                Publicacao publicacao = Publicacao.makeInstance(formData.get());

                //faz uma busca na base de dados de publicacao
                Publicacao publicacaoBusca = Ebean.find(Publicacao.class).where().eq("titulo", formData.data().get("titulo")).findUnique();

                if (publicacaoBusca != null) {
                    formData.reject("A Publicação '" + publicacaoBusca.getTitulo() + "' já esta Cadastrado!");
                    return badRequest(views.html.admin.publicacoes.create.render(formData));
                }

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("titulo");

                    //solucao para tirar os espacos em branco, acentos do titulo do arquivo e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    publicacao.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();
                    String diretorioDeFotosPublicacoes = Play.application().configuration().getString("diretorioDeFotosPublicacoes");
                    String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        file.renameTo(new File(diretorioDeFotosPublicacoes,jpg));
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.publicacoes.create.render(formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.publicacoes.create.render(formData));
                }

                publicacao.setDataCadastro(new Date());
                publicacao.save();
                return created(views.html.mensagens.publicacao.cadastrado.render(publicacao.getTitulo()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.publicacoes.create.render(formData));
            }

        }
    }

    /**
     * Update a publicacao from id
     *
     * @param id variavel identificadora
     * @return a publicacoes updated with a form
     */
    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {
        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<PublicacaoFormData> formData = Form.form(PublicacaoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.publicacoes.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.publicacoes.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver retorna o formulario com os erros e caso não tiver continua o processo de alteracao do publicacoes
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.publicacoes.edit.render(id,formData));
        } else {
            try {
                Publicacao publicacaoBusca = Ebean.find(Publicacao.class, id);

                if (publicacaoBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Publicação não encontrada"));
                }

                //Converte os dados do formularios para uma instancia do Publicacao
                Publicacao publicacao = Publicacao.makeInstance(formData.get());

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart arquivo = body.getFile("arquivo");

                String extensaoPadraoDeJpg = Play.application().configuration().getString("extensaoPadraoDeJpg");

                if (arquivo != null) {
                    String arquivoTitulo = form().bindFromRequest().get("titulo");

                    //solucao para tirar os espacos em branco do titulo do arquivo, acentos e deixa-lo tudo em minusculo
                    arquivoTitulo = formatarTitulo(arquivoTitulo);

                    String jpg = arquivoTitulo + extensaoPadraoDeJpg;

                    publicacao.setNomeCapa(jpg);

                    String tipoDeConteudo = arquivo.getContentType();
                    File file = arquivo.getFile();

                    String diretorioDeFotosPublicacoes = Play.application().configuration().getString("diretorioDeFotosPublicacoes");
                    String contentTypePadraoDeImagens = Play.application().configuration().getString("contentTypePadraoDeImagens");

                    //necessario para excluir o arquivo jpeg antigo
                    File jpgAntigo = new File(diretorioDeFotosPublicacoes,publicacaoBusca.getNomeCapa());

                    //exclui o arquivo jpg antigo
                    jpgAntigo.delete();

                    if (tipoDeConteudo.equals(contentTypePadraoDeImagens)) {
                        file.renameTo(new File(diretorioDeFotosPublicacoes,jpg));
                    } else {
                        formData.reject("Apenas arquivos em formato JPEG é aceito");
                        return badRequest(views.html.admin.publicacoes.create.render(formData));
                    }
                } else {
                    formData.reject("Selecione um arquivo no formato JPEG");
                    return badRequest(views.html.admin.publicacoes.create.render(formData));
                }

                publicacao.setId(id);
                publicacao.setDataAlteracao(new Date());
                publicacao.update();
                tipoMensagem = "Sucesso";
                mensagem = "Publicação atualizado com sucesso.";
                return ok(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
                return badRequest(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
            }
        }
    }

    /**
     * Remove a publicacao from a id
     *
     * @param id variavel identificadora
     * @return ok publicacoes removed
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
            return notFound(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        }

        try {
            //busca o publicacoes para ser excluido
            Publicacao publicacao = Ebean.find(Publicacao.class, id);

            if (publicacao == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Publicação não encontrado"));
            }

            String diretorioDeFotosPublicacoes = Play.application().configuration().getString("diretorioDeFotosPublicacoes");

            //necessario para excluir o publicacoes
            File jpg = new File(diretorioDeFotosPublicacoes,publicacao.getNomeCapa());

            Ebean.delete(publicacao);
            jpg.delete();
            mensagem = "Publicação excluído com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.publicacao.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Retrieve a list of all publicacao
     *
     * @return a list of all publicacao in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Publicacao.class)
                    .order()
                    .asc("titulo")
                    .findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }
    }
}
