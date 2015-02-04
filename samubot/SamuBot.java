package samubot;

import java.util.List;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class SamuBot implements Player {
	private boolean max;
	private Object side;
	private Random rnd;

	public SamuBot(Random rnd) {
		this.rnd = rnd;
	}
	
	public void start(Engine engine, Side side) {
		if (engine.getStarter().equals(side)) {
			this.max = true;
		} else {
			this.max = false;
		}
		this.side = side;
	}
	
	public Move move(Situation situation, int timeLeft) {
		List<Move> moves = situation.legal();
		
		
		
		return moves.get(rnd.nextInt(moves.size()));
	}
	
	public int evaluate() {
		
		return 0;
	}
	
	

	public int maxi( int depth ) {
		if ( depth == 0 ) return evaluate();
		int max = -10000;
		for ( all moves) {
			score = mini( depth - 1 );
			if( score > max )
				max = score;
		}
		return max;
	}
	
	public  int mini( int depth ) {
	    if ( depth == 0 ) return -evaluate();
	    int min = 10000;
	    for ( all moves) {
	        score = maxi( depth - 1 );
	        if( score < min )
	            min = score;
	    }
	    return min;
	}
	

}