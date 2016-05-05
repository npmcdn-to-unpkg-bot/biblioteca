package controllers;

import com.avaje.ebean.Ebean;
import models.Teste;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.data.Form.form;

public class TesteController extends Controller {

    public Form<Teste> testeForm = Form.form(Teste.class);

    public Result lista(){
        List<Teste> listaTeste = Ebean.find(Teste.class).findList();
        return ok(views.html.testes.list.render(listaTeste));
    }

    public Result novo(){
        return ok(views.html.testes.create.render(testeForm));
    }

    public Result detalhar(Long id) {
        Form<Teste> testeForm = form(Teste.class).fill(Teste.find.byId(id));
        return ok(views.html.testes.edit.render(id,testeForm));
    }

    public Result gravar() {
        Form<Teste> testeForm = form(Teste.class).bindFromRequest();
        testeForm.get().save();
        return redirect(routes.TesteController.lista());
    }

    public Result alterar(Long id) {

        Form<Teste> form = testeForm.fill(Teste.find.byId(id)).bindFromRequest();

        if(testeForm.hasErrors()){
            return badRequest(views.html.testes.edit.render(id, testeForm));
        }

        Teste teste = form.get();

        //precisa setar o id, pois por algum motivo ele se perde no formulário
        teste.setId(id);

        teste.update();

        return redirect(routes.TesteController.lista());
    }

    public Result remover(Long id) {
        Teste.find.ref(id).delete();
        return redirect(routes.TesteController.lista());
    }
}
