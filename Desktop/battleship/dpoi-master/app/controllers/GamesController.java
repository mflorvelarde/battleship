package controllers;

import actors.PlayerParentActor;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status;
import akka.japi.Pair;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.*;
import com.fasterxml.jackson.databind.JsonNode;
import model.Game;
import model.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import play.libs.F;
import play.mvc.*;
import scala.compat.java8.FutureConverters;
import views.html.home;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;
import static model.Player.findByFacebookId;

/**
 * Created by tomasnajun on 12/06/16.
 */
@Singleton
public class GamesController extends Controller{

    private Logger logger = org.slf4j.LoggerFactory.getLogger("controllers.GamesController");

    private final static Map<Game, Set<String>> games = new HashMap<>();
    private final static Map<Game, Set<String>> waitingGames = new HashMap<>();
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final ActorRef playerParentActor;
    private final ActorRef waitingPlayersActor;

    @Inject
    public GamesController(ActorSystem actorSystem,
                           Materializer materializer,
                           @Named("waitingPlayersActor") ActorRef waitingPlayersActor,
                           @Named("playerParentActor") ActorRef playerParentActor) {
        this.waitingPlayersActor = waitingPlayersActor;
        this.playerParentActor = playerParentActor;
        this.materializer = materializer;
        this.actorSystem = actorSystem;
    }

    public Result home() {
        return ok(home.render(request(), session("player"), "", ""));
    }

    public WebSocket ws() {
        return WebSocket.Json.acceptOrResult(request -> {
            if (sameOriginCheck(request)) {
                final CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> future = wsFutureFlow(request);
                final CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> stage = future.thenApplyAsync(F.Either::Right);
                return stage.exceptionally(this::logException);
            } else {
                return forbiddenResult();
            }
        });
    }

    @NotNull
    private CompletionStage<F.Either<Result, Flow<JsonNode, JsonNode, ?>>> forbiddenResult() {
        final Result forbidden = Results.forbidden("forbidden");
        final F.Either<Result, Flow<JsonNode, JsonNode, ?>> left = F.Either.Left(forbidden);

        return CompletableFuture.completedFuture(left);
    }

    public CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> wsFutureFlow(Http.RequestHeader request) {
        // create an actor ref source and associated publisher for sink
        final Pair<ActorRef, Publisher<JsonNode>> pair = createWebSocketConnections();
        ActorRef webSocketOut = pair.first();
        Publisher<JsonNode> webSocketIn = pair.second();

        String id = String.valueOf(request._underlyingHeader().id());
        // Create a user actor off the request id and attach it to the source
        final CompletionStage<ActorRef> userActorFuture = createUserActor(id, webSocketOut);

        // Once we have an actor available, create a flow...
        return userActorFuture
                .thenApplyAsync(userActor -> createWebSocketFlow(webSocketIn, userActor));
    }

    public CompletionStage<ActorRef> createUserActor(String id, ActorRef webSocketOut) {
        // Use guice assisted injection to instantiate and configure the child actor.
        long timeoutMillis = 100L;
        return FutureConverters.toJava(
                ask(playerParentActor, new PlayerParentActor.Create(id, webSocketOut), timeoutMillis)
        ).thenApply(stageObj -> (ActorRef) stageObj);
    }

    public Pair<ActorRef, Publisher<JsonNode>> createWebSocketConnections() {
        // Creates a source to be materialized as an actor reference.

        // Creating a source can be done through various means, but here we want
        // the source exposed as an actor so we can send it messages from other
        // actors.
        final Source<JsonNode, ActorRef> source = Source.actorRef(10, OverflowStrategy.dropTail());

        // Creates a sink to be materialized as a publisher.  Fanout is false as we only want
        // a single subscriber here.
        final Sink<JsonNode, Publisher<JsonNode>> sink = Sink.asPublisher(AsPublisher.WITHOUT_FANOUT);

        // Connect the source and sink into a flow, telling it to keep the materialized values,
        // and then kicks the flow into existence.
        final Pair<ActorRef, Publisher<JsonNode>> pair = source.toMat(sink, Keep.both()).run(materializer);
        return pair;
    }

    public F.Either<Result, Flow<JsonNode, JsonNode, ?>> logException(Throwable throwable) {
        // https://docs.oracle.com/javase/tutorial/java/generics/capture.html
        logger.error("Cannot create websocket", throwable);
        Result result = Results.internalServerError("error");
        return F.Either.Left(result);
    }

