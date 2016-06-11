package model.ships;

/**
 * Created by tomasnajun on 02/06/16.
 */
public enum ShipType {
    MINESWEEPER(2), FRIGATE(3), CRUISER(4), BATTLESHIP(5);

    private int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
