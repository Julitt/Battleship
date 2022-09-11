package com.julitt.battleship.board;

import java.util.HashMap;
import java.util.Map;

import static com.julitt.battleship.board.BoardField.*;

public class BoardUI {

    public static void showBoard(BoardField[][] boardFields) {
        Map<BoardField, Character> viewBoard = createMap();
        int boardSize = boardFields.length;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(viewBoard.get(boardFields[i][j]));
            }
            System.out.println();
        }
        System.out.println("----------------------------------");
    }

    private static Map<BoardField, Character> createMap() {
        return new HashMap<>() {{
            put(UNKNOWN, '~');
            put(HIT, 'X');
            put(EMPTY, '*');
            put(SUNK, 'X');
        }};

    }

}