    public Flow<JsonNode, JsonNode, NotUsed> createWebSocketFlow(Publisher<JsonNode> webSocketIn, ActorRef userActor) {
        // http://doc.akka.io/docs/akka/current/scala/stream/stream-flows-and-basics.html#stream-materialization
        // http://doc.akka.io/docs/akka/current/scala/stream/stream-integrations.html#integrating-with-actors

        // source is what comes in: browser ws events -> play -> publisher -> userActor
        // sink is what comes out:  userActor -> websocketOut -> play -> browser ws events
        final Sink<JsonNode, NotUsed> sink = Sink.actorRef(userActor, new Status.Success("success"));
        final Source<JsonNode, NotUsed> source = Source.fromPublisher(webSocketIn);
        final Flow<JsonNode, JsonNode, NotUsed> flow = Flow.fromSinkAndSource(sink, source);

        // Unhook the user actor when the websocket flow terminates
        // http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#watchTermination
        return flow.watchTermination((ignore, termination) -> {
            termination.whenComplete((done, throwable) -> {
                logger.info("Terminating actor {}", userActor);
//                stocksActor.tell(new Stock.Unwatch(null), userActor);
                actorSystem.stop(userActor);
            });

            return NotUsed.getInstance();
        });
    }

    /**
     * Checks that the WebSocket comes from the same origin.  This is necessary to protect
     * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
     * <p>
     * See https://tools.ietf.org/html/rfc6455#section-1.3 and
     * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
     */
    public boolean sameOriginCheck(Http.RequestHeader rh) {
        final String origin = rh.getHeader("Origin");

        if (origin == null) {
            logger.error("originCheck: rejecting request because no Origin header found");
            return false;
        } else if (originMatches(origin)) {
            logger.debug("originCheck: originValue = " + origin);
            return true;
        } else {
            logger.error("originCheck: rejecting request because Origin header value " + origin + " is not in the same origin");
            return false;
        }
    }

    private boolean originMatches(String origin) {
        return origin.contains("localhost:9000") || origin.contains("localhost:19001");
    }

    public long joinGame(String facebookId) {
        final Iterator<Map.Entry<Game, Set<String>>> iterator = waitingGames.entrySet().iterator();
        final long gameId;
        if (iterator.hasNext()) {
            gameId = joinToAWaitingGame(facebookId, iterator.next());
        } else {
            gameId = joinToANewGame(facebookId);
        }
        return gameId;
    }

    private long joinToANewGame(String facebookId) {
        final Player player = findByFacebookId(facebookId);
        final Game game = new Game(player);
        game.setCurrentPlayerFbId(facebookId);
        game.save();
        final HashSet<String> players = new HashSet<>();
        players.add(facebookId);
        waitingGames.put(game, players);
        return  game.getId();
    }

    private long joinToAWaitingGame(String facebookId, Map.Entry<Game, Set<String>> gameSetEntry) {
        final Game game = gameSetEntry.getKey();
        game.setPlayer2(findByFacebookId(facebookId));
        game.update();
        final Set<String> players = gameSetEntry.getValue();
        players.add(facebookId);
        games.put(game, players);
        waitingGames.remove(game);
        return game.getId();
    }

    @Nullable public Game removeUserFromGame(String facebookId) {
        Game removedGame = null;
        for(Game key : games.keySet()) {
            if(games.get(key).remove(facebookId)) {
                removedGame = key;
                break;
            }
        }
        return removedGame;
    }

    /**
     *
     * @param shooterId: player facebookId
     * @return hit or not
     */
    public boolean shoot(int row, int column, String shooterId) {
        final Player player = Player.findByFacebookId(shooterId);
        final Game game = getGameFromFbId(shooterId);
        if (game != null) {

        }
        return false;
    }

    /**
     *
     * @param playerFbId: player FacebookId
     * @param positions: [row][column] squares took up by ship
     * @param shipSize: {@link model.ships.ShipType}
     * @return set successful
     */
    public boolean setShip(long playerFbId, int[][] positions, int shipSize) {
        System.out.println("playerFbId = [" + playerFbId + "], positions = [" + Arrays.deepToString(positions) + "], shipSize = [" + shipSize + "]");
        return false;
    }

    /**
     *
     * @param gameId
     * @return User Fb Id
     */
    public String startGame(long gameId) {
        final Game game = Game.finder.byId(gameId);
        //TODO guardar boards del game
        if (game != null) return game.getCurrentPlayerFbId();
        return "";
    }

    @Nullable
    private Game getGameFromFbId(String facebookId) {
        for (final Game game : games.keySet()) {
            if (games.get(game).contains(facebookId)) {
                return game;
            }
        }
        return null;
    }
}
