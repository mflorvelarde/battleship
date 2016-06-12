package hubs;

import model.FinishedGameStatus;
import model.Position;

/**
 * Created by tomasnajun on 01/06/16.
 */
public interface GamePage {
    public void oponentJoined(long facebookId);
    public void oppenentJoinedGame(long facebookId);
    public void receiveShoot(Position position, boolean hit);
    public void endGame(FinishedGameStatus status);
}
