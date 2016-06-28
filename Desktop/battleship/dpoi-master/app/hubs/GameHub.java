//package hubs;
//
//import controllers.GamesController;
//import model.Game;
//import play.Logger;
//import signalJ.services.Hub;
//
//import java.util.*;
//import java.util.function.BiFunction;
//
//import static model.FinishedGameStatus.OPPONENT_LEFT;
//
///**
// * Created by tomasnajun on 01/06/16.
// */
//public class GameHub extends Hub<GamePage> {
//    private final static Map<UUID, Long> connectionsToUsers = new HashMap<>();
//    private final static GamesController gamesController = new GamesController();
//    private static final String FACEBOOK_ID = "playerDBId";
//
//    public boolean login(long playerDBId) {
//        if(connectionsToUsers.containsValue(playerDBId)) return false;
//        clients().callerState.put(FACEBOOK_ID, String.valueOf(playerDBId));
//        connectionsToUsers.putIfAbsent(context().connectionId, Long.valueOf(clients().callerState.get(FACEBOOK_ID)));
//        return true;
//    }
//
//    public void logout() {
//        connectionsToUsers.remove(context().connectionId);
//        removeUserFromGame(clients().callerState.get(FACEBOOK_ID));
//        clients().callerState.put(FACEBOOK_ID, "");
//    }
//
//    public long joinGame(long playerDBId) {
//        final long waitingPlayer = gamesController.joinGame(playerDBId);
//        addUserToGame(playerDBId, waitingPlayer);
//        return waitingPlayer;
//    }
//
//    private void addUserToGame(long playerDBId, long waitingPlayer) {
//        final String stringGameId = String.valueOf(waitingPlayer);
//        groups().add(context().connectionId, stringGameId);
//        clients().othersInGroup(stringGameId).opponentJoinedGame(playerDBId);
//    }
//
//    public long startGame(long waitingPlayer) {
//        final long fbId = gamesController.startGame(waitingPlayer);
//        for (final Map.Entry<UUID, Long> entry : connectionsToUsers.entrySet()) {
//            if (entry.getValue().equals(fbId)) {
//                clients().client(entry.getKey()).yourTurn();
//            }
//        }
//        return fbId;
//    }
//
//    private boolean removeUserFromGame(String playerDBId) {
//        final Game removedGame = gamesController.removeUserFromGame(playerDBId);
//        if (removedGame != null) {
//            clients().othersInGroup(String.valueOf(removedGame.getId())).endGame(OPPONENT_LEFT);
//            groups().remove(context().connectionId, String.valueOf(removedGame.getId()));
//            return  true;
//        }
//        return false;
//    }
//
//    /**
//     *
//     * @param shooterId: actorRef playerDBId
//     * @return hit or not
//     */
//    public boolean shoot(int row, int column, long shooterId) {
//        return gamesController.shoot(row, column, shooterId);
//    }
//
//    /**
//     *
//     * @param playerFbId: actorRef FacebookId
//     * @param positions: [row][column] squares took up by ship
//     * @param shipSize: {@link model.ships.ShipType}
//     * @return set successful
//     */
//    public boolean setShip(long playerFbId, int[][] positions, int shipSize) {
//        return gamesController.setShip(playerFbId, positions, shipSize);
//    }
//
//    @Override
//    protected Class<GamePage> getInterface() {
//        return GamePage.class;
//    }
//
//    @Override
//    public void onDisconnected() {
//        final Long username = connectionsToUsers.remove(context().connectionId);
//        Logger.debug("Disconnect: " + username);
//        removeUserFromGame(String.valueOf(username));
//    }
//
//    @Override
//    public void onConnected() {
//    }
//
//}
