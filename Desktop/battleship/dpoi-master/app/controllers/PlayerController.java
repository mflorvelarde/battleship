package controllers;

import com.avaje.ebean.Ebean;
import model.Player;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;

import java.util.HashMap;

import static play.libs.Json.toJson;

/**
 * Created by florenciavelarde on 8/6/16.
 */
public class PlayerController extends Controller {
    @Transactional
    public static Result authenticate(String user) {
        HashMap<Long, String> userInfo = parseUser(user);
        String id = userInfo.keySet().iterator().next().toString();

        if (getUserByFacebookId(id) == null) crateUser(id, userInfo.get(id));

        session("player", id);
      //  session().put("player", id);

       // session("player", id);
        return ok(home.render(id));
    }

    private static void crateUser(String id, String name) {
        Player player = new Player(name, Long.parseLong(id));
        Ebean.save(player);
    }

    private static HashMap<Long, String> parseUser (String user) {
        String name = "";
        String id = "";
       for (int i = 0; i < user.length(); i++) {
           if (Character.compare(user.charAt(i), 'n') == 0) {
               if (Character.compare(user.charAt(i + 1), 'a') == 0) {
                   if (Character.compare(user.charAt(i + 2),'m')== 0) {
                       if (Character.compare(user.charAt(i + 3), 'e' ) == 0) {
                           //i+4= '=' i+5 = '''
                         for (int j = i+7; j < user.length(); j++) {
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

    public static Result getUserByFacebookId(String facebookId) {
        Player player = Ebean.find(Player.class).where().eq("FACEBOOK_ID", Long.parseLong(facebookId)).findUnique();
        if (player == null) return null;
        else return ok(toJson(player));
    }
}