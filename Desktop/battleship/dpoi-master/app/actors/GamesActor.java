package actors;

import actors.messages.GameMssg;
import actors.messages.GameMssg.ContinueGame;
import actors.messages.GameMssg.CreateGame;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class GamesActor extends UntypedActor {
    //<PlayerActorRef, PlayerId>
    private final Map<ActorRef, Long> players = new HashMap<>();
    //<PlayerId, PlayerActorRef>
    private final Map<Long, ActorRef> players2 = new HashMap<>(); //TODO es necesario que sean mapa
    //<PlayerId, PlayerActorRef>
    private final Map<Long, ActorRef> waitingPlayers = new HashMap<>(); //TODO es necesario que sean mapa
    // <PlayerId, GameActorRef>
    private final Map<Long, ActorRef> games = new HashMap<>();

    private static final String GAME = "game-";
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GameMssg.JoinGame) {
            final GameMssg.JoinGame joinGame = (GameMssg.JoinGame) message;
            final GameMssg.PlayerMssg player1 = joinGame.waitingPlayer;
            final long player1Id = player1.playerDBId;
            final ActorRef player1ActorRef = player1.actorRef;

//            if (players.containsKey(player1ActorRef)) {
//                players.replace(player1ActorRef, player1Id);
//            } else
                players.put(player1ActorRef, player1Id);

            if (players2.containsKey(player1Id)) {
                final ActorRef oldActorRef = players2.replace(player1Id, player1ActorRef);
                if (games.containsKey(player1Id)) {
                    final ActorRef actorRef = games.get(player1Id);
                    if (actorRef != null) {
                        actorRef.forward(new ContinueGame(player1, oldActorRef), context());
                        log.info("Usuario reconectado a juego");
                        return;
                    }
                }
            } else {
                //create new Game
                if (waitingPlayers.isEmpty()) {
                    waitingPlayers.put(player1Id, player1ActorRef);
                    log.info("usuario Agregado");
                } else {
                    //TODO desconectar
//                    if (waitingPlayers.containsKey(player1Id)) {
//                        waitingPlayers.replace(player1Id, player1ActorRef);
//                    }
                    final Iterator<Map.Entry<Long, ActorRef>> iterator = waitingPlayers.entrySet().iterator();
                    Map.Entry<Long, ActorRef> next = iterator.next();
//                    if (next.getKey().equals(player1Id) && iterator.hasNext()) {
//                        next = iterator.next();
//                    } else return;

                    final GameMssg.PlayerMssg player2 = new GameMssg.PlayerMssg(next.getValue(), next.getKey());
                    final long player2Id = player2.playerDBId;
                    //Create GameActor
                    final String gameName = GAME + player1Id + player2Id;
                    final Props props = Props.create(GameActor.class);
                    final ActorRef gameChild = context().actorOf(props, gameName);
                    final CreateGame createGame = new CreateGame(player1, player2);
                    gameChild.forward(createGame, context());
                    //TODO watch child para que me avise cuando muere
                    //http://doc.akka.io/docs/akka/current/general/supervision.html#What_Lifecycle_Monitoring_Means

                    games.put(player1Id, gameChild);
                    games.put(player2Id, gameChild);
                    players2.put(player1Id, player1ActorRef);
                    players2.put(player2Id, player2.actorRef);
                    waitingPlayers.remove(player2Id);
                    log.info("Game creado");
                }
                return;
            }
        }
//        if (message instanceof CreateGame) {
//            final CreateGame createGame = (CreateGame) message;
//            log.debug("CreateGame");
//            final long player1Id = createGame.player1.playerDBId;
//            final long player2Id = createGame.player2.playerDBId;
//            final String gameName = GAME + player1Id + player2Id;
//
//            final Props props = Props.create(GameActor.class);
//            final ActorRef gameChild = context().actorOf(props, gameName);
//            gameChild.forward(createGame, context());
//
//            games.put(player1Id, gameChild);
//            games.put(player2Id, gameChild);
//        }

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

        if (message instanceof GameMssg.PlayerDisconnected) {
            final Long playerId = players.get(sender());
            if (playerId != null) {
                final ActorRef gameActorRef = games.get(playerId);
                if (gameActorRef != null) {
                    gameActorRef.tell(message, self());
                }
            } else log.info("ERROR: On Player Disconnect player id null");
        }
    }
}
