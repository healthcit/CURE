/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.healthcit.how.models.FormSkip;
import com.healthcit.how.models.FormSkipAnswer;
import com.healthcit.how.models.SharingGroupFormInstance;

public class FormAccessServiceUtils 
{
	
	private static final String QUESTIONS = "questions";
	private static final String ANSWER_VALUES = "answerValues";
	private static final String ANSWER_VALUE = "ansValue";
	
	public static List<FormSkipAnswer> getPossibleSkipTriggeringAnswers( JSONObject jsonForm, SharingGroupFormInstance formInstance, List<FormSkip> skipAffectees )
	{
		List<FormSkipAnswer> answers = new ArrayList<FormSkipAnswer>();
		
		
		for ( FormSkip skipAffectee : skipAffectees )
		{			
			JSONArray savedAnswers = getAnswersFromJSONForm( jsonForm, skipAffectee.getQuestionId() );
			
			for ( Object answer : savedAnswers )
			{
				FormSkipAnswer formSkipAnswer = new FormSkipAnswer( formInstance, skipAffectee, ( answer == null ? null : ( String ) answer ) );
					
				answers.add( formSkipAnswer );
			}
		}
		
		
		return answers;
	}
	
	private static JSONArray getAnswersFromJSONForm( JSONObject jsonForm, String questionId )
	{
		JSONObject jsonQuestions        =  (JSONObject)JSONUtils.getObject( jsonForm, QUESTIONS );
		JSONObject jsonQuestion         =  (JSONObject)JSONUtils.getObject( jsonQuestions, questionId );
		JSONArray  jsonAnswerObjects    =  (JSONArray )JSONUtils.getObject( jsonQuestion, ANSWER_VALUES );
		JSONArray answerValues          =  new JSONArray();
		for ( Object jsonAnswerObject : jsonAnswerObjects )
		{
			String answerValue = ( String )JSONUtils.getObject( ( JSONObject )jsonAnswerObject, ANSWER_VALUE ) ;
			answerValues.add( answerValue );
		}
		
		return answerValues;
	}

}
