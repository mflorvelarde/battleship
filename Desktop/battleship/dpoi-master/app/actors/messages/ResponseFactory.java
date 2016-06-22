package actors.messages;

import com.fasterxml.jackson.databind.JsonNode;
import model.FinishedGameStatus;
import model.ships.HitResult;
import play.libs.Json;

/**
 * Created by tomasnajun on 21/06/16.
 */
public class ResponseFactory {

    private ResponseFactory() {
    }

    public static JsonNode gameCreated(long gameId) {
        return Json.newObject()
                .put("type", "gameCreated")
                .put("gameName", gameId);
    }

    public static JsonNode startGame(String playerWhoPlayfacebookId) {
        return Json.newObject()
                .put("type", "startGame")
                .put("facebookId", playerWhoPlayfacebookId);
    }

    public static JsonNode receiveShoot(int row, int col, HitResult hitResult) {
        return shoot("receiveShoot", row, col, hitResult);
    }

    public static JsonNode shootResult(int row, int col, HitResult hitResult) {
        return shoot("shootResult", row, col, hitResult);
    }

    private static JsonNode shoot(String shootType, int row, int col, HitResult hitResult) {
        return Json.newObject()
                .put("type", shootType)
                .put("row", row)
                .put("col", col)
                .put("result", hitResult.name().toLowerCase());
    }

    public static JsonNode yourTurn() {
        return Json.newObject()
                .put("type", "yourTurn");
    }

    public static JsonNode endGame(FinishedGameStatus finishedGameStatus) {
        return Json.newObject()
                .put("type", "endGame")
                .put("status", finishedGameStatus.name().toLowerCase());
    }
}
