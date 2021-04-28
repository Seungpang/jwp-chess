package chess.controller;

import chess.dao.ChessGameDAO;
import chess.dao.PieceDAO;
import chess.dto.ChessGameInfoResponseDto;
import chess.dto.ChessGamesSaveDto;
import chess.service.ChessGameService;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChessApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChessGameService chessGameService;

    @Autowired
    private PieceDAO pieceDAO;

    @Autowired
    private ChessGameDAO chessGameDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        pieceDAO = new PieceDAO(jdbcTemplate);
        chessGameDAO = new ChessGameDAO(jdbcTemplate);
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM piece");
        jdbcTemplate.execute("DELETE FROM chess_game");
    }

    @DisplayName("피스를 움직이는 API 요청")
    @Test
    void testMovePiece() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        chessGameDAO.updateState(newChessGame.getChessGameId(), "BlackTurn");
        String source = "a7";
        String target = "a5";

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/chessgames/{chessGameId}/pieces?source={source}&target={target}",
                        newChessGame.getChessGameId(), source, target)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("pieceDtos.size()", is(32))
                .body("state", is("WhiteTurn"))
                .body("finished", is(false));
    }

    @DisplayName("허용된 피스를 찾지못했을 때, 피스를 움직이는 API 요청")
    @Test
    void testMovePieceIfNoSuchPermittedChessPieceException() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        chessGameDAO.updateState(newChessGame.getChessGameId(), "BlackTurn");
        String source = "a2";
        String target = "a3";

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/chessgames/{chessGameId}/pieces?source={source}&target={target}",
                        newChessGame.getChessGameId(), source, target)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is("허용된 체스 말을 찾을 수 없습니다."));
    }

    @DisplayName("존재하지 않는 체스 위치로 피스를 선택하거나 또는 그 위치로 움직이는 API 요청")
    @Test
    void testMovePieceIfNotPermittedChessPosition() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        String source = "a9";
        String target = "b7";

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/chessgames/{chessGameId}/pieces?source={source}&target={target}",
                        newChessGame.getChessGameId(), source, target)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is("허용되지 않는 체스 위치입니다."));
    }

    @DisplayName("움직일 수 없는 위치로 움직이는 API 요청")
    @Test
    void testMovePieceIfNotMoveToTargetPosition() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        chessGameDAO.updateState(newChessGame.getChessGameId(), "BlackTurn");
        String source = "a7";
        String target = "b6";

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/chessgames/{chessGameId}/pieces?source={source}&target={target}",
                        newChessGame.getChessGameId(), source, target)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is("해당 위치로는 이동할 수 없습니다."));
    }

    @DisplayName("체스게임을 조회하는 API 요청 ")
    @Test
    void testFindChessGame() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/chessgames/{chessGameId}", newChessGame.getChessGameId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("pieceDtos.size()", is(32))
                .body("state", is("Ready"))
                .body("finished", is(false));
    }

    @DisplayName("존재하지 않는 체스게임을 조회하는 API 요청 ")
    @Test
    void testFindChessGameIfNotExistPlayingChessGame() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        chessGameDAO.updateState(newChessGame.getChessGameId(), "End");

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/chessgames/{chessGameId}", newChessGame.getChessGameId() + 1L)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("새로운 체스 게임을 만드는 API 요청")
    @Test
    void testCreateNewChessGame() {
        //when //then
        ChessGamesSaveDto chessGamesSaveDto = new ChessGamesSaveDto("title");
        RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(chessGamesSaveDto)
                .when().post("/api/chessgames")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("state", is("Ready"))
                .body("finished", is(false));
    }

    @DisplayName("체스 게임을 종료하는 요청을 ")
    @Test
    void testEndChessGame() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        chessGameDAO.updateState(newChessGame.getChessGameId(), "BlackTurn");

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/api/chessgames/{chessGameId}", newChessGame.getChessGameId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("pieceDtos.size()", is(32))
                .body("state", is("End"))
                .body("finished", is(true));
    }

    @DisplayName("체스게임의 현재 점수를 계산하는 API 요청")
    @Test
    void testCalculateScores() {
        //given
        ChessGameInfoResponseDto newChessGame = chessGameService.createNewChessGame("title");
        pieceDAO.delete(newChessGame.getChessGameId(), 1, 0);
        pieceDAO.delete(newChessGame.getChessGameId(), 7, 3);

        //when //then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/chessgames/{chessGameId}/scores", newChessGame.getChessGameId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

}