package actors;

import actors.messages.GameMssg;
import actors.messages.GameMssg.JoinGame;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

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
    public void onReceive(Object msg) throws Exception {

        if (msg instanceof JsonNode) {
            final JsonNode json = (JsonNode) msg;
            System.out.println(msg.toString());
            final String type = json.get("type").textValue();
            System.out.println("hello = " + type);

            final ObjectMapper objectMapper = new ObjectMapper();
            switch (type) {
                case "joinGame":
                    final String facebookId = json.get("facebookId").textValue();
                    final String name = json.get("name").textValue();
                    final JoinGame joinGame = new JoinGame(new GameMssg.PlayerMssg(sender(), name, facebookId));
                    waitingPlayersActor.tell(joinGame, self());
                    break;
                case "setShip":
                    final GameMssg.SetShip setShip = objectMapper.readValue(json.asText(), GameMssg.SetShip.class);
                    gamesActor.tell(setShip, self());
                    System.out.println("setShip = " + setShip);
                    break;
                case "shoot":
                    final GameMssg.Shoot shoot = objectMapper.readValue(json.asText(), GameMssg.Shoot.class);
                    gamesActor.tell(shoot, sender());
                    System.out.println("shoot = " + shoot);
                    break;
                case "leaveGame":
                    final GameMssg.LeaveGame leaveGame = objectMapper.readValue(json.asText(), GameMssg.LeaveGame.class);
                    gamesActor.tell(leaveGame, sender());
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
