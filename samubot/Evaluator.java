package samubot;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Board.Square;
import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Engine;
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
	double attackersX = 1;
	double firepowerX = 0.2;
	double attackX = 0.5;
	double exposedX = 0.25;
	double kingPositionX = -1;
	double distanceToKingX = 1;
	
	//Other
	int kingValue = 100;
	
	//Game info
	int h;
	int w;
	int maxPiece;
	Side ownSide;

	public Evaluator(double value, Engine engine) {
		maxEval = value;
		gameWonX = maxEval;
		h = engine.getBoardHeight();
		w = engine.getBoardWidth();
		maxPiece = engine.getMaxPiece();
	}


	public double evaluate(Situation situation, Move move, Side ownSide, List<Move> legalMoves) {
		double result = 0;
		this.ownSide = ownSide;
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
		result-=evalSide(situation, move, opponentSide);
		result+=evalSide(situation, move, ownSide);
		return result;
	}
	private double evalSide(Situation situation, Move move, Side currentSide){
		// Evaluation values
		double result = 0;
		
		double ranks = 0;
		double attackers = 0;
		double firepower = 0;
		double attack = 0;
		double exposed = 0;
		double kingPosition = 0;
		int distanceToKing = 0;
		
		double temp = 0;
		
		Board board = situation.getBoard();
		
		//Determine if opponent has only one piece left
		int pieceCount = 0;
		Iterable<Board.Square> enemyPieces = board.pieces(currentSide.opposite());
		Board.Square lastPiece = null;
		Board.Square currentPiece = null;
		for (Board.Square square : enemyPieces){
			if(pieceCount > 1)
				break;
			pieceCount++;
			currentPiece = square;
		}
		if(pieceCount == 1 && currentPiece != null){
			lastPiece = currentPiece;
		}
		
		//Go through own pieces
		Iterable<Board.Square> pieces = board.pieces(currentSide);
		for (Board.Square square : pieces){
			
			int x = square.getX();
			int y = square.getY();
			int value = board.get(x, y).getValue();
			
			//Sum up piece ranks
			ranks+=value;
			if(!board.owner(x, y).equals(currentSide)){ //not in own starting area
				//Pieces capable of attacking
				attackers+=1;
				if(!situation.isBlocked(x, y)){
					//Combined attack distance
					temp=board.firepower(currentSide, x, y);
					if(temp>0)
						firepower+=temp;
				}
				//If piece is open to attacking side
				exposed = exposure(currentSide, board, x, y, value);
				
					
			}
			//Distance of the highest ranking piece from own corner
			if(value == maxPiece)
				kingPosition = distanceFromHome(currentSide, x,y);
			
			//If only one enemy piece left
			if(lastPiece != null)
				//Pieces proximity to the enemy's piece
				distanceToKing = w-Math.abs(lastPiece.getX()-x)+h-Math.abs(lastPiece.getY()-y);
		}
		
		//Value of a piece under attack
		if(move.getType() == MoveType.ATTACK){
			double value = move.getTarget().getValue();
			if(value==maxPiece)
				value = kingValue;
			attack+= value;
		}
		
		
		result+= attack*attackX;
		
		result+=ranks*ranksX;
		result+=attackers*attackersX;
		result+=firepower*firepowerX;
		result+=exposed*exposedX;
		result+=kingPosition*kingPositionX;
		result+=distanceToKing*distanceToKingX;
		
		return(result);
	}
	
	private int distanceFromHome(Side currentSide, int x, int y){
		if (currentSide == Side.BLUE)
			return(x+y);
		else
			return(h+w-x-y);
	}
	
	private int exposure(Side currentSide, Board board, int x, int y, int value){
		int exposed = 0;
		int delta;
		if (currentSide == Side.BLUE)
			delta=1;
		else
			delta=-1;
		try{
			Piece next = board.get(x+delta, y);
			if(next != null){
				if(value==6){
					value = kingValue;
				}
				if(!next.getSide().equals(currentSide))
					exposed-=value;
				next = board.get(x, y+delta);
				if(!next.getSide().equals(currentSide))
					exposed-=value;
			}
		}catch(Exception e){
			
		}
		return(exposed);
	}
}
