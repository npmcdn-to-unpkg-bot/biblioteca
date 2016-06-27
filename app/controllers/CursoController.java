package controllers;

import com.avaje.ebean.Ebean;
import models.Curso;
import models.Usuario;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.validators.CursoFormData;

import javax.annotation.Nullable;
import java.util.List;

import static play.data.Form.form;

public class CursoController extends Controller {

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

            //apos o objeto ser instanciado passamos os dados para o eventoformdata e os dados serao carregados no form edit
            Form<CursoFormData> formData = Form.form(CursoFormData.class).fill(cursoFormData);

            return ok(views.html.admin.cursos.edit.render(id,formData));
        } catch (Exception e) {
            mensagem = "Erro interno de sistema";
            tipoMensagem = "Erro";
            Logger.error(e.getMessage());
            return badRequest(views.html.mensagens.evento.mensagens.render(mensagem,tipoMensagem));
        }
    }

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
                //Converte os dados do formularios para uma instancia do Evento
                Curso curso = Curso.makeInstance(formData.get());

                //faz uma busca na base de dados do curso
                Curso cursoBusca = Ebean.find(Curso.class).where().eq("nome", formData.data().get("nome")).findUnique();

                if (cursoBusca != null) {
                    formData.reject("O Curso com o nome'" + cursoBusca.getNome() + "' já esta Cadastrado!");
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

        //verificar se tem erros no formData, caso tiver erros retorna o formulario com os erros caso não tiver continua o processo de alteracao do curso
        if (formData.hasErrors()) {
            return badRequest(views.html.admin.cursos.edit.render(id,formData));
        } else {
            try {
                Curso cursoBusca = Ebean.find(Curso.class, id);

                if (cursoBusca == null) {
                    return notFound(views.html.mensagens.erro.naoEncontrado.render("Curso não encontrado"));
                }

                //Converte os dados do formulario para uma instancia do Curso
                Curso curso = Curso.makeInstance(formData.get());

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

            Ebean.delete(curso);
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
