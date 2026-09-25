package co.icesi.buscaminas.client.connection.dto;

import co.icesi.buscaminas.client.model.Cell;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class CellService {
    private final Cell[][] board;
    private final boolean win;
    private final boolean gameEnd;
}
