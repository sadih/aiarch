package samubot;

import java.util.List;
import java.util.Random;

import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class SamuBot implements Player {
	static int maxDepth = 3;
	private Side side;
	private Random rnd;
	private Evaluator evaluator;
	private OpeningBook openingBook;
	double maxEval= 10000;
	Boolean inOpeningBook = true;
	
	public SamuBot(Random rnd) {
		this.rnd = rnd;
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
		openingBook = new OpeningBook(engine, side);
		evaluator = new Evaluator(maxEval, engine); 
	}
	
	public Move move(Situation situation, int timeLeft) {
		
		//Take a move from opening book if one exists
		if(inOpeningBook){
			Move move = openingBook.getMove(situation);
			if (move != null){
				System.out.println("In opening book");
				return move;
			}
			else
				inOpeningBook = false; //If no move is found, never check the opening book again
		}
		
		//Select move with miniMax
		double alpha = -maxEval-1;
		double beta = maxEval+1;
		List<Move> moves = situation.legal();
		Move max = situation.makePass();
		double maxValue = -maxEval;
		for(Move move: moves){
			Situation newSituation = situation.copy();
			newSituation.apply(move);
			double score = minimax(newSituation, alpha, beta, move, 0, false);
			if(score > alpha){
				alpha = score;
				max = move;
			}
		}
		System.out.println(alpha);
		return max;

	}
	
	public double minimax(Situation situation, double alpha, double beta, Move move, int depth, boolean maxPlayer){
		depth+=1;
		double score = 0;
		List<Move> legalMoves = situation.legal();

		for (Move newMove: legalMoves) {
			Situation newSituation = situation.copyApply(newMove);
			if (depth == maxDepth){
				score = evaluator.evaluate(newSituation, move, side, legalMoves);
			}
			else
				score = minimax(newSituation, alpha, beta, newMove, depth, !maxPlayer);
			if(maxPlayer){
				if(score == maxEval)
					return maxEval;
				if(score > alpha)
					alpha = score;
				if(alpha > beta){
//					System.out.println("Beta cut-off");
					return beta;
				}
			}else{
				if(score == -maxEval)
					return -maxEval;
				if(score < beta)
					beta = score;
				if(alpha > beta){
//					System.out.println("Aplha cut-off");
					return alpha;
				}
			}
		}
		return score;
	}
}