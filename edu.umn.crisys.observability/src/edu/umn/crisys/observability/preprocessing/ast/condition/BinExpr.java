package edu.umn.crisys.observability.preprocessing.ast.condition;

import org.eclipse.jdt.core.dom.*;
import edu.umn.crisys.observability.preprocessing.ast.TVL;
import edu.umn.crisys.observability.preprocessing.ast.condition.Expr;


public abstract class BinExpr extends Expr {
	public Expr lhs;
	public Expr rhs;
	
	protected int maskMin; 
	protected int maskMax; 
	
	public BinExpr(Expression boolExpr) {
		super(boolExpr);
	}
	
	public void setLhs(Expr lhs) {
		this.lhs = lhs;
	}
	
	public void setRhs(Expr rhs) {
		this.rhs = rhs;
	}
	
	@Override
	protected int computeMark(int conditionNumber) {
		maskMin = conditionNumber; 
		maskMax = lhs.computeMark(conditionNumber) ; 
		return rhs.computeMark(maskMax);
		// return maskMax;
	}

	/*
	@Override
	protected void computeAttributes(List<IfJVMAttributes> attributes) {
		lhs.computeAttributes(attributes);
		rhs.computeAttributes(attributes);
	}
	*/
	
	protected abstract TVL operatorValue(Expr child, TVL v);

	
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

}
