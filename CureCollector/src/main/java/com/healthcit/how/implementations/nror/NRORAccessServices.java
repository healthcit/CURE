package com.healthcit.how.implementations.nror;

import com.healthcit.how.api.AccessServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.healthcit.how.models.SharingGroupModule;


public class NRORAccessServices extends AccessServices{
	
	protected SharingGroupModule findCurrentModule(List<SharingGroupModule> modules)
	{
//		List<SharingGroupModule> modules = new ArrayList<SharingGroupModule>();
//		for(int i=0; i< entityModules.size(); i++)
//		{
//			if(entityModules.get(i).getStatus().name().equals(Constants.STATUS_NEW) || entityModules.get(i).getStatus().name().equals(Constants.STATUS_IN_PROGRESS))
//			{
//				
//				modules.add(entityModules.get(i));
//			}
//		}
		SharingGroupModule currentModule = null;
		if(modules.size()>1)
		{
			Collections.sort(modules, new  Comparator<SharingGroupModule>() {
				  @Override
				public int compare(SharingGroupModule e1, SharingGroupModule e2)
				  {
					 Date date1 = e1.getModule().getDeployDate();
					 Date date2 = e2.getModule().getDeployDate();
					  return date1.compareTo(date2);
				  }
				
			});
			currentModule = modules.get(0);
		}
		else if(modules.size()==1)
		{
			currentModule = modules.get(0);
		}
		if(currentModule != null)
		{
			currentModule.setIsEditable(true);
		}
		return currentModule;
	}

}
