package controllers;

import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game;
import views.html.home;
import views.html.index;
import views.html.login;

public class Application extends Controller {

    @Transactional
    public static Result index() {
        return ok(index.render());
    }

    @Transactional
    public static Result home() {
        return ok(home.render());
    }

    @Transactional
    public static Result play() {
        return ok(game.render());
    }

    public static Result login() {
        return ok(login.render());
    }
}