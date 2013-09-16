/*******************************************************************************
 *Copyright (c) 2013 HealthCare It, Inc.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the BSD 3-Clause license
 *which accompanies this distribution, and is available at
 *http://directory.fsf.org/wiki/License:BSD_3Clause
 *
 *Contributors:
 *    HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
com.healthcit.hopeanalytics = {};
/*
 * Answers UI block
 */
com.healthcit.hopeanalytics.Answer = function(element, answers, questionIdentifier, step){
	this.stepSize = step;
	this.answersArray = answers;
	this.questionId = questionIdentifier;
	this.confirmationBoxCallback = undefined;
	this.layoutCallback = undefined;

	this._buildAnswersElements = function(){
		this.fieldsetElement.children("div").detach();
		for ( var i=0; i < this.answersArray.length; ++i ){
			var ansCheckboxId = 'ans_' + i + '_' + questionId;
			
			var answerDivElement = jQuery(document.createElement("div"));
			var answerCheckboxElement = jQuery(document.createElement("input"));
			answerCheckboxElement.attr("type", "checkbox");
			answerCheckboxElement.attr("id", ansCheckboxId);
			answerCheckboxElement.attr("value", this.answersArray[i]);
			answerCheckboxElement.attr("onClick", "setAnswersConfirmationBox('"+i+"', '"+questionId+"');");
			
			var answerSpanElement = jQuery(document.createElement("span"));
			answerSpanElement.html('<span class="answerbull">&bull;</span>' + (this.answersArray[i] ? this.answersArray[i] : '<i>&lt;Empty&gt;</i>'));
			
			answerDivElement.append(answerCheckboxElement);
			answerDivElement.append(answerSpanElement);
			
			this.fieldsetElement.append(answerDivElement);			
		}
		if(this._confirmationBoxCallback != undefined){
			this._confirmationBoxCallback(questionId,"check_"+questionId);
		}
		if(this._layoutCallback != undefined){
			this._layoutCallback(questionId);
		}
	};
	
	this.addAnswers = function(answers){
		var initialCapacity = this.answersArray.length;
		for ( var i=0; i < answers.length; i++ ){
				this.answersArray[initialCapacity+i] = answers[i];
		}
		this._buildAnswersElements();
	};
	

	this.removeAnswers = function() {		
		if (answers.length > this.stepSize) {
			var lastIndex = this.stepSize;
			for(var i = answers.length-1; i>0; i--){
				if(i % this.stepSize == 0){
					lastIndex = i;
					break;
				}
			}
			answers.splice(lastIndex, answers.length);
			this._buildAnswersElements();
			return lastIndex == this.stepSize;
		} else {
			return false;
		}
		
	};
	
	this.setConfirmationBoxCallback = function(callback){
		this._confirmationBoxCallback = callback;
	};
	
	this.setLayoutCallback = function(callback){
		this._layoutCallback = callback;
	};
	
	// Initialization of answers section
	this.fieldsetElement = jQuery(document.createElement("fieldset"));
	this.fieldsetElement.addClass("selectAnswerSpan");
	this.fieldsetElement.attr("id", 'answers_'+ questionId);
	
	this.legendElement = jQuery(document.createElement("legend"));
	this.legendElement.html("Answers");
	this.legendElement.addClass("answerSpanHeader");
	this.fieldsetElement.append(this.legendElement);
	this._buildAnswersElements();	
	element.append(this.fieldsetElement);
};




