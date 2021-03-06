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

public class SamuBot implements Player {
	int maxDepth = 5;
	private Side side;
	private Evaluator evaluator;
	private Map<Move, AtomicInteger> banned;
	private List<Move> history;
	private OpeningBook openingBook;
	double maxEval= 10000;
	Boolean inOpeningBook = true;
	int turnCount = 0;
	
	private double moveAplha = 0.1;
	private double timeAplha = 0.1;
	private double myMoveEstimate = 40.0;
	private double enemyMoveEstimate = 40.0;
	private double timeEstimate = 500.0;
	private double timeGoal = 3*1000;
	private Boolean enemyTested = false;
	
	int iterationDepth = 3;
	
	int bluePieces;
	int redPieces;
	
	public SamuBot(Random rnd) {
	}
	
	public void start(Engine engine, Side side) {
		this.side = side;
		openingBook = new OpeningBook(engine, side);
		evaluator = new Evaluator(maxEval, engine, side); 
		this.banned = new HashMap(16);
		this.history = new ArrayList();
		
		int maxPiece = engine.getMaxPiece();
		int pieces = maxPiece*(1+maxPiece)/2; //arithmetic sum
		bluePieces = pieces;
		redPieces = pieces;
	}
	
	private void updateTimeEstimate(int timeValue){
		double time = (double)timeValue;
		double newTime = timeAplha*time + (1-timeAplha)*timeEstimate;
		timeEstimate = newTime;
	}
	
	private void updateMyMoveEstimate(int moveCount){
		double moves = (double)moveCount;
		double newMoves = moveAplha*moves + (1-moveAplha)*myMoveEstimate;
		myMoveEstimate = newMoves;
	}
	
	private void updateEnemyMoveEstimate(int moveCount){
		double moves = (double)moveCount;
		double newMoves = moveAplha*moves + (1-moveAplha)*enemyMoveEstimate;
		enemyMoveEstimate = newMoves;
	}

	private void updateDepth(){
		double moves;

		if(iterationDepth%2 == 0)
			moves = myMoveEstimate;
		else
			moves = enemyMoveEstimate;
		System.out.printf("timeEstimate: %f; timeGoal: %f; moves: %f", timeEstimate, timeGoal, moves);
		System.out.println();
		if(timeEstimate > timeGoal && iterationDepth > 2){
			iterationDepth--;
			timeEstimate = timeEstimate/moves;
		}
			
		else if(timeEstimate*moves < timeGoal && iterationDepth < maxDepth){
			iterationDepth++;
			timeEstimate = timeEstimate*moves;
		}
			
		return;
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
		
		int size = this.history.size();
		if ((size >= 6) && (((Move)this.history.get(size-1)).equals(this.history.get(size-5))) && (((Move)this.history.get(size-2)).equals(this.history.get(size-6)))) {
			this.banned.put(this.history.get(size - 4), new AtomicInteger(8));
		}
	}
	
	public Move move(Situation situation, int timeLeft) {
		turnCount++;
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
		int moveCount = moves.size();
		ScoredMove[] scoredMoves = scoreMoves(situation, moves);
		Move max = situation.makePass();

		enemyTested = false;
		for(ScoredMove move: scoredMoves){

			Situation newSituation = situation.copyApply(move.move);
			
			double score = minimax(newSituation, alpha, beta, move.move, 0, false, move.score);
			if(score >= maxEval){
				max = move.move;
				alpha = score;
				break;
			}
			if(score > alpha){
				alpha = score;
				max = move.move;
			}
		}
		
		this.history.add(max);
		
		//Measure time spent on move and adjust depth if necessary
		Long time1 = System.currentTimeMillis();
		int timeSpent = (int)(time1 - time0);
		updateTimeEstimate(timeSpent);
		updateMyMoveEstimate(moveCount);
		System.out.printf("%d; %d; %.2f", iterationDepth, timeSpent, alpha);
		System.out.println();
		updateDepth();
		
		return max;
	}
	

	
	public double minimax(Situation situation, double alpha, double beta, Move move, int depth, boolean maxPlayer, double oldEval){
		depth+=1;
		double score = 0;
		List<Move> legalMoves = situation.legal();
		int moveCount = legalMoves.size();
		
		//ScoredMove[] scoredMoves = scoreMoves(situation, legalMoves);
		if(depth == 1 && !enemyTested){
			updateEnemyMoveEstimate(moveCount);
			enemyTested = true;
		}
		if(moveCount == 0){
			if(maxPlayer){
				return -maxEval;
			}else
				return maxEval;
		}

		//for (ScoredMove newMove: scoredMoves) {
		for (Move newMove: legalMoves) {
			//if (this.banned.containsKey(newMove.move)) {
			if (this.banned.containsKey(newMove)) {
				continue;
			}			
			
			Situation newSituation = situation.copyApply(newMove);
			
			if (depth == iterationDepth){
				//score = evaluator.evaluate(newSituation, newMove.move);
				score = evaluator.evaluate(newSituation, newMove);

			}
			else {
				//score = minimax(newSituation, alpha, beta, newMove.move, depth, !maxPlayer, score);
				score = minimax(newSituation, alpha, beta, newMove, depth, !maxPlayer, score);
				
			}
			if(maxPlayer){
				if(score >= maxEval){
					return maxEval;
				}
				if(score > alpha)
					alpha = score;
				if(alpha > beta){
					return beta;
				}
			}else{
				if(score <= -maxEval)
					return -maxEval;
				if(score < beta)
					beta = score;
				if(alpha > beta){
					return alpha;
				}
			}
		}
		return score;
	}
	
	private ScoredMove[] scoreMoves(Situation situation, List<Move> moves){
		ScoredMove[] scoredMoves = new ScoredMove[moves.size()];
		int totMoves = 0;
		for (Move evaluate: moves) {
			Situation applied = situation.copy();
			applied.apply(evaluate);
			int moveScore = (int)evaluator.evaluate(applied, evaluate);
			scoredMoves[totMoves++] = new ScoredMove(evaluate, moveScore);
		}
		Arrays.sort(scoredMoves);
		return scoredMoves;
	}
	
	private class ScoredMove
		implements Comparable<ScoredMove> {
		public Move move;
		public int score;
		
		public ScoredMove(Move move, int score) {
			this.move = move;
			this.score = score;
		}
	
		public int compareTo(ScoredMove compare) {
			return compare.score - this.score;
		}
	}
}