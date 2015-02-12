package gov.nasa.jpf.observability.struct;

public class ConditionLocation {

 protected String file;
 protected int line;
 protected int decision;
 protected int condition;
 
 // perhaps we should also be storing the assembler line number?
 
 public ConditionLocation(String file, int line,
		 int decision, int condition) {
	 this.file = file;
	 this.line = line;
	 this.decision = decision;
	 this.condition = condition;
 }
 
 public String getFile() {
	 return file;
 }
 
 public int getLine() {
	 return line;
 }
 

 public int getCondition(){ 
	 return condition;
 }
 
 public void setCondition(int cond) {
	 this.condition = cond;
 }
 
 public int getDecision(){
	 return decision;
 }

 @Override
 public String toString() {
	 return "l" + getLine() + "_d" + getDecision() + "_c" + getCondition();
 }

 // should we override equals()?
}


