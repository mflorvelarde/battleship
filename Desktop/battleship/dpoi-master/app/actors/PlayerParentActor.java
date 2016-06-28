package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;

/**
 * Created by tomasnajun on 20/06/16.
 */
public class PlayerParentActor extends UntypedActor implements InjectedActorSupport {
    public static class Create {
        private String id;
        private ActorRef out;
        private long playerId;

        public Create(String id, ActorRef out, long playerId) {
            this.id = id;
            this.out = out;
            this.playerId = playerId;
        }
    }

    private PlayerActor.Factory childFactory;

    @Inject
    public PlayerParentActor(PlayerActor.Factory childFactory) {
        this.childFactory = childFactory;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PlayerParentActor.Create) {
            PlayerParentActor.Create create = (PlayerParentActor.Create) message;
            ActorRef child = injectedChild(() -> childFactory.create(create.out, create.playerId), "playerActor-" + create.id);
            sender().tell(child, self());
        }
    }
}
