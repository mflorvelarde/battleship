package actors;

import actors.messages.GameMssg;
import actors.messages.ResponseFactory;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

import static actors.messages.ResponseFactory.*;
import static play.mvc.Controller.session;

/**
 * Created by tomasnajun on 15/06/16.
 */
public class PlayerActor extends UntypedActor{
    private final ActorRef out;
    private final ActorRef gamesActor;
    private Configuration configuration;
    private final long playerId;

    @Inject
    public PlayerActor(@Assisted ActorRef out,
                       @Assisted long playerId,
                       @Named("gamesActor") ActorRef gamesActor,
                       Configuration configuration) {
        this.out = out;
        this.playerId = playerId;
        this.gamesActor = gamesActor;
        this.configuration = configuration;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public void onReceive(Object msg) throws Exception {

        if (msg instanceof GameMssg.GameCreated) {
            final GameMssg.GameCreated gameCreated = (GameMssg.GameCreated) msg;
            final JsonNode gameCreatedMessage = ResponseFactory.gameCreated(gameCreated.gameName);
            System.out.println(gameCreated);
            out.tell(gameCreatedMessage, self());
        }

        if (msg instanceof GameMssg.YourTurn) {
            final JsonNode yourTurn = ResponseFactory.yourTurn();
            out.tell(yourTurn, self());
        }

        if (msg instanceof GameMssg.ShootResult) {
            final GameMssg.ShootResult shootResult = (GameMssg.ShootResult) msg;
            final JsonNode shootResultMessage = shootResult(shootResult.row, shootResult.col, shootResult.hitResult);
            out.tell(shootResultMessage, self());
        }

        if (msg instanceof GameMssg.ReceiveShoot) {
            final GameMssg.ReceiveShoot receiveShoot = (GameMssg.ReceiveShoot) msg;
            final JsonNode receiveShootMessage = receiveShoot(receiveShoot.row, receiveShoot.col, receiveShoot.hitResult);
            out.tell(receiveShootMessage, self());
        }

        if (msg instanceof GameMssg.EndGame) {
            final GameMssg.EndGame endGame = (GameMssg.EndGame) msg;
            final JsonNode endGameMessage = endGame(endGame.status);
            out.tell(endGameMessage, self());
        }

        if (msg instanceof GameMssg.ContinueGame) {
            final JsonNode continueGame = continueGame();
            out.tell(continueGame, self());
        }

        if (msg instanceof JsonNode) {
            final JsonNode json = (JsonNode) msg;
            final String type = json.get("type").textValue();
            final String gameName = json.get("gameName").textValue();
            final ObjectMapper objectMapper = new ObjectMapper();

            switch (type) {
                case "joinGame":
                    final GameMssg.JoinGame joinGame = new GameMssg.JoinGame(new GameMssg.PlayerMssg(self(), playerId));
                    gamesActor.tell(joinGame, self());
                    System.out.println(joinGame);
                    break;
                case "setShip":
                    Integer[] rows = objectMapper.readValue(json.get("row").toString(), Integer[].class);
                    Integer[] cols = objectMapper.readValue(json.get("col").toString(), Integer[].class);
                    final int len = json.get("len").asInt();
                    final GameMssg.SetShip setShip = new GameMssg.SetShip(cols, rows, len, gameName);
                    gamesActor.tell(setShip, self());
                    System.out.println("setShip = " + setShip);
                    break;
                case "shoot":
                    final int row = json.get("row").asInt();
                    final int col = json.get("col").asInt();
                    final GameMssg.Shoot shoot = new GameMssg.Shoot(gameName, row, col);
                    gamesActor.tell(shoot, self());
                    System.out.println("shoot = " + shoot);
                    break;
                case "leaveGame":
                    final GameMssg.LeaveGame leaveGame = new GameMssg.LeaveGame(gameName);
                    gamesActor.tell(leaveGame, self());
                    System.out.println("leaveGame = " + leaveGame);
                    break;
                case "ready":
                    final GameMssg.Ready ready = new GameMssg.Ready(gameName);
                    gamesActor.tell(ready, self());
                default:
                    break;
            }
        }
    }

    public void close() {
        self().tell(PoisonPill.getInstance(), self());
    }

    public interface Factory {
        Actor create(ActorRef out, long playerId);
    }
}
