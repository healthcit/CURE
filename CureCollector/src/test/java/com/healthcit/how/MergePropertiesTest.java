package com.healthcit.how;

import java.io.File;

import com.healthcit.cacure.ant.MergeProperties;

public class MergePropertiesTest {
	
	public static void main(String args[])
	{
		MergeProperties mergeP = new MergeProperties();			
		mergeP.setBaseFile(new File(args[0]));
		mergeP.setOverrideFile(new File(args[1]));
		mergeP.setDestFile(new File(args[2]));
		mergeP.execute();
	}
}
