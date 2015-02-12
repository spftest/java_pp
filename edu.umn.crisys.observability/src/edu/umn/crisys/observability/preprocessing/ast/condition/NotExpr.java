package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;
import org.eclipse.jdt.core.dom.Expression;
import edu.umn.crisys.observability.preprocessing.ast.TVL;

public class NotExpr extends Expr {
	
	Expr child; 
	
	public NotExpr(Expression boolExpr) {
		super(boolExpr);
	}

	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}

	public void setChild(Expr child) {
		this.child = child;
	}
	
	protected void computeMask(BitSet mask, Expr child, TVL v) {
		if (parent != null) {
			parent.computeMask(mask, child, v.negate());
		}
	}
	
	@Override
	protected boolean computeIsTerminal(Expr child, TVL v) {
		v = v.negate();
		if (v == TVL.Unknown) {
			return false;
		} else if (parent == null) {
			return true;
		} else {
			return parent.computeIsTerminal(this, v);
		}
	}

	protected void computeInverted() {
		invertChildren(false, false);
	}
	
	protected void invertChildren(boolean underNegation, boolean inverted) {
		child.invertChildren(!underNegation, inverted);
	}
	
	@Override
	protected int computeMark(int conditionNumber) {
		return child.computeMark(conditionNumber);
	}

/*	@Override
	protected void computeAttributes(List<IfJVMAttributes> attributes) {
		child.computeAttributes(attributes);
	}
*/
};
