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

    public static final class JoinGame {
        public final PlayerMssg waitingPlayer;

        public JoinGame(PlayerMssg waitingPlayer) {
            this.waitingPlayer = waitingPlayer;
        }
    }

    public static final class LeaveGame {
        public final String gameName;
        public final String facebookId;

        public LeaveGame(String gameName, String facebookId) {
            this.gameName = gameName;
            this.facebookId = facebookId;
        }

        public Optional<String> gameName() { return Optional.of(gameName);}

        @Override
        public String toString() {
            return "LeaveGame{" +
                    "gameName='" + gameName + '\'' +
                    ", facebookId='" + facebookId + '\'' +
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

    public static final class PlayerMssg {
        public final ActorRef player;
        public final String name;
        public final String facebookId;

        public PlayerMssg(ActorRef player, String name, String facebookId) {
            this.player = player;
            this.name = name;
            this.facebookId = facebookId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final PlayerMssg that = (PlayerMssg) o;

            return facebookId != null ? facebookId.equals(that.facebookId) : that.facebookId == null;

        }

        @Override
        public int hashCode() {
            return facebookId != null ? facebookId.hashCode() : 0;
        }
    }

    public static final class SetShip {
        public final int size;
        public final int[] row, col;
        public final String gameName;

        public SetShip(int[] col, int[] row, int size, String gameName) {
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
}
