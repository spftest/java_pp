package edu.umn.crisys.observability.preprocessing;

import edu.umn.crisys.observability.ILogger;
import edu.umn.crisys.observability.preprocessing.ast.IdRef;
import edu.umn.crisys.observability.preprocessing.ast.Location;
import edu.umn.crisys.observability.preprocessing.ast.condition.AndExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.BinExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.Expr;
import edu.umn.crisys.observability.preprocessing.ast.condition.LeafExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.NotExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.OrExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.RelExpr;
import edu.umn.crisys.observability.preprocessing.ast.condition.SkipLeafExpr;
//import edu.umn.crisys.observability.preprocessing.ast.condition.SkipLeafDecision;
import edu.umn.crisys.observability.preprocessing.ast.condition.TernaryExpr;
import gov.nasa.jpf.observability.struct.ConditionLocation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.*;

/**
 * PreprocessClassVisitor: class that performs condition preprocessing 
 * required by the OMCDC test generation and monitoring framework. 
 * It uses the javac API to perform the traversal.
 *
 *
 * @author Whalen
 * @version 0.5
 */

/* MWW 9/5/2013
 * 
 * The class of expressions that we are concerned with visiting are all 
 * expressions that can yield a Boolean value.  
 * 
 *  Upon reaching a Boolean expression, the algorithm detects whether it is inside an 
 *  existing decision.  If so, and it is a compound decision, we continue the 
 *  traversal.  If it is a leaf-level condition (e.g. a Boolean expression 
 *  that is not a compound operator), then I set up the condition information 
 *  as described in: 
 *  	http://www.umsec.umn.edu/publications/Efficient-Test-Coverage-Measurement-MCDC
 *  
 *  MWW: 9/26/2013
 *  Unfortunately, the provided Sun AST does not have types.  Therefore, I have to 
 *  maintain some sort of environment that tracks variable types.  Unfortunately,
 *  this means I have to track the types of all variables referenced by any class 
 *  to be analyzed.  For example: (a == b) may involve any kind of variables; if 
 *  it involves Booleans, it needs to be treated specially.
 *  
 *  Other than equalities and inequalities involving Booleans, is it possible 
 *  to determine Boolean arguments based on their context?  Even so, I would
 *  need the ability to determine the types of arguments.
 *  
 *  O.k., so: two passes?  First pass builds the type environment for the 
 *  second pass; it finds and records the member variables for each class.
 *  
 *  Second pass: maintain a local environment scope stack; as blocks are 
 *  added / removed, we push/pop from this stack.  Then, we use our 
 *  current classes to build up the stack information.
 *
 *  MWW: 11/6/2013
 *  Converted to Eclipse AST which contains types.  Need to retest, but otherwise,
 *  things look pretty good.  
 *  	Basic idea is that visitor will fall through to any expression.
 *  	If the expression is of a 'captured' subtype (prefix and binary expressions), 
 *  	then we will deal with it specially; otherwise, we will use the default 
 *  	expression visitor.  Since *all compound* boolean expressions are binary, 
 *  	prefix, or ifThenElseExpressions, we can capture all Boolean expressions 
 *  	by simply capturing these expression types (if the visitor framework works
 *  	as I expect).
 *  
*/ 


public class JavaDecisionVisitor extends ASTVisitor {
	
	private boolean debug = true;

    private List<Expr> decisions = new ArrayList<Expr>();
    
    private CompilationUnit compilationUnit = null;
    private ILogger log;
    
    // Environment env = new Environment(); 

    // The only bad thing about the Eclipse visitors is that they do not support 
    // arguments; therefore you have to maintain a global while parsing subtrees.
    // Not my favorite arrangement!
    
    private Deque<Expr> parentStack = new ArrayDeque<Expr>();
    private Expr decisionChild = null; 
    private String fileName = "unknown";
    private boolean isStmtCond = false;
    
    
    public JavaDecisionVisitor(CompilationUnit unit, ILogger log) {
    	this.log = log;
    	this.compilationUnit = unit;
    }
    
    private JavaDecisionVisitor(CompilationUnit unit, ILogger log, int decisionNumber, int currentStartLineNumber) {
    	this.log = log;
    	this.compilationUnit = unit;
    	this.decisionNumber = decisionNumber;
    	this.currentStartLineNumber = currentStartLineNumber;
    }
    
