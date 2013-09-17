package com.healthcit.cure.dataloader.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.healthcit.cure.dataloader.businessdelegates.GatewayEtlManager;
import com.healthcit.cure.dataloader.utils.IOUtils;
import com.healthcit.cure.utils.Constants;

@Controller
@RequestMapping(value="/gatewayEtl")
public class GatewayEtlController {
	
	private static Logger log = LoggerFactory.getLogger( GatewayEtlController.class );
	
	@Autowired
	GatewayEtlManager gatewayEtlManager;
	
	@RequestMapping(value="/updateEtlDB")
	public void updateEtlDB(@RequestParam(value="callback", required=false) String callback, 
						    HttpServletRequest request, 
						    HttpServletResponse response)
	{
		try
		{
			log.info("In updateEtlDB() method...");			
			gatewayEtlManager.updateEtlDB();						
			IOUtils.sendOkResponse( response, Constants.CONTENT_TYPE_JSON );
		}
		catch(Exception ex)
		{
			IOUtils.sendProcessingError( response, ex.getMessage(), ex );
		}
	}
	
	
}
