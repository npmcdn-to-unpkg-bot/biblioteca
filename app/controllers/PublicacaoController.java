package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Publicacao;
import models.Usuario;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.validators.PublicacaoFormData;

import javax.annotation.Nullable;
import java.text.Normalizer;
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
            //logica onde instanciamos um objeto curso que esteja cadastrado na base de dados
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
    public Result inserir() {return TODO;}

    /**
     * Update a publicacao from id
     *
     * @param id variavel identificadora
     * @return a curso updated with a form
     */
    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {return TODO;}

    /**
     * Remove a publicacao from a id
     *
     * @param id variavel identificadora
     * @return ok curso removed
     */
    @Security.Authenticated(Secured.class)
    public Result remover(Long id) {return TODO;}

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
