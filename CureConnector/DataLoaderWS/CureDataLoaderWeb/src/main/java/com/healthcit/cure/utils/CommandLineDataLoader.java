package com.healthcit.cure.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineDataLoader {
	
	private static final Logger log = LoggerFactory.getLogger( CommandLineDataLoader.class );
	
	public static void main(String[] args)
	{
		String file = args[0];
		String path = args[1];
		
		loadData(new File(file), path);
	}
	
	public static String loadData( File file, String path)
	{
		log.debug("in the CureDataLoader controller");
		try
		{
			
			if(file != null)
			{
				File dstDir = new File(path);
				
				//needs to create the tree recursively
				if(!dstDir.exists())
				{
					boolean status = dstDir.mkdir();
					if(status)
						log.debug("created directory " + dstDir.getAbsolutePath());
					else
						log.error("could not create directory " + dstDir.getAbsolutePath());
				}
				
//				if(!unzipDir.exists())
//				{
//					boolean status = unzipDir.mkdir();
//					if(status)
//						log.debug("created directory " + unzipDir.getAbsolutePath());
//					else
//						log.error("could not create directory " + unzipDir.getAbsolutePath());
//				}
				
				String fileUniqueId =  UUID.randomUUID().toString();
				File unzipDir = new File(dstDir, fileUniqueId);
				boolean status = unzipDir.mkdir();
				if(status)
					log.debug("created directory " + unzipDir.getAbsolutePath());
				else
					log.error("could not create directory " + unzipDir.getAbsolutePath());
				
				String originalFileName = file.getName();
				int index = originalFileName.lastIndexOf('.');
				String fileExtension = originalFileName.substring(index);
				String fileName = fileUniqueId + fileExtension;
				if(path!= null)
				{
					
					File dataFile = new File(path, fileName);
					//file.transferTo(dataFile);
//					File source = new File("H:\\work-temp\\file");
//					File desc = new File("H:\\work-temp\\file2");
					try {
					    FileUtils.copyFile(file, dataFile);
					} catch (IOException e) {
					    e.printStackTrace();
					}
					
					ZipFile zipFile = new ZipFile(dataFile);
				    Enumeration<? extends ZipEntry>  entries = zipFile.entries();

				    ZipEntry propertyFileEntry = null;
				    while(entries.hasMoreElements()) {
				        ZipEntry entry = (ZipEntry)entries.nextElement();
				        if(entry.isDirectory()) {
					         log.debug("Extracting directory: " + entry.getName());
					         File dir = new File(unzipDir, entry.getName());
					         dir.mkdir();
					          continue;
					    }
//				        // we do not need to worry about the properties file to be last in.
//				        if(entry.getName().endsWith("txt"))
//				        {
//				        	propertyFileEntry = entry;
//				        }
//				        else
//				        {
				        	File destFile = new File (unzipDir, entry.getName());
				        	copyInputStream(zipFile.getInputStream(entry), new FileOutputStream(destFile));
//				        }

				      }
				    
//				      if(propertyFileEntry != null)
//				      {
//				    	  File destFile = new File (unzipPath, propertyFileEntry.getName());
//				    	  copyInputStream(zipFile.getInputStream(propertyFileEntry), new FileOutputStream(destFile));
//				      }

				      zipFile.close();
				      //TODO Comment this out temporarily due to ETL issues
//				      Runtime.getRuntime().exec("/home/tomcat/invoke_pentaho.sh " + unzipDir);
			        }
				}
				else
				{
					
				}
		} 
		catch ( Exception ex )
		{
				log.error(ex.getMessage(),ex );
		}

		return null;
	}

    private static  void copyInputStream(InputStream is, OutputStream os) throws IOException
    {
    	BufferedOutputStream output = new BufferedOutputStream(os);
    	byte[] buffer = new byte[1024];
        int len;

        while((len = is.read(buffer)) >= 0)
        	output.write(buffer, 0, len);

        output.flush();
        is.close();
        output.close();
    }

}
