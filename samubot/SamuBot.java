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
	static int maxDepth = 6;
	private Side side;
	private Random rnd;
	private Evaluator evaluator = new Evaluator(); 

	public SamuBot(Random rnd) {
		this.rnd = rnd;
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
//		if (engine.getStarter().equals(side)) {
//			this.max = true;
//		} else {
//			this.max = false;
//		}
//		this.side = side;
	}
	
	public Move move(Situation situation, int timeLeft) {
//		int depth = 6;
		List<Move> moves = situation.legal();
		Move max = situation.makePass();
		double maxValue = -10000.0;
		for(Move move: moves){
			Situation newSituation = situation.copy();
			newSituation.apply(move);
			double value = mini(newSituation, maxDepth);
			if(value > maxValue){
				maxValue = value;
				max = move;
			}
		}
		return max;

//		return moves.get(rnd.nextInt(moves.size()));
	}
	
//	public int evaluate() {
//		
//		return 0;
//	}
	
	

	public double maxi(Situation situation, int depth) {
		double score;
		List<Move> legalMoves = situation.legal();
		if ( depth == maxDepth ) return evaluator.evaluate(situation, side, legalMoves);
		double max = -10000;
		for (Move move: legalMoves) {
			Situation newSituation = situation.copy();
			newSituation.apply(move);
			score = mini(newSituation, depth + 1 );
			if( score > max )
				max = score;
		}
		return max;
	}
	
	public double mini(Situation situation, int depth) {
		double score;
		List<Move> legalMoves = situation.legal();
	    if ( depth == maxDepth ) return -evaluator.evaluate(situation, side, legalMoves);
	    double min = 10000;
	    for (Move move: legalMoves) {
	    	Situation newSituation = situation.copy();
			newSituation.apply(move);
	        score = maxi(newSituation, depth + 1 );
	        if( score < min )
	            min = score;
	    }
	    return min;
	}
	

}