package icesi.truecel.minesweeperclient.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private String action;
    private Map<String, String> data;
}
