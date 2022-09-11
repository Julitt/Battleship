package com.julitt.battleship.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.julitt.battleship.service.Game;
import com.julitt.battleship.boards.BoardField;
import com.julitt.battleship.model.Coordinates;
import com.julitt.battleship.model.ShootResponse;
import com.julitt.battleship.model.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
public class GameController {

    @Autowired
    Game game;

    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();

    private final int BOARD_SIZE = 10;
    private final String URL = "http://192.168.1.10:9909";

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<String> startGame(@RequestBody Start start) {
        game.newGame(start.isYouStart(), BOARD_SIZE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/shoot", method = RequestMethod.POST)
    public ResponseEntity<String> shoot(@RequestBody Coordinates shoot) {
        if(!game.isGameStarted() || game.isMyTurn()){
            return new ResponseEntity<>("It's my turn", HttpStatus.NOT_ACCEPTABLE);
        }
        BoardField result = game.shoot(shoot);
        System.out.println("Shoot at "+shoot.getX() + ", " + shoot.getY());
        System.out.println("Result: "+ result);
        System.out.println();
        return new ResponseEntity<>(gson.toJson(new ShootResponse(result.toString())), HttpStatus.OK);
    }

    @RequestMapping(value = "/addShip", method = RequestMethod.POST)
    public ResponseEntity<String> addShip(@RequestBody List<Coordinates> coordinates) {
        game.addShip(coordinates);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/sendShoot", method = RequestMethod.POST)
    public ResponseEntity<String> sendShoot(@RequestBody Coordinates coordinates) throws IOException, InterruptedException {
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(coordinates);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/shoot"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ShootResponse shootResponse = gson.fromJson(response.body(), ShootResponse.class);
        game.myShoot(coordinates, BoardField.valueOf(shootResponse.getResult()));
        return new ResponseEntity<>(String.valueOf(response.body()), HttpStatus.OK);
    }

    @RequestMapping(value = "/sendStart", method = RequestMethod.POST)
    public ResponseEntity<String> sendStart(@RequestBody Start start) throws IOException, InterruptedException {
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(start);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/start"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            game.newGame(!start.isYouStart(), BOARD_SIZE);
        }
        return new ResponseEntity<>(String.valueOf(response.body()), HttpStatus.OK);
    }

}
