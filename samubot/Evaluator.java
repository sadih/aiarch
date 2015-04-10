package samubot;

import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Piece;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class Evaluator {
	double maxEval;
	
	// Evaluation multipliers
	double ranksX = 1;
	double attackersX = 1;
	double firepowerX = 0.2;
	double attackX = 0.5;
	double exposedX = 0.25;
	double kingPositionX = -1;
	double enemyMovesX = 0;
	
	//Other
	int kingValue = 100;
	
	//Game info
	int h;
	int w;
	int maxPiece;
	Side ownSide;
	Side opponentSide;

	public Evaluator(double value, Engine engine, Side side) {
		maxEval = value;
		h = engine.getBoardHeight();
		w = engine.getBoardWidth();
		maxPiece = engine.getMaxPiece();
		ownSide = side;
		if (ownSide == Side.BLUE)
			opponentSide = Side.RED;
		else {
			opponentSide = Side.BLUE;
		}
	}


	public double evaluate(Situation situation, Move move) {
		double result = 0;
		if (situation.isFinished()) {
			if (situation.getWinner().equals(ownSide)){
				return(maxEval);
			}
			else if (situation.getWinner().equals(opponentSide)) {
				return(-maxEval);
			}
		}
		result+=evalSide(situation, move);
		return result;
	}
	private double evalSide(Situation situation, Move move){
		// Evaluation values
		double result = 0;		
		Board board = situation.getBoard();
		
		double score;
		//Go through own pieces
		Iterable<Board.Square> pieces = board.pieces(ownSide);
		for (Board.Square square : pieces){
			score = evalPiece(situation, ownSide, board, square.getX(), square.getY());//, lastPiece);
			result+= score;
		}
		
		//Go through enemy pieces
		int count = 0;
		pieces = board.pieces(opponentSide);
		for (Board.Square square : pieces){
			count++;
			score = evalPiece(situation, opponentSide, board, square.getX(), square.getY());//, null);
			result+= score;
		}
		
		//If only one enemy piece, try to limit its moves
		if(count <= 1 && situation.getTurn().equals(opponentSide))
			result+= situation.legal(opponentSide).size()*enemyMovesX;
		
		return(result);
	}
	
	private double evalPiece(Situation situation, Side currentSide, Board board, int x, int y){//, Board.Square lastPiece){
		// Evaluation values
		double ranks = 0;
		double attackers = 0;
		double firepower = 0;
		
		double exposed = 0;
		double kingPosition = 0;
		
		int value = board.get(x, y).getValue();
		
		double temp = 0;
		
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
		
		
		double result = 0;
		result+=ranks*ranksX;
		result+=attackers*attackersX;
		result+=firepower*firepowerX;
		result+=exposed*exposedX;
		result+=kingPosition*kingPositionX;
		
		if(currentSide.equals(ownSide))
			return result;
		else
			return -result;
		
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
