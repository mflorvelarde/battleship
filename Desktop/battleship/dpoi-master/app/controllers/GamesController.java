package controllers;

import model.Game;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static model.Player.findByFacebookId;

/**
 * Created by tomasnajun on 12/06/16.
 */
public class GamesController {

    private final static Map<Game, Set<Long>> games = new HashMap<>();
    private final static Map<Game, Set<Long>> waitingGames = new HashMap<>();

    public long joinGame(long facebookId) {
        final Iterator<Map.Entry<Game, Set<Long>>> iterator = waitingGames.entrySet().iterator();
        final long gameId;
        if (iterator.hasNext()) {
            gameId = joinToAWaitingGame(facebookId, iterator.next());
        } else {
            gameId = joinToANewGame(facebookId);
        }
        return gameId;
    }

    private long joinToANewGame(long facebookId) {
        final Game game = new Game(findByFacebookId(facebookId));
        game.save();
        final HashSet<Long> players = new HashSet<>();
        players.add(facebookId);
        waitingGames.put(game, players);
        return  game.getId();
    }

    private long joinToAWaitingGame(long facebookId, Map.Entry<Game, Set<Long>> gameSetEntry) {
        final Game game = gameSetEntry.getKey();
        game.setPlayer2(findByFacebookId(facebookId));
        game.update();
        final Set<Long> players = gameSetEntry.getValue();
        players.add(facebookId);
        games.put(game, players);
        waitingGames.remove(game);
        return game.getId();
    }

    @Nullable public Game removeUserFromGame(String facebookId) {
        Game removedGame = null;
        for(Game key : games.keySet()) {
            if(games.get(key).remove(Long.valueOf(facebookId))) {
                removedGame = key;
                break;
            }
        }
        return removedGame;
    }

    /**
     *
     * @param shooterId: player facebookId
     * @return hit or not
     */
    public boolean shoot(int row, int column, long shooterId) {
        return false;
    }

    /**
     *
     * @param playerFbId: player FacebookId
     * @param positions: [row][column] squares took up by ship
     * @param shipSize: {@link model.ships.ShipType}
     * @return set successful
     */
    public boolean setShip(long playerFbId, int[][] positions, int shipSize) {
        return false;
    }
}
