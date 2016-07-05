package controllers;

import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.game;
import views.html.home;
import views.html.index;
import views.html.login;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Application extends Controller {

    @Inject
    public Application() {
    }

    @Transactional
    public Result index() {
        return ok(index.render());
    }

    @Transactional
    public  Result home() {
        String player = session("player");
        if ( player == null)  return ok(home.render(request(), player, "", ""));//TODO le tengo q pasar vacio??
        else {
            session("player", player);
            return ok(home.render(request(), player, "", ""));//TODO le tengo q pasar vacio??
        }
    }

    @Transactional
    public Result play() {
        if (session("player") == null) return forbidden(login.render());
        else return ok(game.render(request()));
    }

    @Transactional
    public Result login() {
        return ok(login.render());
    }

}