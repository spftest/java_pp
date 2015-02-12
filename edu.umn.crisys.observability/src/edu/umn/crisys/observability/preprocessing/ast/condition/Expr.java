package edu.umn.crisys.observability.preprocessing.ast.condition;

import edu.umn.crisys.observability.preprocessing.ast.TVL;
import java.util.BitSet;
import org.eclipse.jdt.core.dom.*;

public abstract class Expr implements Visitable {
	public Expr parent = null; 
	public Expression javaExpr; 
	
	public Expr(Expression javaExpr) {
		this.javaExpr = javaExpr;
	}

	public Expression getJavaExpr() {
		return javaExpr;
	}

	public void setJavaExpr(Expression javaExpr) {
		this.javaExpr = javaExpr;
	}

	public Expr getParent() {
		return this.parent;
	}
	
	public void setParent(Expr parent) {
		this.parent = parent;
	}
	
	public void mark() {
		computeMark(0);
		computeInverted();
		
	}
	
/*	// should be called on 'top level' decisions only.
	public void addAttributes(List<IfJVMAttributes> attributes) {
		computeAttributes(attributes);
		computeInverted(); 
	}
*/	
		
	protected abstract void computeInverted(); 
	
	protected abstract void invertChildren(boolean underNegation, boolean inverted); 
	
	/** 
	 * marks the decision tree with position attributes and mask min/max values.
	 * @param decisionNumber    number assigned to this decision
	 * @param conditionNumber   current minimum unused condition number within the decision
	 * @return updated minimum unused condition number after marking children.
	 */
	protected abstract int computeMark(int conditionNumber);
	
	/**
	 * computes the attributes for conditions and adds them to a list.
	 * 
	 * TODO: examine why this method is necessary.
	 * 
	 * @param attributes	list where attributes will be stored.
	 */
	//protected abstract void computeAttributes(List<IfJVMAttributes> attributes);

	/**
	 * computes condition mask for a given leaf node/truth value assignment
	 * @param mask  	current mask
	 * @param child		child which triggered call
	 * @param v			truth value for child.
	 */
	protected abstract void computeMask(BitSet mask, Expr child, TVL v);
	protected abstract boolean computeIsTerminal(Expr child, TVL v);
	
	
	//public abstract TVL operatorValue(TVL v);
	//public abstract void addToBitSet(BitSet bs, TVL v);
}
