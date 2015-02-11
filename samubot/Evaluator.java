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
	
	//
//	double currentEval = 0;
//	double lastEval = 0;
//	int enemyPieces;
//	int lastPieceX = -1;
//	int lastPieceY = -1;

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
//		lastEval = currentEval;
//		currentEval = result;
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
		
		
		Iterable<Board.Square> pieces = board.pieces(currentSide);
		for (Board.Square square : pieces){
			
			int x = square.getX();
			int y = square.getY();
			int value = board.get(x, y).getValue();
			ranks+=value;
			if(!board.owner(x, y).equals(currentSide)){
				attackers+=1;
				if(!situation.isBlocked(x, y)){
					temp=board.firepower(currentSide, x, y);
					if(temp>0)
						firepower+=temp;
				}
				exposed = exposure(currentSide, board, x, y, value);
				
					
			}
			if(value == maxPiece)
				kingPosition = distanceFromHome(currentSide, x,y);
//			if(currentSide.equals(ownSide) && lastPieceX > -1){
//				System.out.print("Finish him! ");
//				System.out.println(board.get(lastPieceX, lastPieceY).getValue());
//				distanceToKing = Math.abs(lastPieceX-x)+Math.abs(lastPieceY-y);
//			}
			if(lastPiece != null)
				distanceToKing = w-Math.abs(lastPiece.getX()-x)+h-Math.abs(lastPiece.getY()-y);
		}
		
		
		
//		if(!currentSide.equals(ownSide) && pieceCount == 1){
//			lastPieceX = x;
//			lastPieceY = y;
//		}else{
//			lastPieceX = -1;
//			lastPieceY = -1;
//		}
		
		
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
	//				System.out.println(kingValue);
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
