package actors;

import actors.messages.GameMssg;
import actors.messages.GameMssg.JoinGame;
import actors.messages.ResponseFactory;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

import static actors.messages.ResponseFactory.endGame;
import static actors.messages.ResponseFactory.receiveShoot;
import static actors.messages.ResponseFactory.shootResult;

/**
 * Created by tomasnajun on 15/06/16.
 */
public class PlayerActor extends UntypedActor{
    private final ActorRef out;
    private final ActorRef waitingPlayersActor;
    private final ActorRef gamesActor;
    private Configuration configuration;

    @Inject
    public PlayerActor(@Assisted ActorRef out,
                       @Named("waitingPlayersActor") ActorRef waitingPlayersActor,
                       @Named("gamesActor") ActorRef gamesActor,
                       Configuration configuration) {
        this.out = out;
        this.waitingPlayersActor = waitingPlayersActor;
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

        if (msg instanceof JsonNode) {
            final JsonNode json = (JsonNode) msg;
            final String type = json.get("type").textValue();

            final ObjectMapper objectMapper = new ObjectMapper();

            switch (type) {
                case "joinGame":
                    final String facebookId = json.get("facebookId").textValue();
                    final String name = json.get("name").textValue();
                    final JoinGame joinGame = new JoinGame(new GameMssg.PlayerMssg(self(), name, facebookId));
                    waitingPlayersActor.tell(joinGame, self());
                    System.out.println(joinGame);
                    break;
                case "setShip":
                    Integer[] rows = objectMapper.readValue(json.get("row").toString(), Integer[].class);
                    Integer[] cols = objectMapper.readValue(json.get("col").toString(), Integer[].class);
                    final int len = json.get("len").asInt();
                    final String gameName = json.get("gameName").textValue();
                    final GameMssg.SetShip setShip = new GameMssg.SetShip(cols, rows, len, gameName);
                    gamesActor.tell(setShip, self());
//                    System.out.println("setShip = " + setShip);
                    break;
                case "shoot":
                    final int row = json.get("row").asInt();
                    final int col = json.get("col").asInt();
                    final String shootGameName = json.get("gameName").textValue();
                    final GameMssg.Shoot shoot = new GameMssg.Shoot(shootGameName, row, col);
                    gamesActor.tell(shoot, self());
                    System.out.println("shoot = " + shoot);
                    break;
                case "leaveGame":
                    final String leaveGameName = json.get("gameName").textValue();
                    final String fbId = json.get("facebookId").textValue();
                    final GameMssg.LeaveGame leaveGame = new GameMssg.LeaveGame(leaveGameName, fbId);
                    gamesActor.tell(leaveGame, self());
                    System.out.println("leaveGame = " + leaveGame);
                    break;
            }
        }
    }

    public void close() {
        self().tell(PoisonPill.getInstance(), self());
    }



    public interface Factory {
        Actor create(ActorRef out);
    }
}
