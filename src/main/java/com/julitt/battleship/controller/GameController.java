package com.julitt.battleship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.julitt.battleship.board.BoardField;
import com.julitt.battleship.model.Coordinates;
import com.julitt.battleship.model.ShootResponse;
import com.julitt.battleship.model.Start;
import com.julitt.battleship.service.GameService;
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
    GameService gameService;

    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();

    private final int BOARD_SIZE = 10;
    private final String URL = "http://localhost:8080";

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<String> startGame(@RequestBody Start start) {
        gameService.newGame(start.isYouStart(), BOARD_SIZE);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/shoot", method = RequestMethod.POST)
    public ResponseEntity<String> shoot(@RequestBody Coordinates shoot) {
        if (!gameService.isGameStarted() || gameService.isMyTurn()) {
            return new ResponseEntity<>("It's my turn", HttpStatus.NOT_ACCEPTABLE);
        }
        BoardField result = gameService.shoot(shoot);
        gameService.showShot(shoot, result);
        return new ResponseEntity<>(gson.toJson(new ShootResponse(result.toString())), HttpStatus.OK);
    }

    @RequestMapping(value = "/addShip", method = RequestMethod.POST)
    public ResponseEntity<String> addShip(@RequestBody List<Coordinates> coordinates) {
        gameService.addShip(coordinates);
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
        gameService.myShot(coordinates, BoardField.valueOf(shootResponse.getResult()));
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
        if (response.statusCode() == 200) {
            gameService.newGame(!start.isYouStart(), BOARD_SIZE);
        }
        return new ResponseEntity<>(String.valueOf(response.body()), HttpStatus.OK);
    }

}
