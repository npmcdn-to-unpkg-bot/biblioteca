package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;

public class AdminController extends Controller {

    public Result inicio() {
        return ok(views.html.admin.inicio.render());
    }
}
