package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.TVL;
import gov.nasa.jpf.observability.struct.ConditionLocation;

public class LeafExpr extends Expr implements TerminalExpr {
	
	int position; 
	int decisionNumber;
	boolean inverted;
	public boolean isTrivialDecision;
	public boolean inControlDecision;
	public ConditionLocation loc;
	public List<IdRef> referencedIds;
	public List<String> referencedMethods;
	public List<Expr> childDecisions;
	
	public LeafExpr(Expression boolExpr, ConditionLocation loc,
						boolean inControlDecision,
						boolean isSkipLeaf,
						List<IdRef> referencedIds,
						List<String> referencedMethods,
						List<Expr> childDecisions) {
		super(boolExpr);
		this.loc = loc;
		this.inControlDecision = inControlDecision;
		this.isTrivialDecision = isSkipLeaf;
		this.referencedIds = referencedIds;
		this.referencedMethods = referencedMethods;
		this.childDecisions = childDecisions;
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}

	public int getPosition() {return this.position; }
	public int getDecision() {return this.decisionNumber; }
	public boolean getInverted() {return this.inverted; }
	public boolean getInControlDecision() { return this.inControlDecision; }
	public boolean getIsTrivialDecision() { return this.isTrivialDecision; }
	public ConditionLocation getLoc() {return this.loc; }
	public List<IdRef> getReferencedIds() {return this.referencedIds; }
	public List<String> getReferencedMethods() { return this.referencedMethods; }
	public List<Expr> getChildDecisions() {return this.childDecisions; }
	
	/* public void setReferencedIds(List<IdRef> referencedIds) {
		this.referencedIds = referencedIds;
	}
	*/
	
	// mark is the initial traversal to mark the AST and set the ConditionLocation
	protected int computeMark(int conditionNumber) {
		this.position = conditionNumber; 
		loc.setCondition(this.position);
		return conditionNumber + 1;
	}
	
	protected void computeMask(BitSet mask, Expr child, TVL v) {
		System.out.println("Error: computeMaskInternal called on a LeafDecisionNode.");
	}
	
	@Override
	protected boolean computeIsTerminal(Expr child, TVL v) {
		System.out.println("Error: computeIsTerminal called on a LeafDecisionNode.");
		return false;
	}

	public BitSet mask(TVL v) {
		BitSet mask = new BitSet();
		if (parent != null) {
			parent.computeMask(mask, this, v);
		}
		return mask;
	}
		
	public boolean isTerminal(TVL v) {
		if (v == TVL.Unknown) {
			return false;
		} else if (parent == null) {
			return true;
		} else {
			return parent.computeIsTerminal(this, v);
		}
	}

	protected void computeInverted() {
		invertChildren(false, true);
	}
	
	protected void invertChildren(boolean underNegation, boolean inverted) {
		if (underNegation) {
			this.inverted = !inverted;
		} else {
			this.inverted = inverted;
		}
	}

	public boolean inverted() {
		return inverted; 
	}
	

/*	@Override
 	protected void computeAttributes(List<IfJVMAttributes> attributes) {
		IfJVMAttributes attrs = new IfJVMAttributes(loc, inverted());
		CompletionValue cv;
		boolean completesOnTrue = isTerminal(TVL.True);
		boolean completesOnFalse = isTerminal(TVL.False);
		if (completesOnTrue && completesOnFalse) {
			cv = CompletionValue.IF_EITHER;
		} else if (completesOnTrue) {
			cv = CompletionValue.IF_TRUE;
		} else if (completesOnFalse) {
			cv = CompletionValue.IF_FALSE;
		} else {
			cv = CompletionValue.NEVER;
		}
		attrs.preprocess(cv, null, mask(TVL.True), mask(TVL.False));
		attributes.add(attrs);
	}
*/
	
}
