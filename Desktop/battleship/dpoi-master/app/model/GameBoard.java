package model;

import model.ships.FactoryShip;
import model.ships.HitResult;
import model.ships.Ship;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tomasnajun on 02/06/16.
 */
public class GameBoard {
    private static final int BOARD_SIZE = 10;
    private static final int WATER = -1;
    private static final int HIT = 1;
    private List<Ship> ships;
    private int[][] myBoard;
    private int[][] opponentBoard;
    private Player owner;
    private FactoryShip factoryShip;

    public GameBoard(Player owner) {
        this.owner = owner;
        ships = new ArrayList<>();
        myBoard = new int[BOARD_SIZE][BOARD_SIZE];
        opponentBoard = new int[BOARD_SIZE][BOARD_SIZE];
        factoryShip = new FactoryShip();
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Player getOwner() {
        return owner;
    }

    public void setShip(int size, Integer[] rows, Integer[] cols) {
        final Ship ship = factoryShip.getShip(size);
        ships.add(ship);
        final int shipId = ship.getId();
        for (final int row : rows) {
            for (final int col : cols) {
                myBoard[row - 1][col - 1] = shipId;
            }
        }
    }

    @Nullable
    private Ship getShipFromId(int id) {
        for (final Ship ship : ships) {
            if (ship.getId() == id) {
                return ship;
            }
        }
        return null;
    }

    public HitResult receiveShoot(int row, int col) {
        final int shipId = myBoard[row - 1][col - 1];

        HitResult result;
        final Ship ship = getShipFromId(shipId);
        if (ship != null) result = ship.receiveShoot();
        else result = HitResult.MISS;
        //check if i loose
        final boolean gameIsLost = ships.stream().allMatch(Ship::isSink);
        if (gameIsLost) result = HitResult.WIN;

        return result;
    }

    public void annotate(int row, int col, HitResult hitResult) {
        final int i = row - 1;
        final int j = col - 1;
        if (hitResult.equals(HitResult.MISS)) opponentBoard[i][j] = WATER;
        else opponentBoard[i][j] = HIT;
    }
}
