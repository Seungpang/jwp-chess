package chess.service.dto;

public class ChessGameDto {
    private final String status;
    private final String turn;
    private Long id;
    private String name;
    private String password;

    public ChessGameDto(Long id, String status, String turn) {
        this.id = id;
        this.status = status;
        this.turn = turn;
    }

    public ChessGameDto(Long id, String name, String status, String turn, String password) {
        this.status = status;
        this.turn = turn;
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status.toUpperCase();
    }

    public String getTurn() {
        return turn.toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
