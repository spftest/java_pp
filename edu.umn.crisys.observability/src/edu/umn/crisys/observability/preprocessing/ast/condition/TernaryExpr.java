package edu.umn.crisys.observability.preprocessing.ast.condition;

import edu.umn.crisys.observability.preprocessing.ast.TVL;
import java.util.BitSet;
import org.eclipse.jdt.core.dom.*;

public class TernaryExpr extends Expr {
	public Expr cond;
	public Expr dthen;
	public Expr delse;
	
	// protected int maskMin; 
	// protected int maskMax; 
	
	public TernaryExpr(Expression boolExpr) {
		super(boolExpr);
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}

	public void setCond(Expr cond) {
		this.cond = cond;
	}
	
	public void setThen(Expr dthen) {
		this.dthen = dthen;
	}
	
	public void setElse(Expr delse) {
		this.delse = delse;
	}
	
	public void mark() {
		computeMark(0);
	}
	
	
	/**
	 *  Appends the leaf decision nodes for this decision onto the list ldn_list
	 * @param ldn_list  container for leaf decision nodes.
	 * 
	 */
	
	/** 
	 * marks the decision tree with position attributes and mask min/max values.
	 * @param decisionNumber    number assigned to this decision
	 * @param conditionNumber   current minimum unused condition number within the decision
	 * @return updated minimum unused condition number after marking children.
	 */
	protected int computeMark(int conditionNumber) {
		int condMax = cond.computeMark(conditionNumber) ;
		int dthenMax = dthen.computeMark(condMax);
		return delse.computeMark(dthenMax);
	}
	

	/**
	 * computes condition mask for a given leaf node/truth value assignment
	 * @param mask  	current mask
	 * @param child		child which triggered call
	 * @param v			truth value for child.
	 */
	protected TVL operatorValue(Expr child, TVL v)
	 {
	     if (child == cond) return TVL.Unknown;
	     else return v; 
	 } 

	
	@Override
	protected boolean computeIsTerminal(Expr child, TVL v) {
		v = operatorValue(child, v);
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
		cond.invertChildren(underNegation, !underNegation);
		dthen.invertChildren(underNegation, inverted);
		delse.invertChildren(underNegation, inverted);
	}

	@Override
	protected void computeMask(BitSet mask, Expr child, TVL v) {
		// cond mask does not ever propagate.  Other children
		// always propagate because of short-circuit evaluation.
		if (child != cond && v != TVL.Unknown && parent != null) {
			parent.computeMask(mask, this, operatorValue(child, v));
		}
	}
	
	
	//public abstract TVL operatorValue(TVL v);
	//public abstract void addToBitSet(BitSet bs, TVL v);
}
