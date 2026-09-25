package co.icesi.buscaminas.client.connection;



import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

import co.icesi.buscaminas.client.connection.dto.CellService;
import co.icesi.buscaminas.client.connection.dto.Request;
import co.icesi.buscaminas.client.connection.dto.Response;
import co.icesi.buscaminas.client.model.Cell;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;


public class ServerService {
    private static final int SOCKET_TIMEOUT_MS = 5000;

    private final String host;
    private final int port;
    private final Gson gson;
    private final Type boardType;

    public ServerService(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new GsonBuilder().create();
        this.boardType = new TypeToken<Cell[][]>() {}.getType();
    }

    private Response sendRequest(Request request) {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String jsonOut = gson.toJson(request);
            writer.write(jsonOut);
            writer.newLine();
            writer.flush();

            String jsonIn = reader.readLine();
            if (jsonIn == null) {
                throw new IllegalStateException("The server closed the connection without a response");
            }

            Response response = gson.fromJson(jsonIn, Response.class);
            validateResponse(response);
            return response;

        } catch (SocketTimeoutException e) {
            throw new RuntimeException("The server took too long to respond (timeout)", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not communicate with the server: " + e.getMessage(), e);
        }
    }

    private void validateResponse(Response response) {
        if (response == null) {
            throw new IllegalStateException("The server returned an empty or invalid response");
        }
        // Solo tratamos como error el status EXPLÍCITO "ERROR".
        // El servidor deja status en null en el caso de "game over" al pisar
        // una mina (excepción interna en SELECT_CELL), así que null u "OK"
        // se dejan pasar para no romper ese flujo normal del juego.
        if ("ERROR".equals(response.getStatus())) {
            String message = (response.getData() != null && response.getData().get("message") != null)
                    ? String.valueOf(response.getData().get("message"))
                    : "Unknown server error";
            throw new RuntimeException("Server error: " + message);
        }
    }

    private Cell[][] extractBoard(Response response) {
        Object boardData = response.getData() != null ? response.getData().get("board") : null;
        if (boardData == null) {
            throw new IllegalStateException("The server returned no board");
        }
        return gson.fromJson(gson.toJson(boardData), boardType);
    }

    public Cell[][] getBoard() {
        Response res = sendRequest(new Request("GET_BOARD", null));
        return extractBoard(res);
    }

    public Cell[][] showAllBoard() {
        Response res = sendRequest(new Request("SOW_ALL", null));
        return extractBoard(res);
    }

    public Cell[][] initBoard(int n, int m, int mines) {
        Request req = new Request("INIT_GAME",
                Map.of("n", String.valueOf(n), "m", String.valueOf(m), "minas", String.valueOf(mines)));
        Response res = sendRequest(req);
        return extractBoard(res);
    }

    public Cell[][] markCell(int i, int j) {
        Request req = new Request("MARK_CELL",
                Map.of("i", String.valueOf(i), "j", String.valueOf(j)));
        Response res = sendRequest(req);
        return extractBoard(res);
    }

    public CellService selectCell(int i, int j) {
        Request req = new Request("SELECT_CELL",
                Map.of("i", String.valueOf(i), "j", String.valueOf(j)));
        Response res = sendRequest(req);

        Cell[][] board = extractBoard(res);
        boolean win = Boolean.TRUE.equals(res.getData().get("win"));
        boolean gameEnd = Boolean.TRUE.equals(res.getData().get("gameEnd"));
        return new CellService(board, win, gameEnd);
    }
}