    public void setFileName(String fileName) {
    	this.fileName = fileName;  
    }
        
    public List<Expr> getDecisions() { return decisions; }
    
	// private boolean inDecision = false;
	private int decisionNumber = 0; 
	private int currentStartLineNumber = -1; 

	
	private Location createLocation(ASTNode tree) {
		return edu.umn.crisys.observability.preprocessing.ast.Util.createLocation(fileName, tree, compilationUnit);
	}

    public void printWarning(String op, Expression tree) {
    	Location loc = createLocation(tree);
    	log.warn(loc + " " + op + 
    			" over Boolean arguments is currently unsupported (Expr: " + 
    			tree.toString() + ")");
    }

	/** 
	creates a new condition location; uses currentStartLineNumber 
	to track where we are in terms of decision numbers per line number.
	@param tree 			node in the javac AST to set location 
	@param conditionNumber	condition number within decision; if zero, assume new decision
*/
	private void newDecision(Expr d) {
		int startLine = compilationUnit.getLineNumber(d.getJavaExpr().getStartPosition());
		if (startLine == currentStartLineNumber) {
			decisionNumber++; 
		} else {
			currentStartLineNumber = startLine;
			decisionNumber = 0;
		}
	}
	
    private ConditionLocation createConditionLocation(ASTNode tree) {
		long startLine = compilationUnit.getLineNumber(tree.getStartPosition());
		
		if (fileName == null) {
			IJavaElement icu = compilationUnit.getJavaElement();
			if (icu != null) {
				fileName = icu.getElementName();
			} else {
				throw new Error("In createConditionLocation: unable to find file name.\n");
			}
		}
		// we will fill in the proper condition location when we mark the DecisionNode tree. 
		ConditionLocation loc = 
				new ConditionLocation(fileName, (int)startLine, decisionNumber, -1);
		return loc;
    }
    
    public ITypeBinding getTypeKind(Expression tree) {
    	ITypeBinding binding = tree.resolveTypeBinding();
    	if (binding == null) {
    		System.out.println("ITypeBinding is null in getTypeKind for expression " + tree.toString());
    		return null;
    	} else {
    		return binding;
    	}
    }

    boolean isBoolType(Expression tree) {
    	ITypeBinding type = getTypeKind(tree);
    	return (type != null && 
    			type.getName().equals("boolean"));
    }
    
    boolean inDecision() {
    	return !this.parentStack.isEmpty(); 
    }
    
    void pushParent(Expr tree) {
    	storeCondition(tree);
		this.parentStack.push(tree);
	}
    
    void storeCondition(Expr node) {
    	if (!inDecision()) {
    		newDecision(node);
    		this.decisions.add(node);
    		this.decisionChild = null;
    	} else {
    		node.setParent(this.parentStack.peek());
    		this.decisionChild = node;
    	}
    }
    
    void popParent() {
		this.decisionChild = this.parentStack.pop(); 
    }

    //////////////////////////////////////////////////////
    //
    // This class grabs the referenced ids that are in
    // scope for the current condition.  Note that if there
    // are nested decisions, we want to short circuit the 
    // search; any ids owned by the nested decision should
    // be mapped to that decision rather than the
    // enclosing condition.
    //
    //////////////////////////////////////////////////////
    
    public class ConditionVarVisitor extends ASTVisitor {
        //Environment env;
        List<IdRef> refs = new ArrayList<IdRef>();
        List<String> methodRefs = new ArrayList<String>(); 
        boolean inMethodCall = false; 
        
        public ConditionVarVisitor() { // Environment env) {
        	//this.env = env;
        }    
        
        public void visitName(Name tree) {
        	// Don't want to 'capture' boolean parameters to methods.
        	if (inMethodCall && isBoolType(tree)) {
        		return;
        	}        	
        	IdRef ref = edu.umn.crisys.observability.preprocessing.ast.Util.constructIdRef(tree);
        	if (ref != null) {
            	refs.add(ref);
        	}
        }

        public boolean visit(SimpleName tree) {
        	visitName(tree);
        	return true;
        }
        
        public boolean visit(QualifiedName tree) {
        	visitName(tree);
        	return true;
        }
       
