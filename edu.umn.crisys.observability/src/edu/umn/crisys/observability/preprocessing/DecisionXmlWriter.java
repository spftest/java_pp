package edu.umn.crisys.observability.preprocessing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.TVL;
import edu.umn.crisys.observability.preprocessing.ast.condition.*;
import gov.nasa.jpf.observability.struct.ConditionLocation;

public class DecisionXmlWriter extends ExprVisitorBase<Element> {
	
	private Document dom; 
	
	DecisionXmlWriter(Document dom) { 
		this.dom = dom;
	}
	
	public void writeXmlLocation(Document dom, Element parent, ConditionLocation loc) {
		Element e = dom.createElement("condition_location"); 
		parent.appendChild(e); 
		// Util.writeXmlTextElement(dom, e, "file", this.loc.getFile());
		Util.writeXmlTextElement(dom, e, "line", Integer.toString(loc.getLine()));
		Util.writeXmlTextElement(dom, e, "decision", Integer.toString(loc.getDecision()));
		Util.writeXmlTextElement(dom, e, "condition", Integer.toString(loc.getCondition()));
	}
	
	public void methodRefToXml(Document dom, Element parent, String methodName) {
		Element e = dom.createElement("id_ref");
		parent.appendChild(e); 
		// Util.writeXmlTextElement(dom, e, "filename", fileName);
		Util.writeXmlTextElement(dom, e, "ref_type", "MethodRef");
		Util.writeXmlTextElement(dom, e, "expr_string", methodName);
	}
	
	public void printCommonTerminalInformation(TerminalExpr expr, Element e) {
		e.appendChild(dom.createComment("Java Expr: " + expr.getJavaExpr().toString()));
		this.writeXmlLocation(dom, e, expr.getLoc());
		Util.writeXmlTextElement(dom, e, "control_decision", Boolean.toString(expr.getInControlDecision()));
		Util.writeXmlTextElement(dom, e, "true_mask", Util.bitSetToSpaceDelimited(expr.mask(TVL.True)));
		Util.writeXmlTextElement(dom, e, "false_mask", Util.bitSetToSpaceDelimited(expr.mask(TVL.False)));
		Util.writeXmlTextElement(dom, e, "terminal_on_true", Boolean.toString(expr.isTerminal(TVL.True)));
		Util.writeXmlTextElement(dom, e, "terminal_on_false", Boolean.toString(expr.isTerminal(TVL.False)));
		Util.writeXmlTextElement(dom, e, "inverted", Boolean.toString(expr.inverted()));
		Element ee = dom.createElement("referenced_ids");
		e.appendChild(ee);
		for (IdRef ref : expr.getReferencedIds()) {
			ref.toXml(dom,  ee);
		}
		for (String methodRef : expr.getReferencedMethods()) {
			methodRefToXml(dom, ee, methodRef);
		}

		// for nested ternary expressions.
		ee = dom.createElement("decisions");
		e.appendChild(ee);
		for (Expr d: expr.getChildDecisions()) {
			printDecision(d, ee);
		}
		
	}

	
	public Element visit(LeafExpr expr, Element parent) {
		assert(parent != null);
		Element e = dom.createElement("condition");
		parent.appendChild(e);
		printCommonTerminalInformation(expr, e);
		return parent;
	}
	
	public Element visit(SkipLeafExpr expr, Element parent) {
		
		return parent;
	}
	
	public Element visit(RelExpr expr, Element parent) {
		assert(parent != null);
		
		// don't visit LHS or RHS if they are leaf expressions. 
		if (! (expr.lhs instanceof LeafExpr)) {
			expr.lhs.accept(this, parent);
		}
		if (! (expr.rhs instanceof LeafExpr)) {
			expr.rhs.accept(this, parent);
		}

		Element e = dom.createElement("rel_expr");
		parent.appendChild(e);
		printCommonTerminalInformation(expr, e);
		Util.writeXmlTextElement(dom, e, "op", expr.op);		
		return parent;
	}
	
	public void printDecision(Expr expr, Element parent) {
		assert(parent != null); 
		
		Element e = dom.createElement("decision");
		parent.appendChild(e); 
		e.appendChild(dom.createComment("Java Expr: " + expr.javaExpr.toString()));
		boolean trivialDecision = false;
		if (expr instanceof LeafExpr &&
				((LeafExpr)expr).getIsTrivialDecision()) {
			trivialDecision = true;
		}
		Util.writeXmlTextElement(dom, e, "trivial_decision", Boolean.toString(trivialDecision));
		expr.accept(this, e);
	}
	
	
}
