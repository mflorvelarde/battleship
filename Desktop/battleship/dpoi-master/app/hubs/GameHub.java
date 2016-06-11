package hubs;

import actors.Robot;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import signalJ.services.Hub;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tomasnajun on 01/06/16.
 */
public class GameHub extends Hub<GamePage> {
    private final static Map<String, Set<String>> games = new HashMap<>();
    private final static Map<UUID, String> connectionsToUsers = new HashMap<>();
    private static ActorRef robot;

    public boolean login(String username) {
        if(connectionsToUsers.containsValue(username) || username.equals("Robot")) return false;
        clients().callerState.put("username", username);
        joinGame("Lobby");
        clients().caller.gameList(getGameList());
        connectionsToUsers.putIfAbsent(context().connectionId, clients().callerState.get("username"));
        return true;
    }

    public void logout() {
        connectionsToUsers.remove(context().connectionId);
        removeUserFromGame(clients().callerState.get("username"));
        clients().callerState.put("username", "");
    }

    public void joinGame(String game) {
        if(games.containsKey(game)) joinGame(game, false);
        else createGame(game);
        clients().group(game).userList(getUserList(game));
    }

    private void joinGame(String game, boolean fromCreate) {
        boolean changed = removeUserFromGame(clients().callerState.get("username"));
        addUserToGame(clients().callerState.get("username"), game);
        if(fromCreate || changed) clients().all.gameList(getGameList());
    }

    public void createGame(String game) {
        games.putIfAbsent(game, new HashSet<>());
        joinGame(game, true);
    }

    public void sendMessage(String game, String message) {
        clients().othersInGroup(game).sendMessage(clients().callerState.get("username"), message);
    }

    private void addUserToGame(String username, String game) {
        games.get(game).add(username);
        groups().add(context().connectionId, game);
        clients().othersInGroup(game).userJoinedGame(username);
    }

    private boolean removeUserFromGame(String username) {
        String game = null;
        String removekey = null;
        for(String key : games.keySet()) {
            if(games.get(key).remove(username)) {
                game = key;
                clients().othersInGroup(key).userLeftGame(username);
                clients().othersInGroup(key).userList(getUserList(key));
                if(games.get(key).size() == 0 && !key.equalsIgnoreCase("Lobby")) removekey = key;
                break;
            }
        }
        if (game != null) groups().remove(context().connectionId, game);
        if(removekey != null) {
            games.remove(removekey);
            return true;
        }
        return false;
    }

    private Set<String> getUserList(String game) {
        final Set<String> userlist = new HashSet<>(games.get(game));
        userlist.add("Robot");
        return userlist;
    }

    private Set<String> getGameList() {
        return new HashSet<>(games.keySet());
    }

    @Override
    protected Class<GamePage> getInterface() {
        return GamePage.class;
    }

    @Override
    public void onDisconnected() {
        final String username = connectionsToUsers.remove(context().connectionId);
        Logger.debug("Disconnect: " + username);
        removeUserFromGame(username);
    }

    @Override
    public void onConnected() {
        if(robot == null) {
            robot = Akka.system().actorOf(Props.create(Robot.class), "robot");
            Akka.system().scheduler().schedule(
                    Duration.create(5, TimeUnit.SECONDS),
                    Duration.create(30, TimeUnit.SECONDS),
                    robot,
                    "tick",
                    Akka.system().dispatcher(),
                    ActorRef.noSender()
            );
        }
    }
}
