package com.julitt.battleship.board;

import com.julitt.battleship.model.Coordinates;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.julitt.battleship.board.BoardField.HIT;
import static com.julitt.battleship.board.BoardField.SHIP;

@Getter
public class Ship {

    private final Map<Coordinates, BoardField> shipState = new HashMap<>();
    private final List<Coordinates> coordinates;

    public Ship(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
        for (Coordinates coordinate : coordinates) {
            shipState.put(coordinate, SHIP);
        }
    }

    public void shoot(Coordinates coordinates) {
        shipState.put(coordinates, HIT);
    }

    public boolean isSunk() {
        return !shipState.containsValue(SHIP);
    }

}
