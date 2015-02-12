package gov.nasa.jpf.observability.struct;


public class LocalVarRef extends VarRef {
	public int localTblLocation; 
	public String name;
	
	public LocalVarRef(int localTblLocation) {
		this.localTblLocation = localTblLocation;
	}
	
	public LocalVarRef(String name) {
		this.name = name;
	}
	
	public LocalVarRef(int localTblLocation, String name){
		this.localTblLocation = localTblLocation;
		this.name = name;
	}
	
}; 
