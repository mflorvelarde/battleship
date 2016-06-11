package model;

import model.ships.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomasnajun on 02/06/16.
 */
public class GameBoard {
    private List<Ship> ships;
    private Positions hitPoints;
    private Positions missPoints;
    private Player owner;

    public GameBoard(Player owner) {
        this.owner = owner;
        hitPoints = new Positions();
        missPoints = new Positions();
        ships = new ArrayList<>();
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Positions getHitPoints() {
        return hitPoints;
    }

    public Positions getMissPoints() {
        return missPoints;
    }

    public Player getOwner() {
        return owner;
    }
}
