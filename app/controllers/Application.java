package controllers;


import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    //@Cached(key = "homePage")
    public Result index() {
        String username = session().get("email");
        return ok(views.html.index.render(username));
    }

}
