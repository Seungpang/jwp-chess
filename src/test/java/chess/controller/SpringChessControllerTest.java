package chess.controller;

import chess.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SpringChessControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("방 목록 전체조회 테스트")
    void showRoomList() throws Exception{
        mockMvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("방 아이디로 보드조회 테스트")
    void loadSaveBoard() throws Exception {
        mockMvc.perform(get("/3/board")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("gameOverFlag").value(false));
    }

    @Test
    @DisplayName("방 생성 테스트")
    void makeRoom() throws Exception {
        String content = objectMapper.writeValueAsString(new RoomNameDto("room"));
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("방 생성 테스트 - 중복이름일 때")
    void makeSameRoomException() throws Exception {
        String content = objectMapper.writeValueAsString(new RoomNameDto("room_one"));
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("방 생성 테스트 - 빈 이름일 때")
    void makeNullRoomException() throws Exception {
        String content = objectMapper.writeValueAsString(new RoomNameDto(""));
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("방 생성 테스트 - 13자 초과일 때")
    void makeLongRoomException() throws Exception {
        String content = objectMapper.writeValueAsString(new RoomNameDto("aaaaaaaaaaaaa"));
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("방 초기화 테스트")
    void resetBoard() throws Exception {
        mockMvc.perform(get("/3/reset")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("점수 조회 테스트")
    void scoreStatus() throws Exception {
        mockMvc.perform(get("/3/score")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("whiteScore").value("38.0"))
                .andExpect(jsonPath("blackScore").value("38.0"));
    }

    @Test
    @DisplayName("기물 이동 테스트")
    void move() throws Exception {
        String content = objectMapper.writeValueAsString(new MoveInfoDto("a2", "a4"));
        mockMvc.perform(post("/3/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("기물 이동실패 테스트")
    void moveFail() throws Exception {
        String content = objectMapper.writeValueAsString(new MoveInfoDto("a2", "b2"));
        mockMvc.perform(post("/3/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }
}