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
	double maxEval;
	
	// Evaluation multipliers
	double gameWonX;
	double ranksX = 1;
	double attackersX = 0.2;
	double firepowerX = 0.5;
	double attackX = 0.5;


	public Evaluator(double value) {
		maxEval = value;
		gameWonX = maxEval;
	}


	public double evaluate(Situation situation, Move move, Side ownSide, List<Move> legalMoves) {
		double result = 0;

		Side opponentSide;
		if (ownSide == Side.BLUE)
			opponentSide = Side.RED;
		else {
			opponentSide = Side.BLUE;
		}
		if (situation.isFinished()) {
			if (situation.getWinner().equals(ownSide))
				return(maxEval);
			else if (situation.getWinner().equals(opponentSide)) {
				return(-maxEval);
			}
		}
		result+=evalSide(situation, move, ownSide);
		result-=evalSide(situation, move, opponentSide);
		return result;
	}
	private double evalSide(Situation situation, Move move, Side ownSide){
		// Evaluation values
		double result = 0;
		
		double ranks = 0;
		double attackers = 0;
		double firepower = 0;
		double attack = 0;
		
		Board board = situation.getBoard();
		Iterable<Board.Square> pieces = board.pieces(ownSide);
		for (Board.Square square : pieces){
			int x = square.getX();
			int y = square.getY();
			ranks+=board.get(x, y).getValue();
			if(!board.owner(x, y).equals(ownSide)){
				attackers+=1;
				if(!situation.isBlocked(x, y))
					firepower+=board.firepower(ownSide, x, y);
			}
		}
		if(move.getType() == MoveType.ATTACK){
			attack+= move.getTarget().getValue();
		}
		result+= attack*attackX;
		
		result+=ranks*ranksX;
		result+=attackers*attackersX;
		result+=firepower*firepowerX;
		
		return(result);
	}
}
