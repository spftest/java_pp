package edu.umn.crisys.observability.preprocessing.ast.scoping;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.crisys.observability.preprocessing.Util;
import edu.umn.crisys.observability.preprocessing.ast.Location;

// record type for decision.
public class ControlDecisionInfo {
	Location loc;
	
	public enum DecisionType {IfThen, IfThenElse, For, While, DoWhile};
	public DecisionType dt;
	
	public ControlDecisionInfo(Location loc, DecisionType dt) {
		this.loc = loc;
		this.dt = dt;
	}
	
	public void toXml(Document dom, Element parent) {
		Element e = dom.createElement("control_decision");
		parent.appendChild(e);
		loc.toXml(dom, e);
		Util.writeXmlTextElement(dom, e, "type", dt.toString());
	}
}
