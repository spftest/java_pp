package edu.umn.crisys.observability.preprocessing.ast.condition;

import java.util.BitSet;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;


import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.TVL;
import gov.nasa.jpf.observability.struct.ConditionLocation;

public interface TerminalExpr {
	public Expression getJavaExpr(); 
	public int getPosition();
	public int getDecision(); 
	public boolean getInverted();
	public boolean getInControlDecision();
	public boolean getIsTrivialDecision(); 
	public ConditionLocation getLoc();
	public List<IdRef> getReferencedIds(); 
	public List<String> getReferencedMethods();
	public List<Expr> getChildDecisions(); 

	public BitSet mask(TVL v); 
	public boolean isTerminal(TVL v);
	public boolean inverted();
	
}
