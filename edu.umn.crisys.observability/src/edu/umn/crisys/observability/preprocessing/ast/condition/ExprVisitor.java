package edu.umn.crisys.observability.preprocessing.ast.condition;


public interface ExprVisitor<T> {

	//public T visit(Expr e, T arg); 
	public T visit(AndExpr e, T arg);
	public T visit(LeafExpr e, T arg);
	public T visit(NotExpr e, T arg);
	public T visit(OrExpr e, T arg);
	public T visit(RelExpr e, T arg); 
	public T visit(TernaryExpr e, T arg);
}
