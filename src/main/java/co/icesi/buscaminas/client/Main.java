package co.icesi.buscaminas.client;

import co.icesi.buscaminas.client.connection.ServerService;
import co.icesi.buscaminas.client.model.BoardGame;
import co.icesi.buscaminas.client.service.BoardService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BoardService boardService = new BoardService(
                new ServerService("localhost", 12345),
                new BoardGame());
        boardService.interact();
    }
}