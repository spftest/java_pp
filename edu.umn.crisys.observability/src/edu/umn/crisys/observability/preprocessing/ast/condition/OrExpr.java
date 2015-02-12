package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;

import org.eclipse.jdt.core.dom.Expression;

import edu.umn.crisys.observability.preprocessing.ast.TVL;

public class OrExpr extends BinExpr {
	
	public OrExpr(Expression boolExpr) {
		super(boolExpr);
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}

	protected void computeMask(BitSet mask, Expr child, TVL v) {
		if (v != TVL.Unknown) {  
			if (child == rhs && v == TVL.True) {
				mask.set(maskMin, maskMax);
			}
			if (parent != null) {
				parent.computeMask(mask, this, operatorValue(child, v));
			}
		}
	}
	@Override
	protected TVL operatorValue(Expr child, TVL v)
	 {
    	if (child == rhs || v == TVL.True) { return v;}
    	else return TVL.Unknown;
    } 
	
	protected void computeInverted() {
		invertChildren(false, false);
	}
	/* 
	 * Suppose: underNegation false, inverted false
	 * (non-Javadoc)
	 * @see edu.umn.crisys.observability.preprocessing.ast.condition.Expr#invertChildren(boolean, boolean)
	 */
	protected void invertChildren(boolean underNegation, boolean inverted) {
		lhs.invertChildren(underNegation, false);  
		rhs.invertChildren(underNegation, true);
	}
	
}
