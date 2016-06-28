package model;

import com.avaje.ebean.Model;
import com.avaje.ebeaninternal.server.lib.util.Str;
import org.jetbrains.annotations.Nullable;
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
    public String facebookId;
    @OneToMany(mappedBy = "winner")
    public List<Game> gamesWon;
    @OneToMany(mappedBy = "looser")
    public List<Game> gamesLost;
    @JoinColumn(name = "statistics", referencedColumnName = "id")
    public Statistics statistics;

    public static Finder<Long, Player> find = new Finder<>(Player.class);

    @Nullable
    public static Player findByFacebookId(long facebookId) {
        final List<Player> players = find.where().eq("facebookId", facebookId).findList();
        if (players.size() > 0) return players.get(0);
        return   null;
    }

    public Player(String name, String facebookId) {
        this.name = name;
        this.facebookId = facebookId;
        this.gamesWon = new ArrayList<>();
        this.gamesLost = new ArrayList<>();
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

    public String getFacebookId() {
        return facebookId;
    }
}