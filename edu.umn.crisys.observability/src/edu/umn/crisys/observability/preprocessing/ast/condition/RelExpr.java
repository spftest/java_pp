/*
 * 
 * Still thinking about correct way of dealing with Boolean equality/inequality
 * DO NOT USE THIS CLASS until we have a clear understanding of algorithm.
 * 
 */

package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;

import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.TVL;
import gov.nasa.jpf.observability.struct.ConditionLocation;

public class RelExpr extends BinExpr implements TerminalExpr {
	
	// kind of comparison: ==, !=, xor
	public String op;
	int position; 
	int decisionNumber;
	
	// inverted is always false for rel ops.
	boolean inverted;
	
	public boolean inControlDecision;
	public ConditionLocation loc;
	
	public RelExpr(Expression boolExpr, String op, ConditionLocation loc,
			boolean inControlDecision) {
		super(boolExpr);
		this.inControlDecision = inControlDecision;
		this.op = op;
		this.loc = loc;
	}
	
	public <T> T accept(ExprVisitor<T> ev, T arg) {
		return ev.visit(this, arg);
	}
	
	public boolean getIsTrivialDecision() { return false; }
	public int getPosition() { return this.position; }
	public int getDecision() { return this.decisionNumber; }
	public boolean getInverted() {return this.inverted; }
	public boolean getInControlDecision() {return this.inControlDecision; }
	public ConditionLocation getLoc() {return this.loc; }
	public List<Expr> getChildDecisions() {
		if (lhs instanceof LeafExpr) {
			return ((LeafExpr)lhs).getChildDecisions(); 
		}
		else 
			return new ArrayList<Expr>(); 
	}
	
	public boolean inverted() { return this.inverted; }
	
	
	// Relational operator always blocks masking.
	@Override
	protected void computeMask(BitSet mask, Expr child, TVL v) {
		// for children, return 'mask' as is.
	}
	
	// operator value cannot be computed without knowing values of both 
	// LHS and RHS arguments.
	@Override
	protected TVL operatorValue(Expr child, TVL v)
	{
    	return TVL.Unknown;
    } 

	// since we can't know a-priori (and neither can the compiler) 
	// what the value of the equality is, it is terminal only if 
	// it is the top of the expr or terminal for both TRUE and FALSE
	// values.
	@Override
	protected boolean computeIsTerminal(Expr child, TVL v) {
		if (v == TVL.Unknown) {
			return false;
		}
		if (parent == null) {
			return (child == this.rhs);
		} else {
			return (parent.computeIsTerminal(this, TVL.False) && 
					parent.computeIsTerminal(this, TVL.True));
		}
	}
	
	@Override
	// mark is the initial traversal to mark the AST and set the ConditionLocation
	protected int computeMark(int conditionNumber) {
		conditionNumber = super.computeMark(conditionNumber);
		this.position = conditionNumber; 
		loc.setCondition(this.position);
		return conditionNumber + 1;
	}

	@Override
	protected void computeInverted() {
		invertChildren(false, false);
	}
	
	// relational operators do not influence inversion (I think).
	// TODO: Do they reset it?  Yes, it appears always false/false, at least for equality
	protected void invertChildren(boolean underNegation, boolean inverted) {
		if (underNegation) {
			this.inverted = !inverted;
		} else {
			this.inverted = inverted;
		}
		lhs.invertChildren(false, false);
		rhs.invertChildren(false, false);
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

	// since LHS leaf expressions do not get a condition,
	// we record their ids here.
	
	public List<IdRef> getReferencedIds() {
		if (lhs instanceof LeafExpr) {
			return ((LeafExpr)lhs).referencedIds;
		} else {
			return new ArrayList<IdRef>(); 
		}
	}

	public List<String> getReferencedMethods() {
		if (lhs instanceof LeafExpr) {
			return ((LeafExpr)lhs).getReferencedMethods();
		} else {
			return new ArrayList<String>(); 
		}
	}

}
