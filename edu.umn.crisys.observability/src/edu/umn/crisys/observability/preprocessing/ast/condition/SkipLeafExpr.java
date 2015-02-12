package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;
import org.eclipse.jdt.core.dom.*;
import edu.umn.crisys.observability.preprocessing.ast.TVL;

/*
 * This is a dummy class for Boolean expressions we don't want to emit.
 */
public class SkipLeafExpr extends Expr {
	
	public SkipLeafExpr(Expression boolExpr) {
		super(boolExpr);
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return arg;
	}

	@Override
	protected void computeInverted() { }

	@Override
	protected void invertChildren(boolean underNegation, boolean inverted) { }

	@Override
	protected int computeMark(int conditionNumber) {
		return 0;
	}

//	@Override
//	protected void computeAttributes(List<IfJVMAttributes> attributes) { }

	@Override
	protected void computeMask(BitSet mask, Expr child, TVL v) { }

	@Override
	protected boolean computeIsTerminal(Expr child, TVL v) {
		return false;
	}


}
