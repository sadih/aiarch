package samubot;

import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class OpeningBook {
	private static final int[][] book = {{4,1,4,2},{3,1,5,1},{2,1,4,1}};
	
	int h;
	int w;
	int maxPiece;
	Side side;
	
	int movesMade = 0;
	Boolean isLeft = true;
	
	public OpeningBook(Engine engine, Side side) {
		this.h = engine.getBoardHeight();
		this.w = engine.getBoardWidth();
		this.side = side;
		this.maxPiece = engine.getMaxPiece();
	}
	
	public Move getMove(Situation situation){
		Move result;
		
		if ((h != 9) || (w != 9) || (maxPiece != 6))
			return null;
		
		if(movesMade >= book.length)
			return null;
		
		if(movesMade == 0)
			setDirection(situation);
		
		
		int x0, y0, x1, y1;
		if (isLeft){
			x0 = book[movesMade][0];
			y0 = book[movesMade][1];
			x1 = book[movesMade][2];
			y1 = book[movesMade][3];
		}else{
			x0 = book[movesMade][1];
			y0 = book[movesMade][0];
			x1 = book[movesMade][3];
			y1 = book[movesMade][2];
		}
		if(side == Side.BLUE)
			result = situation.makeMove(x0, y0, x1, y1);
		else
			result = situation.makeMove(8-x0, 8-y0, 8-x1, 8-y1);
		
		movesMade++;
		return result;
	}
	
	private void setDirection(Situation situation){
		Move move = situation.getPreviousMove();
		if(move == null)
			return;
		Coord coord = move.getTo();
		int x = coord.getX();
		int y = coord.getY();
		if(side == Side.BLUE)
			isLeft = (x >= y);
		else
			isLeft = (x <= y);
	}
}
