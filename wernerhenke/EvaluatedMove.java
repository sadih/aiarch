/*    */ package wernerhenke;
/*    */ 
/*    */ import fi.zem.aiarch.game.hierarchy.Move;
/*    */ 
/*    */ public class EvaluatedMove
/*    */ {
/*    */   private Move move;
/*    */   private float value;
/*    */ 
/*    */   public EvaluatedMove(Move move, float value)
/*    */   {
/* 10 */     this.move = move;
/* 11 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public Move getMove() {
/* 15 */     return this.move;
/*    */   }
/*    */   public void setMove(Move move) {
/* 18 */     this.move = move;
/*    */   }
/*    */   public float getValue() {
/* 21 */     return this.value;
/*    */   }
/*    */   public void setValue(float value) {
/* 24 */     this.value = value;
/*    */   }
/*    */ }

/* Location:           /Users/shossain/Documents/workspace/Aiarch/wernerhenke.jar
 * Qualified Name:     wernerhenke.EvaluatedMove
 * JD-Core Version:    0.6.2
 */