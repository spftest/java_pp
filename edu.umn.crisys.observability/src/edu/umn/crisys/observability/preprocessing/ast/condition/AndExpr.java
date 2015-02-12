package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;

import org.eclipse.jdt.core.dom.Expression;

import edu.umn.crisys.observability.preprocessing.ast.TVL;

public class AndExpr extends BinExpr {
	
	public AndExpr(Expression boolExpr) {
		super(boolExpr);
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}
	
	protected void computeMask(BitSet mask, Expr child, TVL v) {
		if (v != TVL.Unknown) {  
			if (child == rhs && v == TVL.False) {
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
     	if (child == rhs || v == TVL.False) { return v;}
     	else return TVL.Unknown;
     } 
	/*
	@Override
	public void addToBitSet(BitSet bs, TVL v) {
		if (side == Side.Right && v == TVL.False) {
			bs.set(maskLeft, maskRight+1);
		}
	}
	*/
	
	protected void computeInverted() {
		invertChildren(false, true); 
	}
	
	protected void invertChildren(boolean underNegation, boolean inverted) {
		lhs.invertChildren(underNegation, true);
		rhs.invertChildren(underNegation, true);
	}
	
	
}
