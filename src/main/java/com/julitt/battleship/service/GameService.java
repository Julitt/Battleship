package com.julitt.battleship.service;

import com.julitt.battleship.board.BoardField;
import com.julitt.battleship.board.BoardUI;
import com.julitt.battleship.board.Ship;
import com.julitt.battleship.model.Coordinates;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.julitt.battleship.board.BoardField.*;

@Service
@Getter
public class GameService {

    @Setter
    private boolean gameStarted;
    @Setter
    private boolean myTurn;
    private int boardSize;
    private BoardField[][] opponentsBoard;
    //TODO show myBoard private BoardField[][] myBoard;
    private final List<Ship> ships = new ArrayList<>();

    public void newGame(boolean myTurn, int boardSize) {
        this.gameStarted = true;
        this.myTurn = myTurn;
        this.boardSize = boardSize;
        this.opponentsBoard = new BoardField[boardSize][boardSize];
        initializeBoards();
    }

    private void initializeBoards() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                opponentsBoard[i][j] = UNKNOWN;
            }
        }
        setUpShips();
    }

    public BoardField shoot(Coordinates coordinates) {
        Optional<Ship> ship = ships.stream()
                .filter(s -> s.getCoordinates().contains(coordinates))
                .findFirst();
        if (ship.isEmpty()) {
            myTurn = true;
            return EMPTY;
        }
        ship.get().shoot(coordinates);
        return ship.get().isSunk() ? SUNK : HIT;
    }

    public void addShip(List<Coordinates> coordinates) {
        ships.add(new Ship(coordinates));
    }

    public void myShot(Coordinates coordinates, BoardField result) {
        opponentsBoard[coordinates.getX()][coordinates.getY()] = result;
        if (result.equals(EMPTY)) {
            myTurn = false;
        } else if (result.equals(SUNK)) {
            markShipSunk(coordinates.getX(), coordinates.getY());
        }
        BoardUI.showBoard(opponentsBoard);
    }

    private void setUpShips() {
        addShip(List.of(new Coordinates(0, 0)));
        addShip(List.of(new Coordinates(0, 2)));
        addShip(List.of(new Coordinates(0, 9)));
        addShip(List.of(new Coordinates(4, 9)));
        addShip(Arrays.asList(new Coordinates(7, 1), new Coordinates(6, 1)));
        addShip(Arrays.asList(new Coordinates(9, 0), new Coordinates(9, 1)));
        addShip(Arrays.asList(new Coordinates(9, 5), new Coordinates(9, 4)));
        addShip(Arrays.asList(new Coordinates(0, 4), new Coordinates(0, 5), new Coordinates(0, 6)));
        addShip(Arrays.asList(new Coordinates(2, 1), new Coordinates(3, 1), new Coordinates(4, 1)));
        addShip(Arrays.asList(new Coordinates(6, 9), new Coordinates(7, 9), new Coordinates(8, 9), new Coordinates(9, 9)));
    }

    public void showShot(Coordinates shoot, BoardField result) {
        System.out.println("Shoot at " + shoot.getX() + ", " + shoot.getY());
        System.out.println("Result: " + result);
        System.out.println();
    }

    private void markShipSunk(int x, int y) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (x + i >= 0 && x + i < boardSize && y + j >= 0 && y + j < boardSize) {
                    if (opponentsBoard[x + i][y + j].equals(UNKNOWN)) {
                        opponentsBoard[x + i][y + j] = EMPTY;
                    } else if (opponentsBoard[x + i][y + j].equals(HIT)) {
                        opponentsBoard[x + i][y + j] = SUNK;
                        markShipSunk(x + i, y + j);
                    }
                }
            }
        }
    }
}