    	// don't capture nested Boolean expressions used as 
        // parameters to methods; these will be their own decisions.

    	// ConditionalExpression always has a Boolean component!
        // Parameters to functions...f
        public boolean visit(ConditionalExpression tree) { return false; }
        public boolean visit(MethodInvocation tree) 
           { inMethodCall = true;  
             methodRefs.add(tree.getName().getIdentifier());
             return !isBoolType(tree); }
        public boolean visit(SuperMethodInvocation tree) 
        	{ inMethodCall = true; 
        	  methodRefs.add(tree.getName().getIdentifier()); 
        	  return !isBoolType(tree); }
        public boolean visit(InstanceofExpression tree) { return !isBoolType(tree); }
        public boolean visit(BooleanLiteral tree) { return !isBoolType(tree); }
        public boolean visit(ArrayAccess tree) { return !isBoolType(tree); }
        public boolean visit(CastExpression tree) { return !isBoolType(tree); }
        public boolean visit(FieldAccess tree) { return !isBoolType(tree); }
        public boolean visit(SuperFieldAccess tree) { return !isBoolType(tree); }
        
        public List<IdRef> getIdRefs() {return refs;}
        public List<String> getMethodRefs() { return methodRefs; }
    }    

    public List<Expr> gatherChildDecisions(Expression tree) {
		JavaDecisionVisitor childVisitor = 
				new JavaDecisionVisitor(compilationUnit, log, decisionNumber, currentStartLineNumber);
		childVisitor.setFileName(fileName);
		JavaSkipParentVisitor spv = new JavaSkipParentVisitor(childVisitor);
		tree.accept(spv);
		
		// reset the decision number after the traversal.
		this.currentStartLineNumber = childVisitor.currentStartLineNumber; 
		this.decisionNumber = childVisitor.decisionNumber; 
		
		List<Expr> decisions = childVisitor.getDecisions(); 
		
		for (Expr node : decisions) {
			node.mark();
		}

		return decisions;
    }
    
    /*    
     * 	Here are my constraints:
     * 	I want to construct the decision AST.  The easiest way to do this is 
     *  to pass in the parent DecisionTree node as an argument...but then 
     *  we can't construct the children first, and we don't know whether
     *  we are a 'top level' node or not.
     *  
     *  The decision node needs to be added to the list of decision nodes.
     *  
     * 
     * So the algorithm is as follows: 
     * When we parse an expression, if it is of boolean type and the 
     * decisionRoot is null, we are at the root of a new decision.  We 
     * want to explicitly walk the expression tree from here until we reach 
     * some leaf-level expession.
     * 
    */

    public void createLeafCondition(Expression tree, boolean isTrivialCond) {
    	ConditionVarVisitor cvv = new ConditionVarVisitor();
		tree.accept(cvv);
		Expr node = new LeafExpr(tree, createConditionLocation(tree), 
							this.isStmtCond, isTrivialCond, cvv.getIdRefs(), cvv.getMethodRefs(),
							gatherChildDecisions(tree));
		storeCondition(node); 
    }

    public void createSkipLeafCondition(Expression tree) {
		Expr node = new SkipLeafExpr(tree);
		storeCondition(node);    	
    }

    /* 
     * We add some logic here to remove 'nuisance' decisions, e.g.:
     * 	x = false;
     *  x.aMethod(); 	// where aMethod returns true/false
     *  x = y		 	// where y is a Boolean variable.
     * 
     * We do so by not storing conditions unless they have some structure, 
     * that is, an operator of some kind.  So, we ignore 'simple' decisions.
     *  
     */
    public boolean isComplexExpression(Expression tree) {
    	return (tree instanceof CastExpression ||
    			tree instanceof ConditionalExpression ||
    			tree instanceof InfixExpression || 
    			tree instanceof InstanceofExpression ||
    			tree instanceof PostfixExpression || 
    			(tree instanceof ParenthesizedExpression && 
    				isComplexExpression(((ParenthesizedExpression)tree).getExpression())) ||
    			tree instanceof PrefixExpression  
    			//tree instanceof MethodInvocation || 
    			//tree instanceof SuperMethodInvocation
    			);
    }
    
    
    public boolean argumentOperator() {
    	return true;
    }
    
