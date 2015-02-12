package gov.nasa.jpf.observability.struct;


public class FieldVarRef extends VarRef {
	public int objectRef;
	public int fieldRef;
	public String name;
	public boolean staticRef;
	
	public FieldVarRef(){}
	
	public FieldVarRef(String name, boolean staticRef) {
		this.name = name;
		this.staticRef = staticRef;
	}
	
	public FieldVarRef(int objectRef, int fieldRef, String fieldName, boolean staticRef){
		this.objectRef = objectRef;
		this.fieldRef = fieldRef;
		this.name = fieldName;
		this.staticRef = staticRef;
	}
	
}; 
