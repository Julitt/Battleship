package com.julitt.battleship.service;

import com.julitt.battleship.boards.BoardField;
import com.julitt.battleship.boards.Ship;
import com.julitt.battleship.model.Coordinates;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Getter
public class Game {

    @Setter
    private boolean gameStarted;
    @Setter
    private boolean myTurn;
    private int boardSize;
    private BoardField[][] opponentsBoard;
    private BoardField[][] myBoard;
    private final List<Ship> ships = new ArrayList();

    public void newGame(boolean myTurn, int boardSize){
        this.gameStarted = true;
        this.myTurn = myTurn;
        this.boardSize = boardSize;
        this.opponentsBoard = new BoardField[boardSize][boardSize];
        this.myBoard = new BoardField[boardSize][boardSize];
        initializeBoards();
    }

    private void initializeBoards(){
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                opponentsBoard[i][j] = BoardField.UNKNOWN;
                myBoard[i][j] = BoardField.UNKNOWN;
            }
        }
        setUpShips();
    }

    public BoardField shoot(Coordinates coordinates){
        Optional<Ship> ship = ships.stream()
                .filter(s -> s.getCoordinates().contains(coordinates))
                .findFirst();
        if(ship.isEmpty()){
            myTurn = true;
            return BoardField.EMPTY;
        }
        ship.get().shoot(coordinates);
        return ship.get().isSunk() ? BoardField.SUNK : BoardField.HIT;
    }

    public void addShip(List<Coordinates> coordinates){
        ships.add(new Ship(coordinates));
    }

    public void myShoot(Coordinates coordinates, BoardField boardField){
        opponentsBoard[coordinates.getX()][coordinates.getY()] = boardField;
        showBoard();
        if(boardField.equals(BoardField.EMPTY)){
            myTurn = false;
        }
    }

    private void showBoard(){
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                BoardField field = opponentsBoard[i][j];
                if(field.equals(BoardField.UNKNOWN)){
                    System.out.print("[ ]");
                }if(field.equals(BoardField.HIT)){
                    System.out.print("[X]");
                }if(field.equals(BoardField.SUNK)){
                    System.out.print("[S]");
                }if(field.equals(BoardField.EMPTY)){
                    System.out.print("[-]");
                }
            }
            System.out.println();
        }
        System.out.println("----------------------------------");
    }

    private void setUpShips(){
        addShip(List.of(new Coordinates(0, 0)));
        addShip(List.of(new Coordinates(0, 2)));
        addShip(List.of(new Coordinates(0, 9)));
        addShip(List.of(new Coordinates(4, 9)));
        addShip(Arrays.asList(new Coordinates(7,1), new Coordinates(6,1)));
        addShip(Arrays.asList(new Coordinates(9,0), new Coordinates(9,1)));
        addShip(Arrays.asList(new Coordinates(9,5), new Coordinates(9,4)));
        addShip(Arrays.asList(new Coordinates(0,4), new Coordinates(0,5), new Coordinates(0, 6)));
        addShip(Arrays.asList(new Coordinates(2,1), new Coordinates(3,1), new Coordinates(4, 1)));
        addShip(Arrays.asList(new Coordinates(6,9), new Coordinates(7,9), new Coordinates(8, 9), new Coordinates(9,9)));
    }
}
