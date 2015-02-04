/*     */ package wernerhenke;
/*     */ 
/*     */ import fi.zem.aiarch.game.hierarchy.Coord;
/*     */ import fi.zem.aiarch.game.hierarchy.Engine;
/*     */ import fi.zem.aiarch.game.hierarchy.Move;
/*     */ import fi.zem.aiarch.game.hierarchy.Player;
/*     */ import fi.zem.aiarch.game.hierarchy.Side;
/*     */ import fi.zem.aiarch.game.hierarchy.Situation;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class WernerHenke
/*     */   implements Player
/*     */ {
/*  12 */   private boolean fixedStartMove = true;
/*     */ 
/*  14 */   private boolean randomStartMove = false;
/*     */ 
/*  16 */   private int searchDepth = 6;
/*     */   static final int maxValue = 100000;
/*     */   static final String myTurnString = "W3";
/*  20 */   private Side mySide = null;
/*     */   private Random rnd;
/*     */   private Evaluator evaluator;
/*     */   private Engine engine;
/*  27 */   private int moveNum = 0;
/*     */   private HashMap<Integer, Move> killerMoves1;
/*     */   private String toStringString;
/*     */   private MoveValueEvaluator moveEvaluator;
/*     */ 
/*     */   public String toString()
/*     */   {
/*  36 */     return this.toStringString;
/*     */   }
/*     */ 
/*     */   public WernerHenke(Random rnd) {
/*  40 */     this.rnd = rnd;
/*  41 */     this.moveEvaluator = new MoveValueEvaluator();
/*  42 */     this.evaluator = new Evaluator(100000);
/*     */   }
/*     */ 
/*     */   public void start(Engine engine, Side side) {
/*  46 */     this.engine = engine;
/*     */ 
/*  48 */     if (this.mySide == null) {
/*  49 */       this.mySide = side;
/*  50 */       this.moveNum = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Move move(Situation situation, int timeLeft)
/*     */   {
/*  58 */     List legalMoves = situation.legal();
/*     */ 
/*  60 */     if (legalMoves.size() == 1) {
/*  61 */       return (Move)legalMoves.get(0);
/*     */     }
/*  63 */     this.killerMoves1 = new HashMap();
/*     */ 
/*  65 */     if (timeLeft < 500) {
/*  66 */       this.searchDepth = 5;
/*  67 */       if (timeLeft < 200) {
/*  68 */         this.searchDepth = 4;
/*     */       }
/*     */     }
/*     */ 
/*  72 */     if ((this.moveNum == 0) && 
/*  73 */       (situation.getTurn() == Side.BLUE)) {
/*  74 */       this.moveNum = -1;
/*     */     }
/*     */ 
/*  77 */     this.moveNum += 2;
/*     */ 
/*  84 */     if ((this.moveNum == 1) && 
/*  85 */       (this.fixedStartMove)) {
/*  86 */       Move temp = null;
/*     */ 
/*  88 */       List moves = situation.legal(2, 3);
/*     */ 
/*  90 */       for (int i = 0; i < moves.size(); i++) {
/*  91 */         if (((Move)moves.get(i)).getTo().equals(3, 3)) {
/*  92 */           temp = (Move)moves.get(i);
/*     */         }
/*     */       }
/*  95 */       if (temp != null) {
/*  96 */         return temp;
/*     */       }
/*     */     }
/*     */ 
/* 100 */     EvaluatedMove evaluatedMove = AlphaBeta(situation.copy(), legalMoves, this.searchDepth, -100000.0F, 100000.0F, null);
/*     */ 
/* 102 */     if ((evaluatedMove != null) && 
/* 103 */       (evaluatedMove.getMove() != null)) {
/* 104 */       if ((this.moveNum == 1) && 
/* 105 */         (this.randomStartMove))
/*     */       {
/* 107 */         int randomInt = this.rnd.nextInt(legalMoves.size());
/*     */ 
/* 109 */         return (Move)legalMoves.get(randomInt);
/*     */       }
/*     */ 
/* 113 */       return evaluatedMove.getMove();
/*     */     }
/*     */ 
/* 117 */     return (Move)legalMoves.get(0);
/*     */   }
/*     */ 
/*     */   private EvaluatedMove AlphaBeta(Situation situation, List<Move> legalMoves, int depth, float alpha, float beta, Move lastMove) {
/* 121 */     int incrementalDepth = this.searchDepth - depth;
/*     */ 
/* 123 */     String indent = "  ";
/* 124 */     for (int i = 0; i < incrementalDepth; i++) {
/* 125 */       indent = indent + "  ";
/*     */     }
/* 127 */     if ((depth <= 0) && (legalMoves.size() > 1)) {
/* 128 */       Float value = Float.valueOf(this.evaluator.Evaluate(situation.copy(), this.mySide, legalMoves));
/* 129 */       if (situation.getTurn() != this.mySide)
/* 130 */         value = Float.valueOf(value.floatValue() * -1.0F);
/* 131 */       return new EvaluatedMove(lastMove, value.floatValue());
/*     */     }
/*     */ 
/* 134 */     EvaluatedMove emBest = new EvaluatedMove(null, -100000.0F);
/*     */ 
/* 136 */     Move killerMove = null;
/* 137 */     boolean killerMoveFound = false;
/*     */ 
/* 140 */     if (this.killerMoves1.containsKey(Integer.valueOf(incrementalDepth))) {
/* 141 */       killerMove = (Move)this.killerMoves1.get(Integer.valueOf(incrementalDepth));
/*     */ 
/* 143 */       for (int i = 0; i < legalMoves.size(); i++) {
/* 144 */         if (((Move)legalMoves.get(i)).equals(killerMove)) {
/* 145 */           legalMoves.remove(i);
/* 146 */           killerMoveFound = true;
/* 147 */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 153 */     if (incrementalDepth < 1)
/*     */     {
/* 156 */       sortMoves(legalMoves, true, true, true, situation.getPreviousMove());
/*     */     }
/* 165 */     else if (depth > 0) {
/* 166 */       sortMoves(legalMoves, true, true, true, null);
/*     */     }
/*     */ 
/* 169 */     if (killerMoveFound) {
/* 170 */       legalMoves.add(0, killerMove);
/*     */     }
/*     */ 
/* 175 */     for (int i = 0; i < legalMoves.size(); i++)
/*     */     {
/* 177 */       Move move = (Move)legalMoves.get(i);
/*     */ 
/* 179 */       Situation newSit = situation.copyApply(move);
/*     */ 
/* 181 */       EvaluatedMove emTemp = new EvaluatedMove(move, -AlphaBeta(newSit, newSit.legal(), depth - 1, -beta, -alpha, move).getValue());
/*     */ 
/* 183 */       if (emTemp.getValue() > emBest.getValue()) {
/* 184 */         emBest = emTemp;
/*     */ 
/* 186 */         if (emBest.getValue() > alpha) {
/* 187 */           alpha = emBest.getValue();
/*     */         }
/*     */ 
/* 190 */         if (emBest.getValue() >= beta)
/*     */         {
/* 192 */           if (this.killerMoves1.containsKey(Integer.valueOf(incrementalDepth))) {
/* 193 */             this.killerMoves1.remove(Integer.valueOf(incrementalDepth));
/*     */           }
/* 195 */           this.killerMoves1.put(Integer.valueOf(incrementalDepth), emBest.getMove());
/*     */ 
/* 197 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 205 */     return emBest;
/*     */   }
/*     */ 
/*     */   private void sortMoves(List<Move> moves, boolean moveLength, boolean attackTargetValue, boolean movementDirection, Move previousMove)
/*     */   {
/* 222 */     this.moveEvaluator.setMoveLengthInUse(moveLength);
/* 223 */     this.moveEvaluator.setAttackTargetValueInUse(attackTargetValue);
/* 224 */     this.moveEvaluator.setMovementDirectionInUse(movementDirection);
/* 225 */     this.moveEvaluator.setPreviousMove(previousMove);
/*     */ 
/* 227 */     quickSort(moves, 0, moves.size() - 1);
/* 228 */     moves = rotateList(moves);
/*     */   }
/*     */ 
/*     */   public void quickSort(List<Move> moves, int start, int end)
/*     */   {
/* 239 */     int i = start;
/* 240 */     int k = end;
/*     */ 
/* 242 */     if (end - start >= 1)
/*     */     {
/* 244 */       int pivot = this.moveEvaluator.valueOfMove((Move)moves.get(start));
/*     */ 
/* 246 */       while (k > i)
/*     */       {
/*     */         do {
/* 249 */           i++;
/*     */ 
/* 248 */           if ((this.moveEvaluator.valueOfMove((Move)moves.get(i)) > pivot) || (i > end)) break;  } while (k > i);
/*     */ 
/* 250 */         while ((this.moveEvaluator.valueOfMove((Move)moves.get(k)) > pivot) && (k >= start) && (k >= i))
/* 251 */           k--;
/* 252 */         if (k > i)
/* 253 */           swap(moves, i, k);
/*     */       }
/* 255 */       swap(moves, start, k);
/*     */ 
/* 257 */       quickSort(moves, start, k - 1);
/* 258 */       quickSort(moves, k + 1, end);
/*     */     }
/*     */     else;
/*     */   }
/*     */ 
/*     */   public void swap(List<Move> moves, int index1, int index2)
/*     */   {
/* 270 */     Move temp = (Move)moves.get(index1);
/* 271 */     moves.set(index1, (Move)moves.get(index2));
/* 272 */     moves.set(index2, temp);
/*     */   }
/*     */ 
/*     */   private List<Move> rotateList(List<Move> list)
/*     */   {
/* 281 */     if (list.size() > 1) {
/* 282 */       int start = 0;
/* 283 */       int end = list.size() - 1;
/* 284 */       while (start < end) {
/* 285 */         Move tempMove = (Move)list.get(start);
/* 286 */         list.set(start, (Move)list.get(end));
/* 287 */         list.set(end, tempMove);
/* 288 */         start++;
/* 289 */         end--;
/*     */       }
/*     */     }
/* 292 */     return list;
/*     */   }
/*     */ }

/* Location:           /Users/shossain/Documents/workspace/Aiarch/wernerhenke.jar
 * Qualified Name:     wernerhenke.WernerHenke
 * JD-Core Version:    0.6.2
 */