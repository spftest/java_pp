package edu.umn.crisys.observability.preprocessing.ast.condition;


public class ExprVisitorBase<T> implements ExprVisitor<T> {

//	public T visit(Expr e, T arg) {
//		throw new Error("reached base class in ExprVisitorBase...why?");
//	}
	
	public T visit(AndExpr e, T arg) {
		return e.rhs.accept(this, e.lhs.accept(this, arg));
	}
	
	public T visit(LeafExpr e, T arg) {
		return arg;
	}
	
	public T visit(NotExpr e, T arg) {
		return e.child.accept(this, arg);
	}
	
	public T visit(OrExpr e, T arg) {
		return e.rhs.accept(this, e.lhs.accept(this,  arg)); 
	}
	
	public T visit(RelExpr e, T arg) {
		return e.rhs.accept(this, e.lhs.accept(this,  arg)); 
	}
	public T visit(TernaryExpr e, T arg) { 
		return e.delse.accept(this,  e.dthen.accept(this, 
				e.cond.accept(this, arg)));
	}
}
