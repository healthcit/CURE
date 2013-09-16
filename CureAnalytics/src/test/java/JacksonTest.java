/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.healthcit.cacure.data.utils.CouchJSONConverter;





public class JacksonTest
{
	public static void main(String[] args) throws Exception
	{
		CouchJSONConverter jc = new CouchJSONConverter(CouchJSONConverter.OutputFormat.JSON);
		//CouchJSONConverter jc = new CouchJSONConverter(CouchJSONConverter.OutputFormat.XML);
		FileInputStream fis = new FileInputStream("C:\\temp\\json.txt");
		FileOutputStream fos = new FileOutputStream("C:\\temp\\json-changed.txt");
//		FileOutputStream fos = new FileOutputStream("C:\\temp\\dataExport.xml");
		jc.setInputStream(fis);
		jc.setOutputStream(fos);
		jc.convert();
		fos.flush();
		fis.close();
		fos.close();
		
	}
}
