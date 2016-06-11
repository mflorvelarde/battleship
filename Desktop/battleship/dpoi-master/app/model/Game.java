package model;

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
public class Game {
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
        this.winner = player1;
        this.looser = player2;
        board1 = new GameBoard(player1);
        board2 = new GameBoard(player2);
        date = now().toDate();
    }

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
}