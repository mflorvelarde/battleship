package hubs;

import model.FinishedGameStatus;

/**
 * Created by tomasnajun on 01/06/16.
 */
public interface GamePage {
    public void opponentJoinedGame(long facebookId);
//    public void receiveShoot(Position position, boolean hit);
    public void endGame(FinishedGameStatus status);
    public void yourTurn();
}
