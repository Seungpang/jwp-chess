package chess.chessgame.domain.piece;

import chess.chessgame.domain.piece.attribute.Color;
import chess.chessgame.domain.piece.attribute.Notation;
import chess.chessgame.domain.piece.attribute.Score;
import chess.chessgame.domain.piece.strategy.BishopMoveStrategy;

public class Bishop extends Piece {
    private static final Score BISHOP_SCORE = new Score(3);
    private static final Notation BISHOP_NOTATION = new Notation("B");

    public Bishop(Color color) {
        super(color, BISHOP_NOTATION, new BishopMoveStrategy(), BISHOP_SCORE);
    }
}