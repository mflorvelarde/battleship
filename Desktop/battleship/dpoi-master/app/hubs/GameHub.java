package hubs;

import controllers.GamesController;
import model.Game;
import play.Logger;
import signalJ.services.Hub;

import java.util.*;

import static model.FinishedGameStatus.OPPONENT_LEFT;

/**
 * Created by tomasnajun on 01/06/16.
 */
public class GameHub extends Hub<GamePage> {
    private final static Map<UUID, Long> connectionsToUsers = new HashMap<>();
    private final static GamesController gamesController = new GamesController();
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
        final long gameId = gamesController.joinGame(facebookId);
        addUserToGame(facebookId, gameId);
    }

    private void addUserToGame(long facebookId, long gameId) {
        final String stringGameId = String.valueOf(gameId);
        groups().add(context().connectionId, stringGameId);
        clients().othersInGroup(stringGameId).oppenentJoinedGame(facebookId);
    }

    private boolean removeUserFromGame(String facebookId) {
        final Game removedGame = gamesController.removeUserFromGame(facebookId);
        if (removedGame != null) {
            clients().othersInGroup(String.valueOf(removedGame.getId())).endGame(OPPONENT_LEFT);
            groups().remove(context().connectionId, String.valueOf(removedGame.getId()));
            return  true;
        }
        return false;
    }

    /**
     *
     * @param shooterId: player facebookId
     * @return hit or not
     */
    public boolean shoot(int row, int column, long shooterId) {
        return gamesController.shoot(row, column, shooterId);
    }

    /**
     *
     * @param playerFbId: player FacebookId
     * @param positions: [row][column] squares took up by ship
     * @param shipSize: {@link model.ships.ShipType}
     * @return set successful
     */
    public boolean setShip(long playerFbId, int[][] positions, int shipSize) {
        return gamesController.setShip(playerFbId, positions, shipSize);
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