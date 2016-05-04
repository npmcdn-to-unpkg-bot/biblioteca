package controllers;

import com.avaje.ebean.Ebean;
import models.Teste;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class TesteController extends Controller {

    private static DynamicForm form = Form.form();

    public Result lista(){
        List<Teste> listaTeste = Ebean.find(Teste.class).findList();
        return ok(views.html.testes.list.render(listaTeste));
    }

    public Result novo(){
        return ok(views.html.testes.create.render(form));
    }

    public Result detalhar(Long id) {
        return TODO;
    }

    public Result gravar() {
        Form<DynamicForm.Dynamic> formPreenchido = form.bindFromRequest();

        String nome = formPreenchido.data().get("nome");

        Teste teste = new Teste();
        teste.setNome(nome);
        Ebean.save(teste);
        return redirect(routes.TesteController.lista());
    }

    public Result alterar(Long id) {
        return TODO;
    }

    public Result remover(Long id) {
        return TODO;
    }
}
