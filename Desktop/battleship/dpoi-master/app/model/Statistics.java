package model;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created by tomasnajun on 02/06/16.
 */
@Entity
public class Statistics extends Model {
    @Id
    private long id;
    @OneToOne
    @JoinColumn(name = "player", referencedColumnName = "id")
    public Player player;
    public int wins;
    public int looses;

    public static Finder<Long, Statistics> find = new Finder<Long, Statistics>(Statistics.class);


    public Statistics(Player player) {
        this.player = player;
        this.wins = 0;
        this.looses = 0;
    }

    public long getId() {
        return id;
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoose() {
        this.looses++;
    }
}
