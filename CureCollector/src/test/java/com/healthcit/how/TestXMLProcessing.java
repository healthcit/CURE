/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.healthcit.how.models.QuestionnaireForm;

public class TestXMLProcessing {

	public static final String END_ANS_TAG = "</answer>";
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	public static final String NAME_SPACE = "xmlns=\"http://www.healthcit.com/FormDataModel\"";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TestXMLProcessing.processXML("");
		//TestXMLProcessing.testRegExpReplace();
	}

	public static void processXML(String xform) {

        String answerXml = "";
        //create new string with replaced data.
        StringBuffer newXml = new StringBuffer();

		final String XML_INPUT = readFromFile("/temp/xformData.xform");

		//get the <answer...>...</answer> from the xml.
		Pattern parentPat = Pattern.compile("<answer.*?>\\w+\\s[^<]+</answer>");
		Matcher parentMatch = parentPat.matcher(XML_INPUT);
		boolean parentStatus = parentMatch.find();

		while(parentStatus) {
			String parentGroup = parentMatch.group();
	        //System.out.println("parentGroup: " + parentStatus + ": " + parentGroup);

	        //find the start tag from parent group.
	        Pattern startTagPat = Pattern.compile("\\<answer\\s\\w.*\\>\\w");
	        Matcher startTagMat = startTagPat.matcher(parentGroup);
	        startTagMat.find();
	        String startTag = startTagMat.group().replaceFirst("\\>\\w", "\\>");
	        //System.out.println("startTag: " + startTag);

	        //get the data from parent group.
	        Pattern dataPat = Pattern.compile("\\>\\w.*[(?!<*)]");
	        Matcher dataMat = dataPat.matcher(parentGroup);
	        boolean findStatus2 = dataMat.find();
	        String dataGroup = dataMat.group();
	        //System.out.println("dataGroup: " + findStatus2 + " " + dataGroup);

	        //split the data into an array.
	        Pattern splitDataPat = Pattern.compile("[\\s]+");
	        String[] result = splitDataPat.split(dataGroup);

	        answerXml = "";

	        //build the answer xml and clean < and > brackets from the data.
	        for (int i=0; i<result.length; i++) {
	        	answerXml = answerXml + startTag + result[i].replaceAll("\\<*\\>*", "") + END_ANS_TAG;
	        }
	        parentMatch.appendReplacement(newXml, answerXml);
	        parentStatus = parentMatch.find();
		}

        parentMatch.appendTail(newXml);

        String processedXML = XML_HEADER + (newXml.toString().replaceAll("_HCITSPACE_", " ")).replace("xmlns=\"\"", NAME_SPACE);

        writeToFile("/temp/xformPorcessed.xml",processedXML);

        //System.out.println("new String: [" + processXML + "]");

        //System.out.println(buildXml);
        //System.out.println("xmlStr: " + xmlStr);
	}

	public static void testRegExpReplace() {
		 Pattern p = Pattern.compile("cat");
		 Matcher m = p.matcher("one cat two cats in the yard");
		 StringBuffer sb = new StringBuffer();
		 while (m.find()) {
		     m.appendReplacement(sb, "dog");
		 }
		 m.appendTail(sb);
		 System.out.println(sb.toString());
	}

	public static void writeToFile(String fileName, String data){
		// Stream to write file
		FileOutputStream fout;

		try
		{
		    // Open an output stream
		    fout = new FileOutputStream (fileName);

		    // Print a line of text
		    new PrintStream(fout).print(data);

		    // Close our output stream
		    fout.close();
		}
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to write to file");
			System.exit(-1);
		}
	}

	public static String readFromFile(String fileName){
		int ch;
		StringBuffer strContent = new StringBuffer("");
		FileInputStream fin = null;
		File file = new File(fileName);

		try {
			fin = new FileInputStream(file);

			while( (ch = fin.read()) != -1) {
		        strContent.append((char)ch);
			}
			fin.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return strContent.toString();
	}
}
