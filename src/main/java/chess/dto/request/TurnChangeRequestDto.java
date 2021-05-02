package chess.dto.request;

import java.beans.ConstructorProperties;

public class TurnChangeRequestDto {
    private final String currentTurn;
    private final String nextTurn;
    private final Long roomId;

    @ConstructorProperties({"currentTurn, nextTurn, roomId"})
    public TurnChangeRequestDto(final String currentTurn, final String nextTurn, final Long roomId) {
        this.currentTurn = currentTurn;
        this.nextTurn = nextTurn;
        this.roomId = roomId;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public String getNextTurn() {
        return nextTurn;
    }

    public Long getRoomId() {
        return roomId;
    }
}