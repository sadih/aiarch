package samubot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import fi.zem.aiarch.game.hierarchy.Engine;
import fi.zem.aiarch.game.hierarchy.Move;
import fi.zem.aiarch.game.hierarchy.Player;
import fi.zem.aiarch.game.hierarchy.Side;
import fi.zem.aiarch.game.hierarchy.Situation;

public class SamuBot implements Player {
	int maxDepth = 3;
	private Side side;
	private Random rnd;
	private Evaluator evaluator;
	private Map<Move, AtomicInteger> banned;
	private List<Move> history;
	private OpeningBook openingBook;
	double maxEval= 10000;
	Boolean inOpeningBook = true;
	
	int bluePieces;
	int redPieces;
	
	public SamuBot(Random rnd) {
		this.rnd = rnd;
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
		openingBook = new OpeningBook(engine, side);
		evaluator = new Evaluator(maxEval, engine); 
		this.banned = new HashMap(16);
		this.history = new ArrayList();
		
		int maxPiece = engine.getMaxPiece();
		int pieces = maxPiece*(1+maxPiece)/2; //arithmetic sum
		bluePieces = pieces;
		redPieces = pieces;
	}
	
	public Move move(Situation situation, int timeLeft) {
		Long time0 = System.currentTimeMillis();
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

		// Banned double moves
		checkBannedMoves(situation);

		//Select move with miniMax
		double alpha = -maxEval-1;
		double beta = maxEval+1;
		List<Move> moves = situation.legal();
		Move max = situation.makePass();
		double maxValue = -maxEval;
		int tot = 0;
		if (moves.size() < 60 && moves.size() > 1) {
			maxDepth = 3;
		} else {
			maxDepth = 3;
		}
		for(Move move: moves){
			tot += 1;
			// System.out.println(tot);
			Situation newSituation = situation.copy();
			newSituation.apply(move);
			double score = minimax(newSituation, alpha, beta, move, 0, false);
			if(score > alpha){
				alpha = score;
				max = move;
			}
		}
//		System.out.println("Jes. "+ tot);
//		System.out.println(alpha);
		
		this.history.add(max);
		
		Long time1 = System.currentTimeMillis();
		System.out.print(time1 - time0);
		System.out.print("; ");
		System.out.println(alpha);
		
		return max;
	}
	
	private void checkBannedMoves(Situation situation) {
		Iterator<Move> iterator = this.banned.keySet().iterator();
		Object unbanMove = null;
		
		while(iterator.hasNext()) {
			Move bannedMove = (Move)iterator.next();
			int turnsLeft = ((AtomicInteger)this.banned.get(bannedMove)).decrementAndGet();
			if (turnsLeft == 0) {
				unbanMove = bannedMove;
			}
		}
		
		if (unbanMove != null) {
			this.banned.remove(unbanMove);
		}
		
		// Add the opponents move to the history list
		if (situation.getPreviousMove() != null) {
			this.history.add(situation.getPreviousMove());
		}
		
		int i = this.history.size();
		if ((i >= 6) && (((Move)this.history.get(i-1)).equals(this.history.get(i-5))) && (((Move)this.history.get(i-2)).equals(this.history.get(i-6)))) {
			this.banned.put(this.history.get(i - 4), new AtomicInteger(8));
		}
	}
	
	public double minimax(Situation situation, double alpha, double beta, Move move, int depth, boolean maxPlayer){
		depth+=1;
		double score = 0;
		List<Move> legalMoves = situation.legal();

		for (Move newMove: legalMoves) {
			if (this.banned.containsKey(newMove)) {
//				System.out.println("BANNED MOVE FOUND!");
				continue;
			}
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