package samubot;

import java.util.ArrayList;
import java.util.Arrays;
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
import fi.zem.aiarch.game.hierarchy.Board;
import fi.zem.aiarch.game.hierarchy.Coord;
import fi.zem.aiarch.game.hierarchy.Piece;

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
	
	public SamuBot(Random rnd) {
		this.rnd = rnd;
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
		openingBook = new OpeningBook(engine, side);
		evaluator = new Evaluator(maxEval, engine); 
		this.banned = new HashMap(16);
		this.history = new ArrayList();
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
		
		
		
		
		for (Move move: moves){
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
		System.out.println("Jes. "+ tot);
		System.out.println(alpha);
		
		this.history.add(max);
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
	
	private ScoredMove[] scoreMoves(Situation situation, List<Move> moves){
		ScoredMove[] scoredMoves = new ScoredMove[moves.size()];
		Iterator<Move> iterator = moves.iterator();
		int totMoves = 0;
		while (iterator.hasNext()) {
			Move evaluate = (Move)iterator.next();
			Situation applied = situation.copy();
			applied.apply(evaluate);
			int moveScore = evaluator.scoreMove(situation, applied, evaluate);
			//int moveScore = (int)evaluator.evaluate(applied, evaluate, side, moves);
			scoredMoves[totMoves++] = new ScoredMove(evaluate, moveScore, applied);
		}
		
		Arrays.sort(scoredMoves);
		return scoredMoves;
		
	}
	
	public double minimax(Situation situation, double alpha, double beta, Move move, int depth, boolean maxPlayer){
		depth+=1;
		double score = 0;
		List<Move> legalMoves = situation.legal();
		ScoredMove[] scoredMoves = scoreMoves(situation, legalMoves);

		
		
		for (ScoredMove newMove: scoredMoves) {
			if (this.banned.containsKey(newMove.move)) {
				System.out.println("BANNED MOVE FOUND!");
				continue;
			}
			Situation newSituation = situation.copyApply(newMove.move);
			if (depth == maxDepth){
				score = evaluator.evaluate(newSituation, move, side, legalMoves);
			}
			else
				score = minimax(newSituation, alpha, beta, newMove.move, depth, !maxPlayer);
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
	
	private class ScoredMove
		implements Comparable<ScoredMove> {
		public Move move;
		public int score;
		public Situation situation;
		
		public ScoredMove(Move move, int score, Situation situation) {
			this.move = move;
			this.score = score;
			this.situation = situation;
		}

		public int compareTo(ScoredMove compare) {
			return compare.score - this.score;
		}
	}
}