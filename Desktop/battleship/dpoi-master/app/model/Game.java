package model;

import com.avaje.ebean.Model;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Date;

import static org.joda.time.DateTime.now;

/**
 * Created by tomasnajun on 02/06/16.
 */
@Entity
public class Game extends Model {
    @Id
    private long id;
    private String facebookId;
    private String currentPlayerFbId;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "gamesWon", referencedColumnName = "id")
    private Player winner;
    @ManyToOne
    @JoinColumn(name = "gamesLost", referencedColumnName = "id")
    private Player looser;
//    @OneToOne
//    @JoinColumn(name = "playerBoard", referencedColumnName = "id")
    private GameBoard playerBoard;
//    @OneToOne
//    @JoinColumn(name = "opponentBoard", referencedColumnName = "id")
    private GameBoard opponentBoard;

    public Game(Player player1, Player player2) {
        playerBoard = new GameBoard(player1);
        opponentBoard = new GameBoard(player2);
        date = now().toDate();
    }

    public Game(Player player1) {
        this.playerBoard = new GameBoard(player1);
        date = now().toDate();
    }

    public static Finder<Long, Game> finder = new Finder<>(Game.class);

    public Date getDate() {
        return date;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLooser() {
        return looser;
    }

    public GameBoard getPlayerBoard() {
        return playerBoard;
    }

    public GameBoard getOpponentBoard() {
        return opponentBoard;
    }

    private void setOpponentBoard(GameBoard opponentBoard) {
        this.opponentBoard = opponentBoard;
    }

    public void setPlayer2(Player player2) {
        final GameBoard gameBoard = new GameBoard(player2);
        setOpponentBoard(gameBoard);
    }

    public long getId() {
        return id;
    }

    public String getCurrentPlayerFbId() {
        return currentPlayerFbId;
    }

    public GameBoard getPlayerGameBoard(Player player) {
        if (playerBoard.getOwner().getId() == player.getId()) {
            return playerBoard;
        } else return opponentBoard;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Game game = (Game) o;

        return id == game.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public void setCurrentPlayerFbId(String fbId) {
        currentPlayerFbId = fbId;
    }

}