    private boolean UnderOpInternal(Expr dec) {
    	if (dec == null) 
    		return false;
    	else if (dec instanceof BinExpr ||
    			 dec instanceof NotExpr)
    		return true;
    	else
    		return UnderOpInternal(dec.getParent());
    }
    
    private boolean UnderOp(Expression tree) {
    	Expr parent = this.parentStack.peek();
    	if (parent instanceof TernaryExpr) {
    		ConditionalExpression ce = (ConditionalExpression)parent.getJavaExpr();
    		if (tree == ce.getExpression()) {
    			return true;
    		}
    		else return UnderOpInternal(parent);
    	} else return UnderOpInternal(parent);
    }
    
    public boolean visitExpr(Expression tree) {
    	assert (tree != null);
    	if (debug)
    		System.out.println("Visiting Expression:" + tree.toString());
    	
    	if (isBoolType(tree)) {
    		boolean isNonTrivialExpr = (UnderOp(tree) || isComplexExpression(tree) || this.isStmtCond); 
    		createLeafCondition(tree, !isNonTrivialExpr);
    	}
		return true;
    }
    
    
    public boolean visit(InfixExpression tree) {
    	if (debug)
    		System.out.println("Visiting Binary Tree:" + tree.toString());
    	assert(tree != null);
    	
		BinExpr node = null; 
		String op = tree.getOperator().toString();
		
		// Only want Boolean subexpressions for structure.
		if (isBoolType(tree) && 
			isBoolType(tree.getLeftOperand()) && 
			isBoolType(tree.getRightOperand())) { 
			switch(op) {  
				case "&&" : 
					node = new AndExpr(tree); break;
				case "||" : 
					node = new OrExpr(tree);  break;
				case "==" : 
				case "!=" : 
				case "^" : 
					node = new RelExpr(tree, op, createConditionLocation(tree), this.isStmtCond); break;
				default: 
					log.warn("Unsupported Boolean infix operator: '" + op + "' encountered in Java program.  Translated as relational operator.");
					node = new RelExpr(tree, op, createConditionLocation(tree), this.isStmtCond); break;
					//throw new PreprocessingError("Unhandled infix operator: " + op + " while visiting Java program");
			}
			
			pushParent(node);
	    	try {
    			tree.getLeftOperand().accept(this);
       		    node.setLhs(this.decisionChild); 
       		    tree.getRightOperand().accept(this);
    			node.setRhs(this.decisionChild);
    		}
	    	finally {
	    		popParent();
	    	}
			return false;
		}
		return visitExpr(tree);
    }


    public boolean visit(PrefixExpression tree) {
    	if (debug)
    		System.out.println("Visiting Unary Tree:" + tree.toString());

    	switch (tree.getOperator().toString()) {
    	case "!": {
    		NotExpr node = new NotExpr(tree);
    		pushParent(node); 
    		try {
	    		tree.getOperand().accept(this);
	    		node.setChild(this.decisionChild);
    		}
	    	finally {
	    		popParent();
	    	}
    		return false;
    	}
    	default : {
    		if (isBoolType(tree)) {
    			createLeafCondition(tree, false);
    			return false;
    		}
    		else return true;
    	}
    	}
    }

    public boolean visit(ConditionalExpression tree) {
    	if (isBoolType(tree)) {
			TernaryExpr node = new TernaryExpr(tree);
			// storeCondition(node); 
			pushParent(node);
			
			// need to save off the previous version of isStmtCond; 
			boolean oldStmtCond = this.isStmtCond;
			
			try {
				this.isStmtCond = true;
				tree.getExpression().accept(this);
				node.setCond(this.decisionChild);
				this.isStmtCond = oldStmtCond;
				
				tree.getThenExpression().accept(this);
				node.setThen(this.decisionChild);

				tree.getElseExpression().accept(this); 
				node.setElse(this.decisionChild); 
			} finally {
				this.isStmtCond = oldStmtCond;
				popParent();
			}
    	} else {
			boolean oldStmtCond = this.isStmtCond;
			this.isStmtCond = true;
			tree.getExpression().accept(this);
			this.isStmtCond = oldStmtCond; 
			
			tree.getThenExpression().accept(this);
			tree.getElseExpression().accept(this);
    	} 
		return false;
    }

