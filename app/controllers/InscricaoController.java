package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.validators.InscricaoFormData;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.TreeMap;

import static play.data.Form.form;

public class InscricaoController extends Controller {

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
     * @return render a inscricao form
     */
    public Result telaInscricao(long id) {

        InscricaoFormData inscricaoData = (id == 0) ? new InscricaoFormData() : models.Inscricao.makeInscricaoFormData(id);

        Form<InscricaoFormData> inscricaoForm = form(InscricaoFormData.class);

        return ok(views.html.inscricao.create.render(inscricaoForm, Escolaridade.getNameList(), Genero.getNameList(), Pais.makePaisMap(inscricaoData), Modalidade.getNameList(), Fonte.getNameList()));
    }

    public Result inserir(long id) {

        InscricaoFormData inscricaoData = (id == 0) ? new InscricaoFormData() : models.Inscricao.makeInscricaoFormData(id);

        //Resgata os dados do formulario atraves de uma requisicao e realiza a validacao dos campos
        Form<InscricaoFormData> formData = Form.form(InscricaoFormData.class).bindFromRequest();

        //se existir erros nos campos do formulario retorne o LivroFormData com os erros
        if (formData.hasErrors()) {
            return badRequest(views.html.inscricao.create.render(formData, Escolaridade.getNameList(), Genero.getNameList(), Pais.makePaisMap(inscricaoData), Modalidade.getNameList(), Fonte.getNameList()));
        }
        else {
            try {
                //Converte os dados do formularios para uma instancia do Livro
                Inscricao inscricao = Inscricao.makeInstance(formData.get());

                inscricao.setDataInscricao(new Date());
                inscricao.save();
                return created(views.html.mensagens.inscricao.cadastrado.render(inscricao.getNome()));
            } catch (Exception e) {
                Logger.error(e.getMessage());
                formData.reject("Não foi possível cadastrar, erro interno de sistema.");
                return badRequest(views.html.inscricao.create.render(formData, Escolaridade.getNameList(), Genero.getNameList(), Pais.makePaisMap(inscricaoData), Modalidade.getNameList(), Fonte.getNameList()));

            }

        }

    }

}
