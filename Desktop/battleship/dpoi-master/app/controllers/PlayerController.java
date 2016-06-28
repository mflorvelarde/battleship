package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import model.Player;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

import static play.libs.Json.toJson;

/**
 * Created by florenciavelarde on 8/6/16.
 */
@Singleton
public class PlayerController extends Controller {

    @Inject
    public PlayerController() {
    }

    @Transactional
    public Result authenticate(String user) {
        final JsonNode userJson = Json.parse(user);
        final String name = userJson.get("name").textValue();
        final long fbId = userJson.get("id").asLong();

        Player player = Player.findByFacebookId(fbId);
        if (player == null) player = createUser(fbId, name);
        else player = createUser(fbId + 1, "Fake User");

        final String playerId = String.valueOf(player.getId());
        session("player", playerId);

        System.out.println("Login Successful");

        return ok(home.render(request(), playerId, "", ""));
    }

    private Player createUser(long fbId, String name) {
        Player player = new Player(name, String.valueOf(fbId));
        Ebean.save(player);
        return player;
    }

    private HashMap<Long, String> parseUser(String user) {
        String name = "";
        String id = "";
        for (int i = 0; i < user.length(); i++) {
            if (Character.compare(user.charAt(i), 'n') == 0) {
                if (Character.compare(user.charAt(i + 1), 'a') == 0) {
                    if (Character.compare(user.charAt(i + 2), 'm') == 0) {
                        if (Character.compare(user.charAt(i + 3), 'e') == 0) {
                            //i+4= '=' i+5 = '''
                            for (int j = i + 7; j < user.length(); j++) {
                                if (Character.compare(user.charAt(j), '"') == 0) {
                                    break;
                                } else name = name + user.charAt(j);
                            }
                        }
                    }
                }
            } else {
                if (user.charAt(i) == 'i') {
                    if (user.charAt(i + 1) == 'd') {
                        //i+2= '=' i+3 = '''
                        for (int j = i + 5; j < user.length(); j++) {
                            if (user.charAt(j) == '"') {
                                break;
                            } else id = id + user.charAt(j);
                        }
                    }
                }
            }
        }

        Long facebookId = Long.parseLong(id);
        HashMap<Long, String> result = new HashMap<>();
        result.put(facebookId, name);
        return result;
    }

    public Result getUserByFacebookId(long facebookId) {
        final Player player = Player.findByFacebookId(facebookId);
        if (player == null) return null;
        else return ok(toJson(player));
    }

}