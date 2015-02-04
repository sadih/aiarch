/*     */ package wernerhenke;
/*     */ 
/*     */ import fi.zem.aiarch.game.hierarchy.Board;
/*     */ import fi.zem.aiarch.game.hierarchy.Board.Square;
/*     */ import fi.zem.aiarch.game.hierarchy.Coord;
/*     */ import fi.zem.aiarch.game.hierarchy.Move;
/*     */ import fi.zem.aiarch.game.hierarchy.MoveType;
/*     */ import fi.zem.aiarch.game.hierarchy.Piece;
/*     */ import fi.zem.aiarch.game.hierarchy.Side;
/*     */ import fi.zem.aiarch.game.hierarchy.Situation;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Evaluator
/*     */ {
/*     */   static int maxValue;
/*     */   private int[][] pieces;
/*  16 */   private float ownMultiplierNumOfPieces = 2.0F;
/*  17 */   private float oppMultiplierNumOfPieces = 2.0F;
/*  18 */   private float ownMultiplierSumOfPiecesValues = 7.0F;
/*  19 */   private float oppMultiplierSumOfPiecesValues = 7.0F;
/*  20 */   private int multiplierGameWon = maxValue;
/*  21 */   private float ownMultiplierAttackDistanceSum = 1.5F;
/*  22 */   private float ownMultiplierAttackDistanceAvg = 2.0F;
/*  23 */   private float oppMultiplierAttackDistanceSum = 1.5F;
/*  24 */   private float oppMultiplierAttackDistanceAvg = 2.0F;
/*  25 */   private float ownMultiplierAttackablePieceValueSum = 0.6F;
/*  26 */   private float oppMultiplierAttackablePieceValueSum = 0.6F;
/*  27 */   private int wrongWayAttackDecrease = 2;
/*     */ 
/*  30 */   float value = 0.0F;
/*     */ 
/*  32 */   private int ownAttackDistanceSum = 0;
/*  33 */   private float ownAttackDistanceAvg = 0.0F;
/*  34 */   private int oppAttackDistanceSum = 0;
/*  35 */   private float oppAttackDistanceAvg = 0.0F;
/*     */ 
/*  37 */   private int countOwnPieces = 0;
/*  38 */   private int sumOfOwnPieces = 0;
/*  39 */   private int countOppPieces = 0;
/*  40 */   private int sumOfOppPieces = 0;
/*  41 */   private int ownAttackablePieceValueSum = 0;
/*  42 */   private int oppAttackablePieceValueSum = 0;
/*     */ 
/*     */   public Evaluator(int _maxValue) {
/*  45 */     maxValue = _maxValue;
/*     */   }
/*     */ 
/*     */   public float Evaluate(Situation situation, Side mySide, List<Move> legalMoves)
/*     */   {
/*  55 */     this.ownAttackDistanceSum = 0;
/*  56 */     this.ownAttackDistanceAvg = 0.0F;
/*  57 */     this.oppAttackDistanceSum = 0;
/*  58 */     this.oppAttackDistanceAvg = 0.0F;
/*     */ 
/*  60 */     this.countOwnPieces = 0;
/*  61 */     this.sumOfOwnPieces = 0;
/*  62 */     this.countOppPieces = 0;
/*  63 */     this.sumOfOppPieces = 0;
/*  64 */     this.ownAttackablePieceValueSum = 0;
/*  65 */     this.oppAttackablePieceValueSum = 0;
/*     */ 
/*  67 */     this.value = 0.0F;
/*     */ 
/*  69 */     Side ownSide = mySide;
/*     */     Side opponentSide;
/*     */     Side opponentSide;
/*  72 */     if (ownSide == Side.BLUE)
/*  73 */       opponentSide = Side.RED;
/*     */     else {
/*  75 */       opponentSide = Side.BLUE;
/*     */     }
/*  77 */     int wonGame = 0;
/*  78 */     if (situation.isFinished()) {
/*  79 */       if (situation.getWinner().equals(ownSide))
/*  80 */         wonGame = 1;
/*  81 */       else if (situation.getWinner().equals(opponentSide)) {
/*  82 */         wonGame = -1;
/*     */       }
/*     */     }
/*  85 */     if (situation.isFinished()) {
/*  86 */       return wonGame * this.multiplierGameWon;
/*     */     }
/*     */ 
/*  89 */     int ownAttackCapablePieces = 0;
/*  90 */     int oppAttackCapablePieces = 0;
/*     */ 
/*  93 */     this.pieces = new int[9][9];
/*     */ 
/*  97 */     Iterator ownSquares = situation.getBoard().pieces(ownSide).iterator();
/*     */ 
/*  99 */     while (ownSquares.hasNext()) {
/* 100 */       Board.Square currSquare = (Board.Square)ownSquares.next();
/* 101 */       Piece currPiece = currSquare.getPiece();
/* 102 */       this.pieces[currSquare.getX()][currSquare.getY()] = currPiece.getValue();
/*     */ 
/* 104 */       this.countOwnPieces += 1;
/* 105 */       this.sumOfOwnPieces += currPiece.getValue();
/*     */     }
/*     */ 
/* 110 */     Iterator oppSquares = situation.getBoard().pieces(opponentSide).iterator();
/* 111 */     while (oppSquares.hasNext()) {
/* 112 */       Board.Square currSquare = (Board.Square)oppSquares.next();
/* 113 */       Piece currPiece = currSquare.getPiece();
/* 114 */       this.pieces[currSquare.getX()][currSquare.getY()] = (-currPiece.getValue());
/*     */ 
/* 116 */       this.countOppPieces += 1;
/* 117 */       this.sumOfOppPieces += currPiece.getValue();
/*     */     }
/*     */ 
/* 122 */     for (int y = 0; y < 9; y++) {
/* 123 */       for (int x = 0; x < 9; x++) {
/* 124 */         if ((x + y > 5) && (x + y < 11) && (this.pieces[x][y] != 0))
/*     */         {
/* 128 */           int[] neighbors = { -100, -100, -100, -100 };
/*     */ 
/* 130 */           if (x - 1 >= 0)
/* 131 */             neighbors[0] = this.pieces[(x - 1)][y];
/* 132 */           if (x + 1 < 9)
/* 133 */             neighbors[1] = this.pieces[(x + 1)][y];
/* 134 */           if (y - 1 >= 0)
/* 135 */             neighbors[2] = this.pieces[x][(y - 1)];
/* 136 */           if (y + 1 < 9) {
/* 137 */             neighbors[3] = this.pieces[x][(y + 1)];
/*     */           }
/* 139 */           int blockedSides = 4;
/* 140 */           int attackDistance = 0;
/* 141 */           for (int i = 0; i < neighbors.length; i++) {
/* 142 */             if (neighbors[i] == 0)
/* 143 */               blockedSides--;
/* 144 */             else if (neighbors[i] != -100) {
/* 145 */               attackDistance += neighbors[i];
/*     */             }
/*     */           }
/*     */ 
/* 149 */           if (blockedSides < 4) {
/* 150 */             if ((neighbors[1] != 0) && (neighbors[3] != 0) && (
/* 151 */               ((ownSide == Side.BLUE) && (this.pieces[x][y] > 0)) || (
/* 152 */               (opponentSide == Side.BLUE) && (this.pieces[x][y] < 0)))) {
/* 153 */               if (attackDistance > this.wrongWayAttackDecrease)
/* 154 */                 attackDistance -= this.wrongWayAttackDecrease;
/* 155 */               else if (attackDistance < -this.wrongWayAttackDecrease)
/* 156 */                 attackDistance += this.wrongWayAttackDecrease;
/*     */               else {
/* 158 */                 attackDistance = 0;
/*     */               }
/*     */             }
/* 161 */             if ((neighbors[0] != 0) && (neighbors[2] != 0) && (
/* 162 */               ((ownSide == Side.RED) && (this.pieces[x][y] > 0)) || (
/* 163 */               (opponentSide == Side.RED) && (this.pieces[x][y] < 0)))) {
/* 164 */               if (attackDistance > this.wrongWayAttackDecrease)
/* 165 */                 attackDistance -= this.wrongWayAttackDecrease;
/* 166 */               else if (attackDistance < -this.wrongWayAttackDecrease)
/* 167 */                 attackDistance += this.wrongWayAttackDecrease;
/*     */               else {
/* 169 */                 attackDistance = 0;
/*     */               }
/*     */             }
/* 172 */             if ((this.pieces[x][y] > 0) && (attackDistance > 0)) {
/* 173 */               ownAttackCapablePieces++;
/* 174 */               this.ownAttackDistanceSum += attackDistance;
/*     */             }
/* 176 */             else if ((this.pieces[x][y] < 0) && (attackDistance < 0)) {
/* 177 */               oppAttackCapablePieces++;
/* 178 */               this.oppAttackDistanceSum += attackDistance;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 185 */     if (ownAttackCapablePieces != 0) {
/* 186 */       this.ownAttackDistanceAvg = (this.ownAttackDistanceSum / ownAttackCapablePieces);
/*     */     }
/* 188 */     if (oppAttackCapablePieces != 0) {
/* 189 */       this.oppAttackDistanceAvg = (this.oppAttackDistanceSum / oppAttackCapablePieces);
/*     */     }
/*     */ 
/* 192 */     int attackablePieceValueSum = 0;
/* 193 */     int[][] attackablePieceCounted = new int[9][9];
/*     */ 
/* 196 */     for (int i = 0; i < legalMoves.size(); i++) {
/* 197 */       Move move = (Move)legalMoves.get(i);
/* 198 */       Coord moveFrom = move.getFrom();
/* 199 */       int x = moveFrom.getX();
/* 200 */       int y = moveFrom.getY();
/* 201 */       if ((attackablePieceCounted[x][y] == 0) && (move.getType() == MoveType.ATTACK)) {
/* 202 */         attackablePieceValueSum += move.getTarget().getValue();
/* 203 */         attackablePieceCounted[x][y] = 1;
/*     */       }
/*     */     }
/*     */ 
/* 207 */     if (ownSide == situation.getTurn())
/* 208 */       this.ownAttackablePieceValueSum = attackablePieceValueSum;
/*     */     else {
/* 210 */       this.oppAttackablePieceValueSum = attackablePieceValueSum;
/*     */     }
/*     */ 
/* 213 */     situation.apply(situation.makePass());
/* 214 */     legalMoves = situation.legal();
/*     */ 
/* 216 */     attackablePieceValueSum = 0;
/*     */ 
/* 219 */     for (int i = 0; i < legalMoves.size(); i++) {
/* 220 */       Move move = (Move)legalMoves.get(i);
/* 221 */       Coord moveFrom = move.getFrom();
/* 222 */       int x = moveFrom.getX();
/* 223 */       int y = moveFrom.getY();
/* 224 */       if ((attackablePieceCounted[x][y] == 0) && (move.getType() == MoveType.ATTACK)) {
/* 225 */         attackablePieceValueSum += move.getTarget().getValue();
/* 226 */         attackablePieceCounted[x][y] = 1;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     if (ownSide == situation.getTurn())
/* 231 */       this.ownAttackablePieceValueSum = attackablePieceValueSum;
/*     */     else {
/* 233 */       this.oppAttackablePieceValueSum = attackablePieceValueSum;
/*     */     }
/*     */ 
/* 238 */     this.value = 
/* 239 */       (this.countOwnPieces * this.ownMultiplierNumOfPieces - 
/* 240 */       this.countOppPieces * this.oppMultiplierNumOfPieces + 
/* 241 */       this.sumOfOwnPieces * this.ownMultiplierSumOfPiecesValues - 
/* 242 */       this.sumOfOppPieces * this.oppMultiplierSumOfPiecesValues + 
/* 243 */       this.ownAttackDistanceSum * this.ownMultiplierAttackDistanceSum + 
/* 244 */       this.ownAttackDistanceAvg * this.ownMultiplierAttackDistanceAvg + 
/* 245 */       this.oppAttackDistanceSum * this.oppMultiplierAttackDistanceSum + 
/* 246 */       this.oppAttackDistanceAvg * this.oppMultiplierAttackDistanceAvg + 
/* 247 */       this.ownAttackablePieceValueSum * this.ownMultiplierAttackablePieceValueSum - 
/* 248 */       this.oppAttackablePieceValueSum * this.oppMultiplierAttackablePieceValueSum);
/*     */ 
/* 251 */     return this.value;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 256 */     String out = "WernerMod3: \n";
/*     */ 
/* 258 */     out = out + "Board:\n";
/* 259 */     for (int y = 0; y < 9; y++) {
/* 260 */       for (int x = 0; x < 9; x++) {
/* 261 */         out = out + (
/* 262 */           this.pieces[x][y] >= 0 ? " " : "") + 
/* 263 */           this.pieces[x][y] + (
/* 264 */           (x + y > 5) && (x + y < 11) && (this.pieces[x][y] != 0) ? "a" : " ") + 
/* 265 */           ", ";
/*     */       }
/* 267 */       out = out + "\n";
/*     */     }
/*     */ 
/* 270 */     out = out + "countOwnPieces: " + this.countOwnPieces + " * " + this.ownMultiplierNumOfPieces + ": " + 
/* 271 */       this.countOwnPieces * this.ownMultiplierNumOfPieces + "\n";
/* 272 */     out = out + "countOppPieces: " + this.countOppPieces + " * " + this.oppMultiplierNumOfPieces + ": " + 
/* 273 */       this.countOppPieces * this.oppMultiplierNumOfPieces + "\n";
/*     */ 
/* 275 */     out = out + "sumOfOwnPieces: " + this.sumOfOwnPieces + " * " + this.ownMultiplierSumOfPiecesValues + ": " + 
/* 276 */       this.sumOfOwnPieces * this.ownMultiplierSumOfPiecesValues + "\n";
/* 277 */     out = out + "sumOfOppPieces: " + this.sumOfOppPieces + " * " + this.oppMultiplierSumOfPiecesValues + ": " + 
/* 278 */       this.sumOfOppPieces * this.oppMultiplierSumOfPiecesValues + "\n";
/*     */ 
/* 280 */     out = out + "ownAttackDistanceSum: " + this.ownAttackDistanceSum + " * " + this.ownMultiplierAttackDistanceSum + ": " + 
/* 281 */       this.ownAttackDistanceSum * this.ownMultiplierAttackDistanceSum + "\n";
/* 282 */     out = out + "ownAttackDistanceAvg: " + this.ownAttackDistanceAvg + " * " + this.ownMultiplierAttackDistanceAvg + ": " + 
/* 283 */       this.ownAttackDistanceAvg * this.ownMultiplierAttackDistanceAvg + "\n";
/* 284 */     out = out + "ownAttackablePieceValueSum: " + this.ownAttackablePieceValueSum + " * " + this.ownMultiplierAttackablePieceValueSum + ": " + 
/* 285 */       this.ownAttackablePieceValueSum * this.ownMultiplierAttackablePieceValueSum + "\n";
/*     */ 
/* 287 */     out = out + "oppAttackDistanceSum: " + this.oppAttackDistanceSum + " * " + this.oppMultiplierAttackDistanceSum + ": " + 
/* 288 */       this.oppAttackDistanceSum * this.oppMultiplierAttackDistanceSum + "\n";
/* 289 */     out = out + "oppAttackDistanceAvg: " + this.oppAttackDistanceAvg + " * " + this.oppMultiplierAttackDistanceAvg + ": " + 
/* 290 */       this.oppAttackDistanceAvg * this.oppMultiplierAttackDistanceAvg + "\n";
/* 291 */     out = out + "oppAttackablePieceValueSum: " + this.oppAttackablePieceValueSum + " * " + this.oppMultiplierAttackablePieceValueSum + ": " + 
/* 292 */       this.oppAttackablePieceValueSum * this.oppMultiplierAttackablePieceValueSum + "\n";
/*     */ 
/* 294 */     out = out + "Final value: " + this.value + "\n";
/*     */ 
/* 296 */     return out;
/*     */   }
/*     */ }

/* Location:           /Users/shossain/Documents/workspace/Aiarch/wernerhenke.jar
 * Qualified Name:     wernerhenke.Evaluator
 * JD-Core Version:    0.6.2
 */