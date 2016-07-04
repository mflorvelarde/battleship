package model.ships;

import static model.ships.ShipType.*;

/**
 * Created by tomasnajun on 21/06/16.
 */
public class FactoryShip {
    private int id;

    public FactoryShip() {
        id = 1;
    }

    public Ship getShip(int size) {
        switch (size) {
            case 1: return new Ship(MINESWEEPER, id++);
            case 2: return new Ship(FRIGATE, id++);
            case 3: return new Ship(CRUISER, id++);
            default: return null;
        }
    }
}
