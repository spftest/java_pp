package edu.umn.crisys.observability.preprocessing.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.crisys.observability.preprocessing.Util;

public class IdRef {
	public enum RefType {LocalRef, FieldRef, StaticFieldRef};
	
	RefType rt;
	String exprString;
	
	public IdRef(RefType rt, String exprString) {
		this.rt = rt;
		this.exprString = exprString;
	}
	
	public String getExprString() {
		return exprString;
	}
	
	public RefType getRefType() {
		return rt;
	}

	public void toXml(Document dom, Element parent) {
		Element e = dom.createElement("id_ref");
		parent.appendChild(e); 
		// Util.writeXmlTextElement(dom, e, "filename", fileName);
		Util.writeXmlTextElement(dom, e, "ref_type", rt.toString());
		Util.writeXmlTextElement(dom, e, "expr_string", exprString);
	}

}
