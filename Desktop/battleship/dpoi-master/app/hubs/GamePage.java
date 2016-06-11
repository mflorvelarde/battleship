package hubs;

import java.util.Set;

/**
 * Created by tomasnajun on 01/06/16.
 */
public interface GamePage {
    public void userJoined(String username);
    public void sendMessage(String username, String message);
    public void userLeftGame(String username);
    public void userJoinedGame(String username);
    public void gameList(Set<String> rooms);
    public void userList(Set<String> users);
}
