package edu.umn.crisys.observability.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.BitSet;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.crisys.observability.ILogger;

public class Util {

	public static void writeXmlTextElement(Document dom, Element parent, String eName, String str) {
		Element e = dom.createElement(eName);
		parent.appendChild(e);
		e.appendChild(dom.createTextNode(str));
	}

	public static String readFileToString(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String result;
		try {
			StringBuffer text = new StringBuffer();
			for (String line; (line = br.readLine()) != null;)
				text.append(line + "\n");
			result = text.toString();
		} finally {
			br.close();
		}
		return result;
	}

	public static String bitSetToSpaceDelimited(BitSet bitSet) {
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < bitSet.length(); i++) {
			if (bitSet.get(i)) {
				sb.append(" " + i);
			}
		}
		return sb.toString();
	}
	
	public static void writeStringToFile(File file, String toWrite) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file)));
		try {
			bw.append(toWrite);
			bw.append("\n");
		} finally {
			bw.close();
		}
	}

	public static void writeXmlFile(Document dom, File file, ILogger logger) {
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// send DOM to file
			ByteArrayOutputStream bs = new ByteArrayOutputStream();

			tr.transform(new DOMSource(dom), new StreamResult(bs));

			OutputStream outputStream = new FileOutputStream(file);
			bs.writeTo(outputStream);
			outputStream.close();

		} catch (TransformerException te) {
			logger.error("writeXmlFile::Error while transforming XML: " + te.getMessage());
		} catch (IOException ie) {
			logger.error("writeXmlFile::Error while writing file: " + ie.getMessage());
		}
	}
	

}