    // ignore parenthesis.  Continue traversal.
    public boolean visit(ParenthesizedExpression tree) { 
    	return true; 
    }
    
    // callbacks to basic expression.  Curse you, java overloaded disambiguation semantics!
    // The first two can cause a new decision context. 
    // the next bunch can return boolean values
    public boolean visit(MethodInvocation tree) { return visitExpr(tree); }
    public boolean visit(SuperMethodInvocation tree) { return visitExpr(tree); }
    public boolean visit(InstanceofExpression tree) { return visitExpr(tree); }
    public boolean visit(BooleanLiteral tree) { return visitExpr(tree); }
    public boolean visit(ArrayAccess tree) { return visitExpr(tree); }
    public boolean visit(CastExpression tree) { return visitExpr(tree); }
    public boolean visit(FieldAccess tree) { return visitExpr(tree); }
    public boolean visit(SimpleName tree) { return visitExpr(tree); }
    public boolean visit(QualifiedName tree) { return visitExpr(tree); }
    public boolean visit(SuperFieldAccess tree) { return visitExpr(tree); }
    
    
    public boolean visit(ArrayCreation tree) { return visitExpr(tree); }
    public boolean visit(ArrayInitializer tree) { return visitExpr(tree); }
    public boolean visit(CharacterLiteral tree) { return visitExpr(tree); }
    public boolean visit(ClassInstanceCreation tree) { return visitExpr(tree); }
    public boolean visit(NullLiteral tree) { return visitExpr(tree); }
    public boolean visit(NumberLiteral tree) { return visitExpr(tree); }
    public boolean visit(PostfixExpression tree) { return visitExpr(tree); }
    public boolean visit(StringLiteral tree) { return visitExpr(tree); }
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

    // callbacks to places where we only want to visit portions of the tree.
    public boolean visit(Assignment t) { t.getRightHandSide().accept(this); return false; }

    // setting context for statements.
    private void visitStmtCond(Expression cond) {
    	this.isStmtCond = true;
    	cond.accept(this);
    	this.isStmtCond = false;
    }
    
    public boolean visit(DoStatement t) {
    	visitStmtCond(t.getExpression());
    	t.getBody().accept(this); 
    	return false;
    }
    
    public boolean visit (EnhancedForStatement t) {
    	// handle enhanced for statements specially...
    	createLeafCondition(t.getExpression(), true);
    	t.getBody().accept(this); 
    	return false;
    }
    
    public boolean visit (ForStatement t) {
    	for (Object i: t.initializers()) {
    		((ASTNode)i).accept(this);
    	}
    	visitStmtCond(t.getExpression());
    	for (Object i: t.updaters()) {
    		((ASTNode)i).accept(this);
    	}
    	t.getBody().accept(this);
    	return false;
    }
    
    // For Neha: 8/28/2014
    // We want to skip the top-level expression for expression statements if
    // they are method invocations whose boolean result is not used.
    //
    // NOTE: assignments are expressions, not statements, so this horks up
    // assignments unless we look specifically for method calls.
    
    public boolean visit(ExpressionStatement t) {
    	if (t.getExpression() instanceof MethodInvocation || 
    		t.getExpression() instanceof SuperMethodInvocation) {
    		JavaSkipParentVisitor skipParent = new JavaSkipParentVisitor(this);
    		skipParent.visitExpr(t.getExpression());
    		return false;
    	}
    	return true;
    }
    
    public boolean visit (IfStatement t) {
    	visitStmtCond(t.getExpression());
    	t.getThenStatement().accept(this);
    	if (t.getElseStatement() != null) {
    		t.getElseStatement().accept(this);
    	}
    	return false;
    }
    
    public boolean visit (WhileStatement t) {
    	visitStmtCond(t.getExpression());
    	t.getBody().accept(this);
    	return false;
    }
    
    
    public boolean visit(VariableDeclarationFragment t) { 
    	// env.add(t.getName().getIdentifier());
    	if (t.getInitializer() != null) {
    		t.getInitializer().accept(this); 
    	} 
    	return false; 
    }
    	

    public boolean visit(MethodDeclaration t) { 
    	if (debug)
    		System.out.println("At MethodDeclaration: " + t.toString());

    	if (t.getBody() != null)
    		t.getBody().accept(this); 
    	return false; 
    }
    
}    
    
