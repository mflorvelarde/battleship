package model.ships;

/**
 * Created by tomasnajun on 02/06/16.
 */
public enum ShipType {
    MINESWEEPER(1), FRIGATE(2), CRUISER(3), BATTLESHIP(4);

    private int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
