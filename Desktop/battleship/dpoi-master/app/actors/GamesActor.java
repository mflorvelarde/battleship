package actors;

import actors.messages.GameMssg;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Collections;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class GamesActor extends UntypedActor {

    private static final String GAME = "game-";
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    //TODO agregar mapa con sesion
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameMssg.CreateGame) {
            final GameMssg.CreateGame createGame = (GameMssg.CreateGame) message;

            // get or create the GameActor for the game and forward this message
//            Optional.ofNullable(getContext().getChild(gameName)).orElseGet(() -> {
//                        final Props props = Props.create(GameActor.class, gameName);
//                        return context().actorOf(props, gameName);
//                    }
//            ).forward(gameCreated, context());
            log.debug("CreateGame");
            final String gameName = GAME + createGame.player1.facebookId + createGame.player2.facebookId;
            final Props props = Props.create(GameActor.class);
            final ActorRef gameChild = context().actorOf(props, gameName);
            gameChild.forward(createGame, context());
        }

        if (message instanceof GameMssg.SetShip) {
            final GameMssg.SetShip setShip = (GameMssg.SetShip) message;
            final String gameName = setShip.gameName;
            getContext().getChild(gameName).forward(setShip, context());
        }

        if (message instanceof GameMssg.Shoot) {
            final GameMssg.Shoot shoot = (GameMssg.Shoot) message;
            final String gameName = shoot.gameName;
            getContext().getChild(gameName).forward(shoot, context());
        }

        if (message instanceof GameMssg.LeaveGame) {
            final GameMssg.LeaveGame leaveGame = (GameMssg.LeaveGame) message;
            final String gameName = leaveGame.gameName;
            getContext().getChild(gameName).forward(leaveGame, context());

            // forward this message to the associated GameActor

            leaveGame.gameName()
                    .map(getContext()::getChild)
                    .<Iterable<ActorRef>>map(Collections::singletonList);
            //or otherwise to everyone
//                    .orElse(getContext().getChildren())
//                    .forEach(child -> child.forward(leaveGame, context()));
        }
    }
}
