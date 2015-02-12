package edu.umn.crisys.observability.preprocessing;

import edu.umn.crisys.observability.ConsoleLogger;
import edu.umn.crisys.observability.ILogger;
import edu.umn.crisys.observability.preprocessing.ast.condition.Expr;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Preprocessor {

	ILogger logger;
	
	public Preprocessor(ILogger logger) {
		this.logger = logger;
	}

	private List<Expr> decisions;
	//private List<ControlDecisionInfo> decisionInfo; 
	private CompilationUnit compilationUnit; 
	private String fileName;
	
	public void parse(IJavaProject prj, ICompilationUnit icu) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setProject(prj); 
        parser.setSource(icu);
        parser.setResolveBindings(true);
        compilationUnit = (CompilationUnit) parser.createAST(null); // parse		
	}
	
	public void processJavaCompilationUnit(IJavaProject prj, ICompilationUnit icu) {
		parse(prj, icu); 
		JavaDecisionVisitor pp = new JavaDecisionVisitor(
				compilationUnit, logger);
		fileName = compilationUnit.getJavaElement().getElementName();
		pp.setFileName(fileName);
		compilationUnit.accept(pp);
		
		this.decisions = pp.getDecisions();
		
		for (Expr node : decisions) {
			node.mark();
		}
	}
	
	
	/**
	 * Assumes you have previously run processJavaFile or processJavaCompilationUnit
	 */
	public Document createXmlDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// create instance of DOM
		Document dom = db.newDocument();

		dom.appendChild(dom.createComment("Open source license goes here\n"));
		
		// create the root element
		Element root = dom.createElement("OMCDC_file_info");
		dom.appendChild(root);
		Element child;
		Util.writeXmlTextElement(dom, root, "file_name", fileName);
		
		child = dom.createElement("condition_list");
		root.appendChild(child);
		
		// write XML for decisions here.  This should probably be a 
		// function that operates over the tree, rather than this "gathering" 
		// Then this additional interface would not be required.  
		
		DecisionXmlWriter visitor = new DecisionXmlWriter(dom);
		for (Expr node : decisions) {
			visitor.printDecision(node, child);
		}
		
		child = dom.createElement("method_structures");
		root.appendChild(child);
		
		// adds structural information to the XML file.
		// See StructuralVisitorEclipse for more information.
		StructureVisitor sve = new StructureVisitor(compilationUnit, dom, child);
		compilationUnit.accept(sve);
		
//		for (ControlDecisionInfo cdi: decisionInfo) {
//			cdi.toXml(dom, child);
// 		}
		return dom;
	}

	public void writeXmlDocumentToFile(Document dom, String fileName) {
		File file = new File(fileName);
		Util.writeXmlFile(dom, file, logger);
	}
	
	public void processJavaFile(File toVisit) {
		try {
			String source = Util.readFileToString(toVisit);
			org.eclipse.jface.text.Document document = new org.eclipse.jface.text.Document(source);
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setEnvironment(null, null, null, true);
			parser.setUnitName(toVisit.getName());
			parser.setResolveBindings(true);
			parser.setSource(document.get().toCharArray());
			compilationUnit = (CompilationUnit) parser.createAST(null);
			JavaDecisionVisitor pp = new JavaDecisionVisitor(
					compilationUnit, logger);
			pp.setFileName(toVisit.getName());
			compilationUnit.accept(pp);
			this.decisions = pp.getDecisions();
			
			for (Expr node : decisions) {
				node.mark();
			}
		} catch (IOException io) {
			System.out.println("Java IO Exception: " + io.toString());
		}
	}

	public void runIt(String oldFileName) {
		try {
			File oldf = new File(oldFileName);
			processJavaFile(oldf);
			Document dom = createXmlDocument();
			writeXmlDocumentToFile(dom, oldFileName + ".xml");
		} catch (ParserConfigurationException pce) {
			System.out.println("Error creating XML document: " + pce.toString());
		}
	}

	public static void main(String[] args) {
		Preprocessor f = new Preprocessor(new ConsoleLogger(0));
		f.runIt(args[0]);
	}
}
