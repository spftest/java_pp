package edu.umn.crisys.observability.preprocessing;

import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.Location;
import edu.umn.crisys.observability.preprocessing.ast.scoping.ControlDecisionInfo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * PreprocessClassVisitor: class that performs condition preprocessing 
 * required by the OMCDC test generation and monitoring framework. 
 * It uses the javac API to perform the traversal.
 *
 *
 * @author Whalen
 * @version 0.1
 */

/* MWW 3/4/2014
 * 
 * Split from ConditionVisitorEclipse
 * 
 * This class visits the structure of a method.  For each 
 * method, it maintains a hierarchical structure of code blocks
 * where in each block we record: 
 * 	 - the subblocks and what generated them (ite, for, while, etc)
 *   - the variable assignments
 *   	For the assigned variables, the goal is to record whether the assigned 
 *   	var is a local or a reference to a field.
 *   - the functions called within the block.
 *
 *  For a class structure we have: 
 *  Block, which contains a list of annotations
 *  Where an annotation can be: 
 *  	Function call
 *  	Assignment
 *  	BlockStructure
 *  		While
 *  		For
 *  		IfThen
 *  		IfThenElse
 *  
 *  To determine locals vs. fields, we need to track defined locals. 
 *  Probably some way to do this for free, but I'll write it.
 *  
 *  
*/ 


public class StructureVisitor extends ASTVisitor {
	
	// private boolean debug = true;

    Deque<Element> parents = new ArrayDeque<Element>();
    Document dom;
    
    private CompilationUnit compilationUnit = null;
    private String fileName;
    
    // The only bad thing about the Eclipse visitors is that they do not support 
    // arguments; therefore you have to maintain a global while parsing subtrees.
    // Not my favorite arrangement!
    
    public StructureVisitor(CompilationUnit unit, Document dom, Element parent) {
    	this.fileName = "unknown";
    	this.compilationUnit = unit;
    	this.dom = dom;
    	this.parents.push(parent); 
    }    
    
    public void setFileName(String fileName) {
    	this.fileName = fileName;  
    }
    
	private Location createLocation(ASTNode tree) {
		return edu.umn.crisys.observability.preprocessing.ast.Util.createLocation(fileName, tree, compilationUnit);
	}
    
	
    // visit if statements, for loops, while loops, and do-while loops.
    public void addCdi(Statement stmt, ControlDecisionInfo.DecisionType dt) {
    	Element e = dom.createElement("control_decision");
    	Location loc = createLocation(stmt); 
    	
    	parents.peek().appendChild(e);
    	loc.toXml(dom, e);
    	Util.writeXmlTextElement(dom, e, "type", dt.toString());

    	Element ec = dom.createElement("children"); 
    	e.appendChild(ec);
    	parents.push(ec); 
    }

    
    public boolean visit(Assignment assign) {
    	Expression e = assign.getLeftHandSide();
    	// What are valid expression types for lhs expressions?
    	Location loc = createLocation(e); 
    	Element elem = null;
    	IdRef ref = null;
    	if (e instanceof SimpleName) {
    		SimpleName n = (SimpleName) e;
    		ref = edu.umn.crisys.observability.preprocessing.ast.Util.constructIdRef(n);
    	}
    	if (ref == null || ref.getRefType() == IdRef.RefType.FieldRef) {
    		elem = dom.createElement("field_assignment");
    		Util.writeXmlTextElement(dom,  elem, "expr", e.toString()); 
    	} else if (ref != null && ref.getRefType() == IdRef.RefType.LocalRef){
			elem = dom.createElement("local_assignment"); 
			Util.writeXmlTextElement(dom, elem, "expr", e.toString());
    	} else {
			elem = dom.createElement("static_field_assignment"); 
			Util.writeXmlTextElement(dom, elem, "expr", e.toString());
    	}
    	loc.toXml(dom, elem);
		parents.peek().appendChild(elem); 
    	return false;
    }
    
    public boolean visit(IfStatement ifStmt) {
    	
    	addCdi(ifStmt, 
    		ifStmt.getElseStatement() == null ? 
    				ControlDecisionInfo.DecisionType.IfThen :
    				ControlDecisionInfo.DecisionType.IfThenElse); 	
    	return true;
    }

    public void endVisit(IfStatement ifStmt) {
    	parents.pop();
    }
    
    public boolean visit(DoStatement dstmt) {
    	addCdi(dstmt, ControlDecisionInfo.DecisionType.DoWhile);
    	return true;
    }
    
    public void endVisit(DoStatement dstmt) {
    	parents.pop();
    }
    
    public boolean visit(ForStatement fstmt) {
    	addCdi(fstmt, ControlDecisionInfo.DecisionType.For);
    	return true;
    }
    
    public void endVisit(ForStatement fstmt) {
    	parents.pop(); 
    }
    
    public boolean visit(EnhancedForStatement fstmt) {
    	addCdi(fstmt, ControlDecisionInfo.DecisionType.For);
    	return true;
    }
    
    public void endVisit(EnhancedForStatement fstmt) {
    	parents.pop(); 
    }
    
    public boolean visit(WhileStatement wstmt) {
    	addCdi(wstmt, ControlDecisionInfo.DecisionType.While); 
    	return true;
    }    
    
    public void endVisit(WhileStatement wstmt) {
    	parents.pop(); 
    }	
    
   
    public void addMethod(MethodDeclaration m) {
    	Element e = dom.createElement("method");
    	Location loc = createLocation(m); 
    	
    	parents.peek().appendChild(e);
    	IMethodBinding mb = m.resolveBinding(); 
    	IMethod method = (IMethod) m.resolveBinding().getJavaElement();
    	String fullyQualifiedName = 
    			method.getDeclaringType().getFullyQualifiedName() + "." +  
    			method.getElementName();
    	Util.writeXmlTextElement(dom, e, "name", fullyQualifiedName);
    	loc.toXml(dom, e);
    	
    	Element ee = dom.createElement("parameter_types");
    	e.appendChild(ee);
        @SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>)m.parameters();
    	for (SingleVariableDeclaration p: parameters) {
            final IVariableBinding paramBinding = p.resolveBinding();
            final ITypeBinding paramTypeBinding = paramBinding.getType(); 
            final IJavaElement je = paramTypeBinding.getJavaElement();
//            String paramTypeName;
//            if (je != null && je instanceof IType) {
//            	paramTypeName = ((IType) je).getFullyQualifiedName();
//            } else {
//            	paramTypeName = paramTypeBinding.getQualifiedName(); 
//            }
    		Util.writeXmlTextElement(dom, ee, "parameter", paramTypeBinding.getBinaryName());
    	}

    	Element ec = dom.createElement("children"); 
    	e.appendChild(ec);
    	parents.push(ec); 
    }
    
    public boolean visit(MethodDeclaration m) {
    	addMethod(m);
    	return true;
    }
    
    public void endVisit(MethodDeclaration m) {
    	parents.pop();
    }
    
    public void addType(TypeDeclaration m) {
    	Element e = dom.createElement("type");
    	Location loc = createLocation(m); 
    	
    	parents.peek().appendChild(e);
    	Util.writeXmlTextElement(dom, e, "name", m.getName().getIdentifier());
    	loc.toXml(dom, e);
    	Element ec = dom.createElement("children"); 
    	e.appendChild(ec);
    	parents.push(ec); 
    }

    public boolean visit(TypeDeclaration c) {
    	addType(c);
    	return true;
    }
    
    public void endVisit(TypeDeclaration c) {
    	parents.pop();
    }
}    
    
