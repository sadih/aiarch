/*     */ package wernerhenke;
/*     */ 
/*     */ import fi.zem.aiarch.game.hierarchy.Coord;
/*     */ import fi.zem.aiarch.game.hierarchy.Move;
/*     */ import fi.zem.aiarch.game.hierarchy.MoveType;
/*     */ import fi.zem.aiarch.game.hierarchy.Piece;
/*     */ import fi.zem.aiarch.game.hierarchy.Side;
/*     */ 
/*     */ public class MoveValueEvaluator
/*     */ {
/*     */   private boolean moveLengthInUse;
/*     */   private boolean attackTargetValueInUse;
/*     */   private boolean movementDirectionInUse;
/*     */   private boolean distanceToPreviousMoveInUse;
/*     */   private Move previousMove;
/*     */ 
/*     */   public MoveValueEvaluator()
/*     */   {
/*  17 */     this.moveLengthInUse = false;
/*  18 */     this.attackTargetValueInUse = false;
/*  19 */     this.movementDirectionInUse = false;
/*  20 */     this.distanceToPreviousMoveInUse = false;
/*  21 */     this.previousMove = null;
/*     */   }
/*     */ 
/*     */   public MoveValueEvaluator(boolean moveLength, boolean attackTarget, boolean movementDirection, Move previousMove)
/*     */   {
/*  29 */     this.moveLengthInUse = moveLength;
/*  30 */     this.attackTargetValueInUse = attackTarget;
/*  31 */     this.movementDirectionInUse = movementDirection;
/*  32 */     this.previousMove = previousMove;
/*  33 */     if (this.previousMove == null) {
/*  34 */       this.distanceToPreviousMoveInUse = false;
/*     */     }
/*     */     else
/*  37 */       this.distanceToPreviousMoveInUse = true;
/*     */   }
/*     */ 
/*     */   public int valueOfMove(Move currentMove)
/*     */   {
/*  42 */     int moveLengthValue = 0;
/*  43 */     int attackTargetValue = 0;
/*  44 */     int movementDirection = 0;
/*  45 */     int distanceToLastMove = 0;
/*     */ 
/*  47 */     int currentToX = 0;
/*  48 */     int currentToY = 0;
/*     */ 
/*  50 */     if ((this.moveLengthInUse) || (this.movementDirectionInUse) || (this.distanceToPreviousMoveInUse))
/*     */     {
/*  52 */       currentToX = currentMove.getTo().getX();
/*  53 */       currentToY = currentMove.getTo().getY();
/*     */     }
/*     */ 
/*  56 */     if ((this.moveLengthInUse) || (this.movementDirectionInUse))
/*     */     {
/*  58 */       int deltaX = currentToX - currentMove.getFrom().getX();
/*  59 */       int deltaY = currentToY - currentMove.getFrom().getY();
/*     */ 
/*  61 */       if (this.moveLengthInUse) {
/*  62 */         moveLengthValue = Math.abs(deltaX) + Math.abs(deltaY);
/*     */       }
/*     */ 
/*  65 */       if (this.movementDirectionInUse) {
/*  66 */         if (currentMove.getPlayer().equals(Side.BLUE)) {
/*  67 */           if ((deltaX > 0) || (deltaY > 0)) {
/*  68 */             movementDirection = 1;
/*     */           }
/*     */         }
/*  71 */         else if ((deltaX < 0) || (deltaY < 0)) {
/*  72 */           movementDirection = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  77 */     if ((this.attackTargetValueInUse) && 
/*  78 */       (currentMove.getType().equals(MoveType.ATTACK))) {
/*  79 */       attackTargetValue = currentMove.getTarget().getValue();
/*     */     }
/*     */ 
/*  83 */     if ((this.distanceToPreviousMoveInUse) && 
/*  84 */       (this.previousMove != null)) {
/*  85 */       distanceToLastMove = Math.abs(currentToX - this.previousMove.getTo().getX()) + 
/*  86 */         Math.abs(currentToY - this.previousMove.getTo().getY());
/*     */     }
/*     */ 
/*  89 */     int value = moveLengthValue + 
/*  90 */       attackTargetValue + 
/*  91 */       movementDirection - 
/*  92 */       distanceToLastMove;
/*     */ 
/*  94 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean isMoveLengthInUse() {
/*  98 */     return this.moveLengthInUse;
/*     */   }
/*     */ 
/*     */   public void setMoveLengthInUse(boolean moveLengthInUse) {
/* 102 */     this.moveLengthInUse = moveLengthInUse;
/*     */   }
/*     */ 
/*     */   public boolean isAttackTargetValueInUse() {
/* 106 */     return this.attackTargetValueInUse;
/*     */   }
/*     */ 
/*     */   public void setAttackTargetValueInUse(boolean attackTargetValueInUse) {
/* 110 */     this.attackTargetValueInUse = attackTargetValueInUse;
/*     */   }
/*     */ 
/*     */   public boolean isMovementDirectionInUse() {
/* 114 */     return this.movementDirectionInUse;
/*     */   }
/*     */ 
/*     */   public void setMovementDirectionInUse(boolean movementDirectionInUse) {
/* 118 */     this.movementDirectionInUse = movementDirectionInUse;
/*     */   }
/*     */ 
/*     */   public boolean isDistanceToPreviousMoveInUse() {
/* 122 */     return this.distanceToPreviousMoveInUse;
/*     */   }
/*     */ 
/*     */   public void setDistanceToPreviousMoveInUse(boolean distanceToPreviousMoveInUse) {
/* 126 */     this.distanceToPreviousMoveInUse = distanceToPreviousMoveInUse;
/*     */   }
/*     */ 
/*     */   public Move getPreviousMove() {
/* 130 */     return this.previousMove;
/*     */   }
/*     */ 
/*     */   public void setPreviousMove(Move previousMove) {
/* 134 */     this.previousMove = previousMove;
/* 135 */     if (this.previousMove != null) {
/* 136 */       setDistanceToPreviousMoveInUse(true);
/*     */     }
/*     */     else
/* 139 */       setDistanceToPreviousMoveInUse(false);
/*     */   }
/*     */ }

/* Location:           /Users/shossain/Documents/workspace/Aiarch/wernerhenke.jar
 * Qualified Name:     wernerhenke.MoveValueEvaluator
 * JD-Core Version:    0.6.2
 */