package actors;

import actors.messages.GameMssg;
import actors.messages.GameMssg.PlayerMssg;
import actors.messages.GameMssg.SetShip;
import actors.messages.GameMssg.CreateGame;
import actors.messages.GameMssg.Shoot;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import model.FinishedGameStatus;
import model.Game;
import model.GameBoard;
import model.Player;
import model.ships.HitResult;
import org.jetbrains.annotations.Nullable;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static model.FinishedGameStatus.OPPONENT_LEFT;
import static model.FinishedGameStatus.WIN;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class GameActor extends AbstractActor {

    private Game game;
    private Cancellable cancellable;
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private HashSet<String> playerReadyToPlay = new HashSet<>();

    private final HashMap<ActorRef, GameBoard> gameBoards = new HashMap<>();
    private Player userPlaying = null;

    public GameActor() {
        receive(ReceiveBuilder
                .match(CreateGame.class, createGame -> {
                    //TODO ver si puedo instanciarlo en el constructor
                    this.game = createGame(createGame);

                    final GameMssg.GameCreated gameCreated = new GameMssg.GameCreated(self().path().name());
                    gameBoards.keySet().forEach(actorRef -> actorRef.tell(gameCreated, self()));
                })
                .match(SetShip.class, setShip -> {
                    final GameBoard gameBoard = gameBoards.get(sender());
                    gameBoard.setShip(setShip.size, setShip.row, setShip.col);
                })
                .match(Shoot.class, shoot -> {
                    final GameBoard playerBoard = gameBoards.get(sender());
                    ActorRef opponentRef = getOpponentRef();
                    if (opponentRef != null) {
                        if (playerBoard.getOwner().getId() == userPlaying.getId()) {
                            final GameBoard opponentGameBoard = gameBoards.get(opponentRef);
                            final HitResult hitResult = opponentGameBoard.receiveShoot(shoot.row, shoot.col);



                            final GameMssg.ShootResult shootResult;
                            final GameMssg.ReceiveShoot receiveShoot;
                            if (hitResult.name().equals(HitResult.WIN.name())) {
                                receiveShoot = new GameMssg.ReceiveShoot(shoot.row, shoot.col, HitResult.LOOSE);
                                shootResult = new GameMssg.ShootResult(shoot.row, shoot.col, HitResult.WIN);
                            } else {
                                receiveShoot = new GameMssg.ReceiveShoot(shoot.row, shoot.col, hitResult);
                                shootResult = new GameMssg.ShootResult(shoot.row, shoot.col, hitResult);
                            }
                            opponentRef.tell(receiveShoot, self());
                            sender().tell(shootResult, self());
                            playerBoard.annotate(shoot.row, shoot.col, hitResult);
                            changeTurn();
                        }
                    }
                })
                .match(GameMssg.LeaveGame.class, leaveGame -> {
                    final GameMssg.EndGame endGame = new GameMssg.EndGame(OPPONENT_LEFT);

                    final ActorRef opponentRef = getOpponentRef();
                    if (opponentRef != null)
                        opponentRef.tell(endGame, self());
                    else log.error("LeaveGame: opponentRef is null!");
                    saveGameState();
                    close();
                })
                .match(GameMssg.PlayerDisconnected.class, playerDisconnected -> {
                    final ActorRef opponentRef = getOpponentRef();
                    //TODO avisar que el otro jugador se desconecto que espere un ratito
                    if (opponentRef != null) {
                        final ActorSystem system = context().system();
                        cancellable = system.scheduler().scheduleOnce(Duration.create(15, TimeUnit.SECONDS),
                                self(), new GameMssg.LeaveGame(null), system.dispatcher(), null);
                    }
                })
                .match(GameMssg.ContinueGame.class, continueGame -> {
                    final PlayerMssg player = continueGame.player1;
                    if (cancellable != null) {
                        if (cancellable.cancel()) {
                            final GameBoard gameBoard = gameBoards.remove(continueGame.oldActorRef);
                            gameBoards.put(player.actorRef, gameBoard);
                            final ActorRef opponentRef = getOpponentRef();
                            if (opponentRef != null) {
                                opponentRef.tell(new GameMssg.ContinueGame(null, null), self());
                            } else log.info("Opponent Ref is Null");

                        }
                    }
                })
                .match(GameMssg.Ready.class, ready -> {
                    playerReadyToPlay.add(sender().path().name());
                    log.info("Entre a Ready, players ready: " + playerReadyToPlay.size());
                    if (playerReadyToPlay.size() > 1 && userPlaying == null) {
                        log.info("Se otorga un turno");
                        changeTurn();
                    }
                })
                .build());
    }

    public void close() {
        context().parent().tell(new GameMssg.EndGame(null) , self());
        self().tell(PoisonPill.getInstance(), self());
    }

    private Game createGame(CreateGame createGame) {
        final PlayerMssg playerActor1 = createGame.player1;
        final PlayerMssg playerActor2 = createGame.player2;

        final Player player1 = Player.find.byId(playerActor1.playerDBId);
        final Player player2 = Player.find.byId(playerActor2.playerDBId);
        final Game game = new Game(player1, player2);
        game.save();

        final GameBoard gameBoard1 = game.getPlayerGameBoard(player1);
        final GameBoard gameBoard2 = game.getPlayerGameBoard(player2);
        gameBoards.put(playerActor1.actorRef, gameBoard1);
        gameBoards.put(playerActor2.actorRef, gameBoard2);
        return game;
    }

    private void changeTurn() {
        final ActorRef opponentRef = getOpponentRef();
        if (opponentRef != null) {
            final GameBoard opponentGameBoard = gameBoards.get(opponentRef);
            userPlaying = opponentGameBoard.getOwner();
            opponentRef.tell(new GameMssg.YourTurn(), self());
        } else log.error("ChangeTurn: opponentRef is null!");
    }

    @Nullable
    private ActorRef getOpponentRef() {
        for (final Map.Entry<ActorRef, GameBoard> entry : gameBoards.entrySet()) {
            final ActorRef opponentRef = entry.getKey();
            if (!opponentRef.equals(sender())) {
                return opponentRef;
            }
        }
        return null;
    }

    private void saveGameState() {
        game.save();
    }
}
