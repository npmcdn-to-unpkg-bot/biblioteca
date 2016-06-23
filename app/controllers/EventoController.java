package controllers;

import actions.Secured;
import com.avaje.ebean.Ebean;
import models.Evento;
import models.Usuario;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.validators.EventoFormData;

import javax.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.Year;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class EventoController extends Controller {

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

        Form<EventoFormData> eventoForm = form(EventoFormData.class);

        return ok(views.html.admin.eventos.create.render(eventoForm));
    }

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
            List<Evento> eventos = Ebean.find(Evento.class).findList();
            return ok(views.html.admin.eventos.list.render(eventos,""));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

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
            Evento evento = Ebean.find(Evento.class, id);

            if (evento == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Evento não encontrado"));
            }

            return ok(views.html.admin.eventos.detail.render(evento));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(views.html.error.render(e.getMessage()));
        }
    }

    @Security.Authenticated(Secured.class)
    public Result telaEditar(Long id) {
        String mensagem;
        String tipoMensagem;

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            mensagem = "Usuário não autenticado";
            tipoMensagem = "Erro";
            return badRequest(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            return badRequest(views.html.mensagens.erro.naoAutorizado.render());
        }

        try {
            //logica onde instanciamos um objeto evento que esteja cadastrado na base de dados
            EventoFormData eventoFormData = (id == 0) ? new EventoFormData() : models.Evento.makeEventoFormData(id);

            //apos o objeto ser instanciado passamos os dados para o eventoformdata e os dados serao carregados no form edit
            Form<EventoFormData> formData = Form.form(EventoFormData.class).fill(eventoFormData);

            return ok(views.html.admin.eventos.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        }
    }

    @Security.Authenticated(Secured.class)
    public Result inserir() {
        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<EventoFormData> formData = Form.form(EventoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.eventos.create.render(formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.eventos.create.render(formData));
        }

        //se existir erros nos campos do formulario retorne o EventoFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.eventos.create.render(formData));
        } else {
            try {
                //Converte os dados do formularios para uma instancia do Evento
                Evento evento = Evento.makeInstance(formData.get());

                //faz uma busca na base de dados do evento
                Evento eventoBusca = Ebean.find(Evento.class).where().eq("nome", formData.data().get("nome")).findUnique();

                if (eventoBusca != null) {
                    formData.reject("O Evento '" + eventoBusca.getNome() + "' já esta Cadastrado!");
                    return badRequest(views.html.admin.eventos.create.render(formData));
                }

                evento.save();
                return created(views.html.mensagens.evento.cadastrado.render(evento.getNome()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.admin.eventos.create.render(formData));

            }

        }
    }

    @Security.Authenticated(Secured.class)
    public Result editar(Long id) {
        String mensagem;
        String tipoMensagem;

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<EventoFormData> formData = Form.form(EventoFormData.class).bindFromRequest();

        //busca o usuário atual que esteja logado no sistema
        Usuario usuarioAtual = atual();

        if (usuarioAtual == null) {
            formData.reject("Usuário não autenticado");
            return badRequest(views.html.admin.eventos.edit.render(id,formData));
        }

        //verificar se o usuario atual encontrado é administrador
        if (usuarioAtual.getPrivilegio() != 1) {
            formData.reject("Você não tem privilégios de Administrador");
            return badRequest(views.html.admin.eventos.edit.render(id,formData));
        }

        //verificar se tem erros no formData, caso tiver erros retorna o formulario com os erros caso não tiver continua o processo de alteracao do evento
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.eventos.edit.render(id,formData));
        } else {
            try {
                Evento eventoBusca = Ebean.find(Evento.class, id);

                if (eventoBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Evento não encontrado"));
                }

                //Converte os dados do formularios para uma instancia do Evento
                Evento evento = Evento.makeInstance(formData.get());

                evento.setId(id);
                evento.update();
                tipoMensagem = "Sucesso";
                mensagem = "Evento atualizado com sucesso.";
                return ok(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
            } catch (Exception e) {
                tipoMensagem = "Erro";
                mensagem = "Erro Interno de sistema.";
                Logger.error(e.getMessage());
            }

            return badRequest(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        }
    }

    @Security.Authenticated(Secured.class)
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
            //busca o evento para ser excluido
            Evento evento = Ebean.find(Evento.class, id);

            if (evento == null) {
                return notFound(views.html.mensagens.erro.naoEncontrado.render("Evento não encontrado"));
            }

            Ebean.delete(evento);
            mensagem = "Evento excluído com sucesso";
            tipoMensagem = "Sucesso";
            return ok(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        }
    }

    public Result buscaTodos() {

        try {
            return ok(Json.toJson(Ebean.find(Evento.class).findList()));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return badRequest(Json.toJson(Messages.get("error.app")));
        }

    }

}
