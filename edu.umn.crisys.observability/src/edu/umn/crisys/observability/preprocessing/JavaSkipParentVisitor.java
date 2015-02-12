package edu.umn.crisys.observability.preprocessing;

import org.eclipse.jdt.core.dom.*;

/**
 * JavaSkipParentVisitor: Stupid class to ignore the current top of the Expression AST and
 * visit all children using a different visitor.  There *must* be a better way to do this.
 *
 *
 * @author Whalen
 * @version 0.5
 */



public class JavaSkipParentVisitor extends ASTVisitor {
	
	private boolean inParent = true;

	ASTVisitor childVisitor;  
    
    public JavaSkipParentVisitor(ASTVisitor childVisitor) {
    	this.childVisitor = childVisitor; 
    }
    
    public boolean visitExpr(Expression e) {
    	if (inParent) {
    		inParent = false;
    		return true;
    	} else {
    		e.accept(childVisitor);
    		return false;
    	}
    }
    
    public boolean visit(InfixExpression tree) { return visitExpr(tree); }
    public boolean visit(PrefixExpression tree) { return visitExpr(tree); }
    public boolean visit(ConditionalExpression tree) { return visitExpr(tree); }
    public boolean visit(ParenthesizedExpression tree) { return visitExpr(tree); }
    public boolean visit(ArrayAccess tree) { return visitExpr(tree); }
    public boolean visit(ArrayCreation tree) { return visitExpr(tree); }
    public boolean visit(ArrayInitializer tree) { return visitExpr(tree); }
    public boolean visit(BooleanLiteral tree) { return visitExpr(tree); }
    public boolean visit(CastExpression tree) { return visitExpr(tree); }
    public boolean visit(CharacterLiteral tree) { return visitExpr(tree); }
    public boolean visit(ClassInstanceCreation tree) { return visitExpr(tree); }
    public boolean visit(FieldAccess tree) { return visitExpr(tree); }
    public boolean visit(InstanceofExpression tree) { return visitExpr(tree); }
    public boolean visit(MethodInvocation tree) { 
    	for (Object e: tree.arguments()) {
    		((Expression)e).accept(childVisitor);
    	}
    	return false;
    }
    
    public boolean visit(SimpleName tree) { return visitExpr(tree); }
    public boolean visit(QualifiedName tree) { return visitExpr(tree); }
    public boolean visit(NullLiteral tree) { return visitExpr(tree); }
    public boolean visit(NumberLiteral tree) { return visitExpr(tree); }
    public boolean visit(PostfixExpression tree) { return visitExpr(tree); }
    public boolean visit(StringLiteral tree) { return visitExpr(tree); }
    public boolean visit(SuperFieldAccess tree) { return visitExpr(tree); }
    public boolean visit(SuperMethodInvocation tree) { return visitExpr(tree); }
    public boolean visit(ThisExpression tree) { return visitExpr(tree); }
    public boolean visit(VariableDeclarationExpression tree) { return visitExpr(tree); }
    
    // callbacks to places where we want to short-circuit search.
    public boolean visit(AnnotationTypeDeclaration t) { return false;} 
    public boolean visit(AnnotationTypeMemberDeclaration t) { return false; }
    public boolean visit(MarkerAnnotation t) { return false; }
    public boolean visit(MethodRefParameter t) { return false; }
    public boolean visit(Modifier t) { return false; }
    public boolean visit(NormalAnnotation t) { return false; }
    public boolean visit(ParameterizedType t) { return false; }
    public boolean visit(PrimitiveType t) { return false; }
    public boolean visit(SingleMemberAnnotation t) { return false; }
    public boolean visit(SingleVariableDeclaration t) { return false; }
//    public boolean visit(TypeDeclaration t) { return false; }
    public boolean visit(TypeLiteral t) { return false; }
    public boolean visit(TypeParameter t) { return false; }
    public boolean visit(UnionType t) { return false; }
    public boolean visit(WildcardType t) { return false; }
    
}    
    
