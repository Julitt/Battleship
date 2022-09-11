package com.julitt.battleship.boards;

import com.julitt.battleship.model.Coordinates;
import lombok.Getter;

import java.util.*;

@Getter
public class Ship {

    private Map<Coordinates, BoardField> shipState = new HashMap();
    private final List<Coordinates> coordinates;

    public Ship(List<Coordinates> coordinates){
        this.coordinates = coordinates;
        for (Coordinates coordinate : coordinates) {
            shipState.put(coordinate, BoardField.SHIP);
        }
    }

    public void shoot(Coordinates coordinates){
        shipState.put(coordinates, BoardField.HIT);
    }

    public boolean isSunk(){
        return !shipState.containsValue(BoardField.SHIP);
    }

}
