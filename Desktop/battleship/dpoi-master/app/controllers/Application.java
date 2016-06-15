package controllers;

import model.Statistics;
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
        if (session("player") == null) return forbidden(login.render());
        return ok(home.render(session("player")));
    }

    @Transactional
    public static Result play() {
        if (session("player") == null) return forbidden(login.render());
        else return ok(game.render());
    }

    @Transactional
    public static Result login() {
        return ok(login.render());
    }

}