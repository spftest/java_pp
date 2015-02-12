package edu.umn.crisys.observability.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.w3c.dom.Document;

import edu.umn.crisys.observability.EclipseLogger;
import edu.umn.crisys.observability.preprocessing.Preprocessor;
import edu.umn.crisys.observability.preprocessing.Util;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	// necessary to be global to support workspace job.
	List<ICompilationUnit> compilationUnits; 
	
	public SampleHandler() {
	}

	public ISelection getCurrentSelection(IWorkbenchWindow window) {
        ISelectionService selectionService =  window.getSelectionService();    
        ISelection selection = selectionService.getSelection();    
        return selection;
	}
	
	public void showSelection(EclipseLogger log, ISelection selection) {
		log.info(" (" + selection.getClass().getName() + ")");
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			showItems(log, ss.toArray());
		}
		if (selection instanceof ITextSelection) {
			ITextSelection ts  = (ITextSelection) selection;
			showText(log, ts.getText());
		}
		if (selection instanceof IMarkSelection) {
			IMarkSelection ms = (IMarkSelection) selection;
			try {
			    showText(log, ms.getDocument().get(ms.getOffset(), ms.getLength()));
			} catch (BadLocationException ble) { }
		}
	}
	
	private void showItems(EclipseLogger log, Object[] items) {
		for (Object o: items) {
			log.info("Selected object: " + o);
		}
	}
	
	private void showText(EclipseLogger log, String text) {
		log.info("Selected text: " + text);
	}
	
	private void getCUCompilationUnits(EclipseLogger log, ICompilationUnit unit, List<ICompilationUnit> compilationUnits) throws JavaModelException {
		log.info("Compilation Unit: " + unit.getElementName());
		compilationUnits.add(unit); 
	}
	
	private void getPFCompilationUnits(EclipseLogger log, IPackageFragment obj, List<ICompilationUnit> compilationUnits) throws JavaModelException {
    	if (obj.getKind() == IPackageFragmentRoot.K_SOURCE) {
            log.info("Package " + obj.getElementName());
            for (ICompilationUnit unit: obj.getCompilationUnits()) {
            	getCUCompilationUnits(log, unit, compilationUnits);
            }
    	}
	}
	
	private void getJPCompilationUnits(EclipseLogger log, IJavaProject obj, List<ICompilationUnit> compilationUnits) throws JavaModelException {
		IPackageFragment[] packages = obj.getPackageFragments();
	    for (IPackageFragment mypackage : packages) {
	    	getPFCompilationUnits(log, mypackage, compilationUnits);
	    }
	}
	
	private void getProjectCompilationUnits(EclipseLogger log, IProject project, List<ICompilationUnit> compilationUnits) throws CoreException {
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			getJPCompilationUnits(log, javaProject, compilationUnits);
		}	
	}
	
	private void getJECompilationUnits(EclipseLogger log, IJavaElement obj, List<ICompilationUnit> compilationUnits) throws CoreException, JavaModelException {
		if (obj instanceof IProject) {
			IProject project = (IProject)obj;
			getProjectCompilationUnits(log, project, compilationUnits); 
		} else if (obj instanceof IPackageFragment) {
			IPackageFragment fragment = (IPackageFragment)obj;
			getPFCompilationUnits(log, fragment, compilationUnits);
		} else if (obj instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) obj;
			getCUCompilationUnits(log, unit, compilationUnits);
		} else if (obj instanceof IPackageFragmentRoot) {
				// IPackageFragmentRoot root = (IPackageFragmentRoot)obj;
				getPERCompilationUnits(log, (IPackageFragmentRoot)obj, compilationUnits) ; 
		} else {
			log.error("Selection is not project, package, or compilation unit");
		}
	}
	
	private void getPERCompilationUnits(EclipseLogger log, IPackageFragmentRoot root, List<ICompilationUnit> compilationUnits) throws CoreException {
		IJavaElement [] childArray = root.getChildren();
		for (IJavaElement child: childArray) {
			getJECompilationUnits(log, child, compilationUnits);
		}
	}
	
	private void getCompilationUnits(EclipseLogger log, ISelection selection, List<ICompilationUnit> compilationUnits) throws CoreException, JavaModelException {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object obj;
			@SuppressWarnings("rawtypes")
			Iterator it;
			for (it = ssel.iterator(); it.hasNext(); ) {
				obj = it.next();
				if (obj instanceof IProject) {
					getProjectCompilationUnits(log, (IProject)obj, compilationUnits); 
				}
				if (obj instanceof IJavaElement) {
					getJECompilationUnits(log, (IJavaElement)obj, compilationUnits);
				}
			}
		}
	}
	

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelection sel = getCurrentSelection(window);
		final EclipseLogger log = new EclipseLogger(EclipseLogger.INFO, "Observability Preprocessor", window);
		log.info("Preprocessing java project or compilation unit.");
		// log.info("Have selection.\n");

		try {
			compilationUnits = new ArrayList<ICompilationUnit>();
			getCompilationUnits(log, sel, compilationUnits);
			
			if (compilationUnits.isEmpty()) {
				log.error("Selection does not map to a compilation unit: [" + sel + "] \n");
				return null; 
			}
		} catch (CoreException e) {
			log.error(e);
			throw new ExecutionException(e.toString());
		} 
		
		WorkspaceJob job = new WorkspaceJob("AADL Job") {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException 
			{
				for (ICompilationUnit compilationUnit : compilationUnits) {
					//log.info("Getting project for compilation unit: [" + compilationUnit + "]");
					IJavaProject project = compilationUnit.getJavaProject();
					Preprocessor pp = new Preprocessor(log); 
					pp.processJavaCompilationUnit(project, compilationUnit); 
					try {
						Document dom = pp.createXmlDocument(); 
						IResource rsc = compilationUnit.getCorrespondingResource();
						IPath xmlPath = rsc.getLocation().addFileExtension("xml");
						//log.info("Writing xml to file: " + xmlPath.toString());
						Util.writeXmlFile(dom, xmlPath.toFile(), log);
					} catch (JavaModelException jme) { 
						log.error("Error creating IResource from IJavaProject: " + jme.toString());
					
					} catch (ParserConfigurationException pce) {
						log.error("Error creating parser configuration: " + pce.toString());
					}
					log.info("Preprocessing of file: " + compilationUnit.getElementName() + " completed.\n");
				}
				return Status.OK_STATUS;
			};
		};
		
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
		
		return null;
	}
}
