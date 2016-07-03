package actors.messages;

import akka.actor.ActorRef;
import model.FinishedGameStatus;
import model.ships.HitResult;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class GameMssg {

    private GameMssg() {
    }

    public static final class JoinGame {
        public final PlayerMssg waitingPlayer;

        public JoinGame(PlayerMssg waitingPlayer) {
            this.waitingPlayer = waitingPlayer;
        }

        @Override
        public String toString() {
            return "JoinGame{" +
                    "waitingPlayer=" + waitingPlayer +
                    '}';
        }
    }

    public static final class LeaveGame {
        public final String gameName;

        public LeaveGame(String gameName) {
            this.gameName = gameName;
        }

        public Optional<String> gameName() { return Optional.of(gameName);}

        @Override
        public String toString() {
            return "LeaveGame{" +
                    "gameName='" + gameName + '\'' +
                    '}';
        }
    }

    public static final class CreateGame {
        public final PlayerMssg player1;
        public final PlayerMssg player2;

        public CreateGame(PlayerMssg player1, PlayerMssg player2) {
            this.player1 = player1;
            this.player2 = player2;
        }
    }

    public static final class ContinueGame {
        public final PlayerMssg player1;
        public final ActorRef oldActorRef;

        public ContinueGame(PlayerMssg player1, ActorRef oldActorRef) {
            this.player1 = player1;
            this.oldActorRef = oldActorRef;
        }
    }

    public static final class PlayerMssg {
        public final ActorRef actorRef;
        public final long playerDBId;

        public PlayerMssg(ActorRef actorRef, long playerDBId) {
            this.actorRef = actorRef;
            this.playerDBId = playerDBId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final PlayerMssg that = (PlayerMssg) o;

            return playerDBId == that.playerDBId;
        }

        @Override
        public int hashCode() {
            return (int) (playerDBId ^ (playerDBId >>> 32));
        }

        @Override
        public String toString() {
            return "PlayerMssg{" +
                    "actorRef=" + actorRef +
                    ", playerDBId='" + playerDBId + '\'' +
                    '}';
        }
    }

    public static final class SetShip {
        public final Integer size;
        public final Integer[] row, col;
        public final String gameName;

        public SetShip(Integer[] col, Integer[] row, int size, String gameName) {
            this.col = col;
            this.row = row;
            this.size = size;
            this.gameName = gameName;
        }

        @Override
        public String toString() {
            return "SetShip{" +
                    "size=" + size +
                    ", row=" + Arrays.toString(row) +
                    ", col=" + Arrays.toString(col) +
                    ", gameName='" + gameName + '\'' +
                    '}';
        }
    }

    public static final class Shoot {
        public final String gameName;
        public final int row;
        public final int col;

        public Shoot(String gameName, int row, int col) {
            this.gameName = gameName;
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return "Shoot{" +
                    "gameName='" + gameName + '\'' +
                    ", row=" + row +
                    ", col=" + col +
                    '}';
        }
    }

    public static final class GameCreated {
        public final String gameName;

        public GameCreated(String gameName) {
            this.gameName = gameName;
        }

        @Override
        public String toString() {
            return "GameCreated{" +
                    "gameName='" + gameName + '\'' +
                    '}';
        }
    }

    public static final class ReceiveShoot {
        public final int row, col;
        public final HitResult hitResult;

        public ReceiveShoot(int row, int col, HitResult hitResult) {
            this.row = row;
            this.col = col;
            this.hitResult = hitResult;
        }
    }

    public static final class ShootResult{
        public final int row, col;
        public final HitResult hitResult;

        public ShootResult(int row, int col, HitResult hitResult) {
            this.row = row;
            this.col = col;
            this.hitResult = hitResult;
        }
    }

    public static final class YourTurn {

    }

    public static final class EndGame {
        public final FinishedGameStatus status;

        public EndGame(FinishedGameStatus status) {
            this.status = status;
        }
    }

    public static final class PlayerDisconnected {
    }

    public static final class Ready {
        public final String gameName;

        public Ready(String gameName) {
            this.gameName = gameName;
        }
    }
}
