/*
 * class ifJVMAttributes
 * 
 * This class stores the attributes necessary for efficient computation of 
 * MCDC or OMCDC obligations at the JVM bytecode level.  It is just a 
 * container class with data that will be used by a visitor during 
 * JPF/SPF search.
 * 
 * The fields are documented in the body of the class
 * 
 * Created: 6/27/2013 Neha Rungta
 * Updated: 6/28/2013 Mike Whalen - added fields: 
 * 	conditionCompletesDecisionWhen
 *  conditionVarRefs
 *  falseMask
 *  trueMask 
 * Updated: 8/28/2013 Daniela Grijincu - added fields:
 * 	variablesAssignedInIfBlock
 * 	variablesAssignedInElseBlock
 * 	ifBlock
 * 	hasElseBlock
 * 	isLoop
 */


package gov.nasa.jpf.observability.struct;

import java.util.HashSet;
import java.util.BitSet;

public class IfJVMAttributes {
	
	  public ConditionLocation loc; // bytecode position
	  public Boolean inverted;  // with respect to the source condition a > b 
	  
	  // describes whether this condition completes a decision.  For example, 
	  // given (a || b) && c: 
	  //   a never completes the decision
	  //   b completes the decision if it is false
	  //   c completes the decision if it is either true or false
	  public CompletionValue conditionCompletesDecisionWhen; 
	  
	  // This describes the set of variables that are referenced when evaluating
	  // the condition.  I'm assuming this set is constant, but it is not 
	  // necessarily so if ternary expressions are used: b ? x : y.  We will
	  // have to re-examine this.  Also, how function return values factor 
	  // in is currently unknown.
	  public HashSet<VarRef> conditionVarRefs ;
	  
	  // falseMask and trueMask: these describe the condition numbers in the 
	  // decision that are masked out if the condition has the value 'false' 
	  // (resp. 'true').  It is assumed that conditions within a decision 
	  // are ordered left to right starting from 0.  The conditions that will be masked 
	  // out will always be strictly less than (i.e. to the left of) the 
	  // current condition. 
	  
	  public BitSet falseMask; 
	  public BitSet trueMask;
	  
	  // the variables assigned in the IF block
	  public HashSet<VarRef> variablesAssignedInIfBlock = new HashSet<VarRef>();
	  
	  // the variables assigned in the ELSE block
	  public HashSet<VarRef> variablesAssignedInElseBlock = new HashSet<VarRef>();
	  
	  // if the decision is an IF block and not just an assignment (i.e. x = a && b) 
	  // then the ifBlock attribute should be set to true
	  // in cases of loop blocks, both ifBlock and hasElseBlock attributes should remain false
	  public boolean ifBlock = false;
	  
	  // if the IF block also has an ELSE block then this attribute should be set to true
	  public boolean hasElseBlock = false;
	  
	  // if the decision is a LOOP block then this attribute should be set to true
	  public boolean isLoop = false;
	  
	  
	  public IfJVMAttributes(ConditionLocation loc, Boolean inverted) {
		  this.loc = loc;
		  this.inverted = inverted;
	  }; 
	  
	  public void preprocess(CompletionValue conditionCompletesDecisionWhen,
			  			HashSet<VarRef> conditionVarRefs,
			  			BitSet falseMask, 
			  			BitSet trueMask) {
		  this.conditionCompletesDecisionWhen = conditionCompletesDecisionWhen;
		  this.conditionVarRefs = conditionVarRefs;
		  this.falseMask = falseMask;
		  this.trueMask = trueMask;
	  }
	  
};  