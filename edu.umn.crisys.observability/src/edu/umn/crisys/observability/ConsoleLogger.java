/*
Copyright (c) 2011, Rockwell Collins.
Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).

Permission is hereby granted, free of charge, to any person obtaining a copy of this data, 
including any software or models in source or binary form, as well as any drawings, specifications, 
and documentation (collectively "the Data"), to deal in the Data without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Data.

THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
*/

package edu.umn.crisys.observability;

import java.io.PrintStream;

public class ConsoleLogger implements ILogger {
	
	private int verbosity;
	private PrintStream out;

	public ConsoleLogger(int verbosity) {
		this.verbosity = verbosity;
		this.out = System.out;
	}

	
	private void log(int priority, String msg) {
		if (verbosity >= priority) {
			if (priority == ERROR) msg = "*** ERROR: " + msg;
			if (priority == WARN)  msg = "  WARNING: "  + msg;
			out.println(msg);
		}
	}

	public void status(String msg) { log(STATUS, msg); }
	public void error(String msg)  { log(ERROR, msg); }
	public void warn(String msg)   { log(WARN, msg); }
	public void info(String msg)   { log(INFO, msg); }
	public void status(Object obj) { log(STATUS, String.valueOf(obj)); }
	public void error(Object obj)  { log(ERROR, String.valueOf(obj)); }
	public void warn(Object obj)   { log(WARN, String.valueOf(obj)); }
	public void info(Object obj)   { log(INFO, String.valueOf(obj)); }
}
