package edu.umn.crisys.observability.preprocessing.ast.condition;

public interface Visitable {
	public <T> T accept(ExprVisitor<T> ev, T arg);
}
