package controllers;

import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

import java.util.HashMap;
import java.lang.Long;
import java.util.Set;

/**
 * Created by florenciavelarde on 8/6/16.
 */
public class PlayerController extends Controller {
    @Transactional
    public static Result authenticate(String user) {
        System.out.println(user);
        HashMap<Long, String> userInfo = parseUser(user);
        System.out.println(userInfo);
        return ok(login.render());
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
}