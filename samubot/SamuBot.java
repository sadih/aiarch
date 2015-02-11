package samubot;

import java.util.List;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class SamuBot implements Player {
//	private boolean max;
	static int maxDepth = 3;
	private Side side;
	private Random rnd;
	private Evaluator evaluator;
	double maxEval= 10000;
	
	public SamuBot(Random rnd) {
		this.rnd = rnd;
		evaluator = new Evaluator(maxEval); 
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
		double aplha = -maxEval;
		double beta = maxEval;
		List<Move> moves = situation.legal();
		Move max = situation.makePass();
		double maxValue = -maxEval;
		for(Move move: moves){
			Situation newSituation = situation.copy();
			newSituation.apply(move);
//			double value = mini(newSituation, move, 0);
			double value = minimax(newSituation, aplha, beta, move, 0, false);
			if(value > maxValue){
				maxValue = value;
				max = move;
			}
		}
//		System.out.println(maxValue);
		return max;

//		return moves.get(rnd.nextInt(moves.size()));
	}
	
//	public int evaluate() {
//		
//		return 0;
//	}
	
	public double minimax(Situation situation, double alpha, double beta, Move move, int depth, boolean maxPlayer){
		depth+=1;
		double score = 0;
		List<Move> legalMoves = situation.legal();
//		if ( depth == maxDepth ) 
//			return evaluator.evaluate(situation, move, side, legalMoves);
//		double max = -maxEval;
//		double min = maxEval;
		for (Move newMove: legalMoves) {
			Situation newSituation = situation.copy();
			newSituation.apply(newMove);
			if (depth == maxDepth){
				score = evaluator.evaluate(newSituation, move, side, legalMoves);
				System.out.println(score);
			}
			else
				score = minimax(newSituation, alpha, beta, newMove, depth, !maxPlayer);
			if(maxPlayer){
				if(score > alpha)
					alpha = score;
				if(alpha > beta)
					return beta;
			}else{
				if(score < beta)
					beta = score;
				if(alpha > beta)
					return alpha;
			}
//				
//			if( score > max )
//				max = score;
//			if( score < min )
//				min = score;
		}
		return score;
	}
	
	

//	public double maxi(Situation situation, Move move, int depth) {
//		System.out.println(depth);
//		double score;
//		List<Move> legalMoves = situation.legal();
//		if ( depth == maxDepth ) 
//			return evaluator.evaluate(situation, move, side, legalMoves);
//		double max = -maxEval;
//		for (Move newMove: legalMoves) {
//			Situation newSituation = situation.copy();
//			newSituation.apply(newMove);
//			score = mini(newSituation, newMove, depth + 1 );
//			if( score > max )
//				max = score;
//		}
//		return max;
//	}
//	
//	public double mini(Situation situation, Move move, int depth) {
//		System.out.println(depth);
//		double score;
//		List<Move> legalMoves = situation.legal();
//	    if ( depth == maxDepth ) 
//	    	return -evaluator.evaluate(situation, move, side, legalMoves);
//	    double min = maxEval;
//	    for (Move newMove: legalMoves) {
//	    	Situation newSituation = situation.copy();
//			newSituation.apply(newMove);
//	        score = maxi(newSituation, newMove, depth + 1 );
//	        if( score < min )
//	            min = score;
//	    }
//	    return min;
//	}
//	

}