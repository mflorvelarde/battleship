package controllers;

import com.avaje.ebean.Ebean;
import model.Statistics;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import static play.libs.Json.toJson;

/**
 * Created by florenciavelarde on 15/6/16.
 */
@Singleton
public class StatisticsController extends Controller {

    @Inject
    public StatisticsController() {
    }

    @Transactional
    public Result getPlayerStattistics(String player) {
        Statistics statistics = Ebean.find(Statistics.class).where().eq("PLAYER", Long.parseLong(player)).findUnique();
        return ok(toJson(statistics));
    }
}