Downloading:
============
To run a binary version of the tool, please visit the releases page: 
https://github.com/spftest/java_pp/releases

Building:
=========
I have checked in the Eclipse .project file for building this plugin, but the
included libraries are specific to Eclipse 4.2 (Juno) so it may not work for 
other versions of Eclipse.

To update the libraries, you need to have versions of the following JAR files, 
which may have different version numbers depending on your version of Eclipse:

\eclipse\plugins\org.eclipse.ui_3.103.0.v20120521-2329.jar
\eclipse\plugins\org.eclipse.swt_3.100.0.v4233d.jar
\eclipse\plugins\org.eclipse.swt.win32.win32.x86_64_3.100.0.v4233d.jar
\eclipse\plugins\org.eclipse.jface_3.8.0.v20120521-2329.jar
\eclipse\plugins\org.eclipse.core.commands_3.6.1.v20120521-2329.jar
\eclipse\plugins\org.eclipse.ui.workbench_3.103.0.v20120530-1824.jar
\eclipse\plugins\org.eclipse.e4.ui.workbench3_0.12.0.v20120521-2329.jar
\eclipse\plugins\org.eclipse.core.runtime_3.8.0.v20120521-2346.jar
\eclipse\plugins\org.eclipse.osgi_3.8.0.v20120529-1548.jar
\eclipse\plugins\org.eclipse.equinox.common_3.6.100.v20120522-1841.jar
\eclipse\plugins\org.eclipse.core.jobs_3.5.200.v20120521-2346.jar
\eclipse\plugins\org.eclipse.core.runtime.compatibility.registry_3.5.100.v20120521-2346\runtime_registry_compatibility.jar
\eclipse\plugins\org.eclipse.equinox.registry_3.5.200.v20120522-1841.jar
\eclipse\plugins\org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar
\eclipse\plugins\org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar
\eclipse\plugins\org.eclipse.equinox.app_1.3.100.v20120522-1841.jar
\eclipse\plugins\org.eclipse.jdt_3.8.0.v201206081400.jar
\eclipse\plugins\org.eclipse.jdt.core_3.8.1.v20120531-0637.jar
\eclipse\plugins\org.eclipse.jdt.compiler.apt_1.0.500.v20120522-1651.jar
\eclipse\plugins\org.eclipse.jdt.compiler.tool_1.0.101.v20120522-1651.jar
\eclipse\plugins\org.eclipse.jdt.core.manipulation_1.5.0.v20120523-1543.jar
\eclipse\plugins\org.eclipse.jface.text_3.8.0.v20120531-0600.jar
\eclipse\plugins\org.eclipse.text_3.5.200.v20120523-1310.jar
\eclipse\plugins\org.eclipse.core.resources_3.8.0.v20120522-2034.jar
\eclipse\plugins\org.eclipse.ui.console_3.5.100.v20120521-2012.jar
 
To create a new project from scratch, create an "Eclipse Plugin" project 
in Eclipse, check "Contains User Interface Elements", replace the 
autogenerated Activator and SampleHandler classes with the ones in the 
repository, and update the dependencies to include the JAR files described
above.

Exporting to Eclipse
====================

To export the project as a JAR file that is usable by Eclipse, choose the 
plugin.xml file, then choose the "Export Wizard" link from the "Overview" 
tab.  The default options should be fine, and choose to import into a 
directory of your choosing.  This will create a "plugins" subdirectory 
underneath your directory.  Take any files from this directory and place 
into Eclipse.

Happy Building!

Mike

Usage:
======
Once imported into Eclipse,  
You should be able to select any file, package, or package fragment for 
projects that 1.) do not have errors and 2.) have the Java profile and 
choose "Observability Testing | Preprocess" to run the tools.  For 
each java file foo.java in the selection, the tool will generate a 
foo.java.xml file in the same location with the preprocessor information.  
