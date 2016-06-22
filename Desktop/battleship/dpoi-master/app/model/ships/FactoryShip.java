package model.ships;

import static model.ships.ShipType.*;

/**
 * Created by tomasnajun on 21/06/16.
 */
public class FactoryShip {
    private int id;

    public Ship getShip(int size) {
        switch (size) {
            case 2: return new Ship(MINESWEEPER, id++);
            case 3: return new Ship(FRIGATE, id++);
            case 4: return new Ship(CRUISER, id++);
            case 5: return new Ship(BATTLESHIP, id++);
            default: return null;
        }
    }
}
