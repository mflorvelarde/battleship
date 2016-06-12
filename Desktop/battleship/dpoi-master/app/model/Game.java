package model;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

import static org.joda.time.DateTime.now;

/**
 * Created by tomasnajun on 02/06/16.
 */
@Entity
public class Game extends Model {
    @Id
    private long id;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "gamesWon", referencedColumnName = "id")
    private Player winner;
    @ManyToOne
    @JoinColumn(name = "gamesLost", referencedColumnName = "id")
    private Player looser;
    private GameBoard board1, board2;

    public Game(Player player1, Player player2) {
        board1 = new GameBoard(player1);
        board2 = new GameBoard(player2);
        date = now().toDate();
    }

    public Game(Player player1) {
        this.board1 = new GameBoard(player1);
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

    public GameBoard getBoard1() {
        return board1;
    }

    public GameBoard getBoard2() {
        return board2;
    }

    private void setBoard2(GameBoard board2) {
        this.board2 = board2;
    }

    public void setPlayer2(Player player2) {
        final GameBoard gameBoard = new GameBoard(player2);
        setBoard2(gameBoard);
    }

    public long getId() {
        return id;
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
}