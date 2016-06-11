package controllers;

import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;
import views.html.index;

public class Application extends Controller {

    @Transactional
    public static Result index() {
        return ok(index.render());
    }

    @Transactional
    public static Result home() {
        return ok(home.render());
    }
}
