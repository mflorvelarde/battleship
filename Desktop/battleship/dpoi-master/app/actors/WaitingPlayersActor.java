package actors;

import actors.messages.GameMssg;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import javax.inject.Named;
import java.util.HashSet;

import static actors.messages.GameMssg.*;

/**
 * Created by tomasnajun on 21/06/16.
 */
public class WaitingPlayersActor extends UntypedActor {

    private final HashSet<GameMssg.PlayerMssg> waitingPlayers = new HashSet<>();
    private final ActorRef gamesActor;

    public WaitingPlayersActor(@Named("gamesActor") ActorRef gamesActor) {
        this.gamesActor = gamesActor;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof JoinGame) {
            final JoinGame joinGame = (JoinGame) message;
            if (!waitingPlayers.isEmpty()) {
                final PlayerMssg waitingPlayer1 = waitingPlayers.iterator().next();
                final PlayerMssg waitingPlayer2 = joinGame.waitingPlayer;

                final CreateGame createGame = new CreateGame(waitingPlayer1, waitingPlayer2);
                gamesActor.tell(createGame, self());
            } else {
                final PlayerMssg waitingPlayer = joinGame.waitingPlayer;
                waitingPlayers.add(waitingPlayer);
            }
        }
    }


}
