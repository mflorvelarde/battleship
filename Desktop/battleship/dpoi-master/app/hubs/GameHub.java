package hubs;

import akka.actor.ActorRef;
import model.Game;
import play.Logger;
import signalJ.services.Hub;

import java.util.*;
import java.util.Map.Entry;

import static model.FinishedGameStatus.OPPONENT_LEFT;
import static model.Player.findByFacebookId;

/**
 * Created by tomasnajun on 01/06/16.
 */
public class GameHub extends Hub<GamePage> {
    private final static Map<Game, Set<Long>> games = new HashMap<>();
    private final static Map<Game, Set<Long>> waitingGames = new HashMap<>();
    private final static Map<UUID, Long> connectionsToUsers = new HashMap<>();
    private static final String FACEBOOK_ID = "facebookId";

    public boolean login(long facebookId) {
        if(connectionsToUsers.containsValue(facebookId)) return false;
        clients().callerState.put(FACEBOOK_ID, String.valueOf(facebookId));
        connectionsToUsers.putIfAbsent(context().connectionId, Long.valueOf(clients().callerState.get(FACEBOOK_ID)));
        return true;
    }

    public void logout() {
        connectionsToUsers.remove(context().connectionId);
        removeUserFromGame(clients().callerState.get(FACEBOOK_ID));
        clients().callerState.put(FACEBOOK_ID, "");
    }

    public void joinGame(long facebookId) {
        final Iterator<Entry<Game, Set<Long>>> iterator = waitingGames.entrySet().iterator();
        if (iterator.hasNext()) {
            joinToAWaitingGame(facebookId, iterator.next());
        } else {
            joinToANewGame(facebookId);
        }
    }

    private void joinToANewGame(long facebookId) {
        final Game game = new Game(findByFacebookId(facebookId));
        game.save();
        final HashSet<Long> players = new HashSet<>();
        players.add(facebookId);
        waitingGames.put(game, players);
        addUserToGame(facebookId, game.getId());
    }

    private void joinToAWaitingGame(long facebookId, Entry<Game, Set<Long>> gameSetEntry) {
        final Game game = gameSetEntry.getKey();
        game.setPlayer2(findByFacebookId(facebookId));
        game.update();
        final Set<Long> players = gameSetEntry.getValue();
        players.add(facebookId);
        games.put(game, players);
        waitingGames.remove(game);
        addUserToGame(facebookId, game.getId());
    }

    private void addUserToGame(long facebookId, long gameId) {
        final String stringGameId = String.valueOf(gameId);
        groups().add(context().connectionId, stringGameId);
        clients().othersInGroup(stringGameId).oppenentJoinedGame(facebookId);
    }

    private boolean removeUserFromGame(String facebookId) {
        long gameId = 0;
        Game removedGame = null;
        for(Game key : games.keySet()) {
            if(games.get(key).remove(Long.valueOf(facebookId))) {
                removedGame = key;
                clients().othersInGroup(String.valueOf(gameId)).endGame(OPPONENT_LEFT);
                break;
            }
        }
        if (removedGame != null) {
            groups().remove(context().connectionId, String.valueOf(removedGame.getId()));
            //TODO save who wins and who loos
            return  true;
        }
        return false;
    }

    /**
     *
     * @param shooterId: player facebookId
     */
    public boolean shoot(int row, int column, long shooterId) {
        return false;
    }

    /**
     *
     * @param playerFbId: player FacebookId
     * @param positions: [row][column] squares took up by ship
     * @param shipSize: {@link model.ships.ShipType}
     * @return
     */
    public boolean setShip(long playerFbId, int[][] positions, int shipSize) {
        return false;
    }

    @Override
    protected Class<GamePage> getInterface() {
        return GamePage.class;
    }

    @Override
    public void onDisconnected() {
        final Long username = connectionsToUsers.remove(context().connectionId);
        Logger.debug("Disconnect: " + username);
        removeUserFromGame(String.valueOf(username));
    }

    @Override
    public void onConnected() {
    }


}
