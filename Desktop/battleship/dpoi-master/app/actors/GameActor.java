package actors;

import actors.messages.GameMssg;
import actors.messages.GameMssg.PlayerMssg;
import actors.messages.GameMssg.SetShip;
import actors.messages.GameMssg.CreateGame;
import actors.messages.GameMssg.Shoot;
import actors.messages.ResponseFactory;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import model.FinishedGameStatus;
import model.Game;
import model.GameBoard;
import model.Player;
import model.ships.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static actors.messages.ResponseFactory.*;
import static model.FinishedGameStatus.WIN;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class GameActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final HashMap<ActorRef, GameBoard> gameBoards = new HashMap<>();
    private Player userPlaying;

    public GameActor() {
        receive(ReceiveBuilder
                .match(CreateGame.class, createGame -> {
                    //TODO ver si puedo instanciarlo en el constructor
                    final Game game = createGame(createGame);
                    userPlaying = game.getPlayerBoard().getOwner();
                    final JsonNode gameCreatedMessage = ResponseFactory.gameCreated(game.getId());
                    gameBoards.keySet().forEach(actorRef -> actorRef.tell(gameCreatedMessage, self()));
                })
                .match(SetShip.class, setShip -> {
                    final GameBoard gameBoard = gameBoards.get(sender());
                    gameBoard.setShip(setShip.size, setShip.row, setShip.col);
                    changeTurn();
                    //TODO que ambos jugadores puedan setear los barcos al mismo tiempo
                })
                .match(Shoot.class, shoot -> {
                    final GameBoard playerBoard = gameBoards.get(sender());
                    ActorRef opponentRef = getOpponentRef();
                    if (opponentRef != null) {
                        if (playerBoard.getOwner().getId() == userPlaying.getId()) {
                            final GameBoard opponentGameBoard =  gameBoards.get(opponentRef);
                            final HitResult hitResult = opponentGameBoard.receiveShoot(shoot.row, shoot.col);

                            if (!hitResult.name().equals(HitResult.WIN.name())) {
                                opponentRef.tell(receiveShoot(shoot.row, shoot.col, hitResult), self());
                                sender().tell(shootResult(shoot.row, shoot.col, hitResult), self());
                            } else {
                                sender().tell(endGame(WIN), self());
                            }
                            playerBoard.annotate(shoot.row, shoot.col, hitResult);

                            //Change turn
                            changeTurn();
                        }
                    }
                })
                .match(GameMssg.LeaveGame.class, leaveGame -> {
                    final JsonNode endGame = endGame(FinishedGameStatus.OPPONENT_LEFT);
                    final String facebookId = leaveGame.facebookId;
                    if (!userPlaying.facebookId.equals(facebookId)) {
                        sender().tell(endGame, self());
                    } else {
                        final ActorRef opponentRef = getOpponentRef();
                        if (opponentRef != null)
                            opponentRef.tell(endGame, self());
                        else log.error("LeaveGame: opponentRef is null!");
                    }
                })
                .build());
    }

    private Game createGame(CreateGame createGame) {
        final PlayerMssg playerActor1 = createGame.player1;
        final PlayerMssg playerActor2 = createGame.player2;

        final Player player1 = Player.findOrCreate(playerActor1.name, playerActor1.facebookId);
        final Player player2 = Player.findOrCreate(playerActor2.name, playerActor2.facebookId);
        final Game game = new Game(player1, player2);
        game.save();

        final GameBoard gameBoard1 = game.getPlayerGameBoard(player1);
        final GameBoard gameBoard2 = game.getPlayerGameBoard(player2);
        gameBoards.put(playerActor1.player, gameBoard1);
        gameBoards.put(playerActor2.player, gameBoard2);
        return game;
    }

    private void changeTurn() {
        final ActorRef opponentRef = getOpponentRef();
        if (opponentRef != null) {
            final GameBoard opponentGameBoard = gameBoards.get(opponentRef);
            userPlaying = opponentGameBoard.getOwner();
            opponentRef.tell(yourTurn(), self());
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
}
