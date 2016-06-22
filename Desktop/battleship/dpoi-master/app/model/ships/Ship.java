package model.ships;

/**
 * Created by tomasnajun on 02/06/16.
 */
public class Ship {
    private ShipType shipType;
    private int hits;
    private boolean isSink;
    private int id;

    public Ship(ShipType shipType, int id) {
        this.shipType = shipType;
        this.id = id;
    }

    public HitResult receiveShoot() {
        HitResult result;
        hits++;
        result = HitResult.HIT;
        if (hits >= shipType.getSize()) {
            isSink = true;
            result = HitResult.SINK;
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public boolean isSink() {
        return isSink;
    }
}
