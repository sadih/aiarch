package samubot;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Board.Square;
import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.MoveType;
import fi.zem.aiarch.game.hierarchy.Piece;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;
import java.util.Iterator;
import java.util.List;

public class Evaluator {
	double maxValue = 10000.0;

	// Evaluation variables
	int gameWon = 0;
	
	// Evaluation multipliers
	double gameWonX = maxValue;


	public double evaluate(Situation situation, Side mySide, List<Move> legalMoves) {
		double result = 0;
		
		Side ownSide = mySide;
		Side opponentSide;
		if (ownSide == Side.BLUE)
			opponentSide = Side.RED;
		else {
			opponentSide = Side.BLUE;
		}
		int gameWon = 0;
		if (situation.isFinished()) {
			if (situation.getWinner().equals(ownSide))
				gameWon = 1;
			else if (situation.getWinner().equals(opponentSide)) {
				gameWon = -1;
			}
		}
		result += gameWon*gameWonX;
		
		return(result);
	}
}