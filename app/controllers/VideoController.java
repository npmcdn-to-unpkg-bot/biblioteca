package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Usuario;
import models.Video;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.validators.VideoFormData;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class VideoController extends Controller {

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
     * @return autenticado form if auth OK or not authorized
     */
    public Result telaNovo() {
        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            return notFound(views.html.mensagens.erro.naoEncontrado.render("Vídeo não encontrado"));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        Form<VideoFormData> videoForm = form(VideoFormData.class);

        return ok(views.html.admin.videos.create.render(videoForm));
    }

    /**
     * Retrieve a list of all videos
     *
     * @return a list of all videos in a render template
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
            List<Video> videos = Ebean.find(Video.class).findList();
            return ok(views.html.admin.videos.list.render(videos,""));
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
            Video video = Ebean.find(Video.class, id);

            if (video == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Vídeo não encontrado"));
            }

            return ok(views.html.admin.videos.detail.render(video));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    /**
     * @return render edit form with a video data
     */
    public Result telaEditar(Long id) {
        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuário não autenticado";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            //logica onde instanciamos um objeto evento que esteja cadastrado na base de dados
            VideoFormData videoFormData = (id == 0) ? new VideoFormData() : models.Video.makeVideoFormData(id);

            //apos o objeto ser instanciado passamos os dados para o eventoformdata e os dados serao carregados no form edit
            Form<VideoFormData> formData = Form.form(VideoFormData.class).fill(videoFormData);

            return ok(views.html.admin.videos.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Save a video
     *
     * @return a render view to inform OK
     */
    public Result inserir() {
        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<VideoFormData> formData = Form.form(VideoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.videos.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.videos.create.render(formData));
        }

        //se existir erros nos campos do formulario retorne o VideoFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.videos.create.render(formData));
        } else {
            try {
                //Converte os dados do formularios para uma instancia do Video
                Video video = Video.makeInstance(formData.get());

                //faz uma busca na base de dados do video
                Video videoBusca = Ebean.find(Video.class).where().eq("titulo", formData.data().get("titulo")).findUnique();

                if (videoBusca != null) {
                    formData.reject("O Vídeo com o nome'" + videoBusca.getTitulo() + "' já esta Cadastrado!");
                    return badRequest(views.html.admin.videos.create.render(formData));
                }

                video.setDataCadastro(new Date());
                video.save();

                return created(views.html.mensagens.video.cadastrado.render(video.getTitulo()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.videos.create.render(formData));

            }

        }
    }

    /**
     * Update a video from id
     *
     * @param id
     * @return a video updated with a form
     */
    public Result editar(Long id) {
        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<VideoFormData> formData = Form.form(VideoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.videos.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.videos.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver erros retorna o formulario com os erros caso não tiver continua o processo de alteracao do video
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.videos.edit.render(id,formData));
        } else {
            try {
                Video videoBusca = Ebean.find(Video.class, id);

                if (videoBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Vídeo não encontrado"));
                }

                //Converte os dados do formulario para uma instancia do Video
                Video video = Video.makeInstance(formData.get());

                video.setId(id);
                video.setDataAlteracao(new Date());
                video.update();
                tipoMensagem = "Sucesso";
                mensagem = "Vídeo atualizado com sucesso.";
                return ok(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
                return badRequest(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
            }

        }
    }

    /**
     * Remove a video from a id
     *
     * @param id
     * @return ok video removed
     */
    public Result remover(Long id) {
        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuario não autenticado";
            tipoMensagem = "Erro";
            return notFound(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            mensagem = "Você não tem privilégios de Administrador";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        }

        try {
            //busca o video para ser excluido
            Video video = Ebean.find(Video.class, id);

            if (video == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Vídeo não encontrado"));
            }

            Ebean.delete(video);
            mensagem = "Vídeo excluído com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.video.mensagens.render(mensagem,tipoMensagem));
        }
    }

    /**
     * Retrieve a list of all videos
     *
     * @return a list of all videos in json
     */
    public Result buscaTodos() {
        try {
            return ok(Json.toJson(Ebean.find(Video.class).findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }
    }
}
