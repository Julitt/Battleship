package com.julitt.battleship.board;

import java.util.HashMap;
import java.util.Map;

import static com.julitt.battleship.board.BoardField.*;

public class BoardUI {

    private static Map<BoardField, Character> viewBoard;

    public static void showBoard(BoardField[][] boardFields){
        createMap();
        int boardSize = boardFields.length;
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                System.out.print(viewBoard.get(boardFields[i][j]));
            }
            System.out.println();
        }
        System.out.println("----------------------------------");
    }

    private static void createMap(){
        viewBoard = new HashMap<>();
        viewBoard.put(UNKNOWN, '~');
        viewBoard.put(HIT, 'X');
        viewBoard.put(EMPTY, '*');
        viewBoard.put(SUNK, 'X');
    }

}
