package edu.umn.crisys.observability.preprocessing.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.crisys.observability.preprocessing.Util;

public class Location {
	public String fileName;
	public int startLineNumber;
	public int startColumnNumber;
	public int endLineNumber;
	public int endColumnNumber;
	public int startChar;
	public int endChar;
	
	public String toString() {
		return "[" + fileName + " (" + startLineNumber + ", " + startColumnNumber + 
			") -- (" + endLineNumber + ", " + endColumnNumber + ")]";
	}
	
	public void toXml(Document dom, Element parent) {
		Element e = dom.createElement("location");
		parent.appendChild(e); 
		// Util.writeXmlTextElement(dom, e, "filename", fileName);
		Util.writeXmlTextElement(dom, e, "start_line_number", Integer.toString(startLineNumber));
		Util.writeXmlTextElement(dom, e, "start_column_number", Integer.toString(startColumnNumber));
		Util.writeXmlTextElement(dom, e, "end_line_number", Integer.toString(endLineNumber));
		Util.writeXmlTextElement(dom, e, "end_column_number", Integer.toString(endColumnNumber));
	}
}
