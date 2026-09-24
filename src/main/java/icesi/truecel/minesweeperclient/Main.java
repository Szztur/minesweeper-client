package icesi.truecel.minesweeperclient;
import icesi.truecel.minesweeperclient.connection.ServerService;
import icesi.truecel.minesweeperclient.model.BoardGame;
import icesi.truecel.minesweeperclient.service.BoardService;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BoardService boardService = new BoardService(
                new ServerService("localhost", 12345),
                new BoardGame());
        boardService.interact();
    }
}