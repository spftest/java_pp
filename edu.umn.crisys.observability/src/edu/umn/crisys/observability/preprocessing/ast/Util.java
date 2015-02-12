package edu.umn.crisys.observability.preprocessing.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;

public class Util {
	
    public static IdRef constructIdRef(Name tree) {
    	IdRef ref = null;
    
    	IBinding binding = tree.resolveBinding();
    	if (binding instanceof IVariableBinding) {
        	IVariableBinding vb = (IVariableBinding)binding;
    		// ITypeBinding ty = vb.getType();
    		boolean isField = vb.isField(); 
    		boolean isStaticField = (isField && 
    								((vb.getModifiers() & org.eclipse.jdt.core.dom.Modifier.STATIC) != 0));
        	IdRef.RefType rt = 
        			(isStaticField) ? IdRef.RefType.StaticFieldRef : 
        				(isField) ? IdRef.RefType.FieldRef : 
        					IdRef.RefType.LocalRef;
        	System.out.println("At: " + tree.toString() + " var type: " + rt.toString()) ;  
        	ref = new IdRef(rt, tree.toString());
    	}
    	return ref;
    }

	public static Location createLocation(String fileName, ASTNode tree, CompilationUnit compilationUnit) {
		Location loc = new Location();
		
    	
     	loc.startChar = tree.getStartPosition();
     	loc.fileName = fileName;
		loc.startLineNumber = compilationUnit.getLineNumber(loc.startChar);
		loc.startColumnNumber = compilationUnit.getColumnNumber(loc.startChar);
		loc.endChar = loc.startChar + tree.getLength();
		loc.endLineNumber = compilationUnit.getLineNumber(loc.endChar);
		loc.endColumnNumber = compilationUnit.getColumnNumber(loc.endChar);
		return loc;
	}

}