package model;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomasnajun on 02/06/16.
 */
@Entity
public class Player extends Model {
    @Id
    private long id;
    public String name;
    public long facebookId;
    @OneToMany(mappedBy = "winner")
    public List<Game> gamesWon;
    @OneToMany(mappedBy = "looser")
    public List<Game> gamesLost;
    @JoinColumn(name = "player", referencedColumnName = "id")
    public Statistics statistics;

    public static Finder<Long, Player> find = new Finder<Long, Player>(Player.class);


    public Player(String name, long facebookId) {
        this.name = name;
        this.facebookId = facebookId;
        this.gamesWon = new ArrayList<Game>();
        this.gamesLost = new ArrayList<Game>();
    }

    public long getId() {
        return id;
    }

    public void addStatisctics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void addWonGame(Game game) {
        this.gamesWon.add(game);
        this.gamesLost.add(game);
    }
}