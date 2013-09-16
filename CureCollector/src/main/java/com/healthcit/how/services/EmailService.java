/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.services;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailService {
	
	Logger log = LoggerFactory.getLogger(EmailService.class);
	
	private MailSender mailSender;
	
	private SimpleMailMessage message;
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	public void setMessage(SimpleMailMessage message) {
		this.message = message;
	}
	
	/**
	 * Generates email using the supplied parameters as the email body/recipients.
	 * @param body - The email content
	 * @param recipients - The email recipients
	 * @param body - The email body
	 */
	public void generateEmail( String body, String subject, String[] recipients ) {
		
		SimpleMailMessage msg = new SimpleMailMessage(this.message);
		
		// set the subject - default to the configuration value if this is null
		if ( subject != null )
			msg.setSubject( subject );
		
		// set the recipients
		msg.setTo( recipients );
		
		// set the body
		msg.setText( body );
		
		// generate the email
		
		try {
			this.mailSender.send(msg);
		} catch ( MailException mex ) {
			log.error( "Could not generate email..." );
			log.error( mex.getMessage() );
		}
		
	}

}
