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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Evaluator {
	double maxEval;
	
	// Evaluation multipliers
//	double gameWonX;
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
	Side opponentSide;

	public Evaluator(double value, Engine engine, Side side) {
		maxEval = value;
//		gameWonX = maxEval;
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
//		this.ownSide = ownSide;
//		Side opponentSide;
//		if (ownSide == Side.BLUE)
//			opponentSide = Side.RED;
//		else {
//			opponentSide = Side.BLUE;
//		}
		if (situation.isFinished()) {
//			System.out.println(move);
			if (situation.getWinner().equals(ownSide)){
//				System.out.println("winning");
				return(maxEval);
			}
			else if (situation.getWinner().equals(opponentSide)) {
//				System.out.print(ownSide);
//				System.out.print(", ");
//				System.out.println(opponentSide);
				return(-maxEval);
			}
		}
		result+=evalSide(situation, move);
//		result-=evalSide(situation, move, opponentSide);
//		result+=evalSide(situation, move, ownSide);
		return result;
	}
	private double evalSide(Situation situation, Move move){
		// Evaluation values
		double result = 0;
		
//		double ranks = 0;
//		double attackers = 0;
//		double firepower = 0;
//		
//		double exposed = 0;
//		double kingPosition = 0;
//		int distanceToKing = 0;
		
		double attack = 0;
		
//		double temp = 0;
		
		Board board = situation.getBoard();
		
		//Determine if opponent has only one piece left
		int pieceCount = 0;
		Iterable<Board.Square> enemyPieces = board.pieces(ownSide.opposite());
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
		
		double score;
		//Go through own pieces
		Iterable<Board.Square> pieces = board.pieces(ownSide);
		for (Board.Square square : pieces){
			score = evalPiece(situation, ownSide, board, square.getX(), square.getY(), lastPiece);
			result+= score;
		}
		
		//Go through enemy pieces
		pieces = board.pieces(ownSide.opposite());
		for (Board.Square square : pieces){
			score = evalPiece(situation, ownSide.opposite(), board, square.getX(), square.getY(), null);
			result+= score;
		}
		
//		//Value of a piece under attack
//		if(move.getType() == MoveType.ATTACK && move.getPlayer().equals(ownSide)){
//			double value = move.getTarget().getValue();
//			if(value==maxPiece)
//				value = kingValue;
//			attack+= value;
//		}
//		
//		
//		result+= attack*attackX;
		
		return(result);
	}
	
	private double evalPiece(Situation situation, Side currentSide, Board board, int x, int y, Board.Square lastPiece){
		// Evaluation values
		double ranks = 0;
		double attackers = 0;
		double firepower = 0;
		
		double exposed = 0;
		double kingPosition = 0;
		int distanceToKing = 0;
		
		
//		int x = square.getX();
//		int y = square.getY();
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
		
		//If only one enemy piece left
		if(lastPiece != null)
			//Pieces proximity to the enemy's piece
			distanceToKing = w-Math.abs(lastPiece.getX()-x)+h-Math.abs(lastPiece.getY()-y);
		
		double result = 0;
		result+=ranks*ranksX;
		result+=attackers*attackersX;
		result+=firepower*firepowerX;
		result+=exposed*exposedX;
		result+=kingPosition*kingPositionX;
		result+=distanceToKing*distanceToKingX;
		
		if(currentSide.equals(ownSide))
			return result;
		else
			return -result;
		
	}
	
	public double evalDelta(Situation situation, Move move, Situation newSituation) {
		Board board = situation.getBoard();
		Board newBoard = newSituation.getBoard();
		double totalChange = 0;
		Piece piece = move.getPiece();
		if(piece == null) //pass
			return 0.0;
		Coord from = move.getFrom();
		int x0 = from.getX();
		int y0 = from.getY();
		Coord to = move.getTo();
		int x1 = to.getX();
		int y1 = to.getY();
		
		HashSet<Integer> tested = new HashSet<Integer>();
		
		int[] xs = {x0, x0-1, x0+1, x0,   x0,   x1, x1-1, x1+1, x1,   x1};
		int[] ys = {y0, y0,   y0,   y0-1, y0+1, y1, y1,   y1,   y1-1, y1+1};
		
		int x, y;
		double oldScore, newScore;
		Side side;
		
		
		for(int i=0;i<10;i++){
			try{
				x = xs[i];
				y = ys[i];
				if(tested.contains(10*x+y))
					continue;
				side = board.get(x,y).getSide();
				oldScore = evalPiece(situation, side, board, x, y, null);
				newScore = evalPiece(newSituation, side, newBoard, x, y, null);
				totalChange+= newScore-oldScore;
				tested.add(10*x+y);
			}catch(Exception e){
	
			}
		}
		return totalChange;
		
		
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
