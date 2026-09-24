package icesi.truecel.minesweeperclient.connection.dto;
import icesi.truecel.minesweeperclient.model.Cell;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class CellService {
    private final Cell[][] board;
    private final boolean win;
    private final boolean gameEnd;
}
