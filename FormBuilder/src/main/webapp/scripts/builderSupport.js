/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function generateAnswerBlock(idx)
{
	var html =  
	"	<hr/> "+
		"Answer " + (idx + 1) + ".<br/>" +
		<select id="type" name="type" onchange="javascript:showControls();">  
		<option value="TEXT" selected="selected">TEXT</option><option value="RADIO">RADIO</option><option value="CHECKBOX">CHECKBOX</option>
	</select>         

		"Type: \<select id=\"answers" + idx + ".type\" name=\"answers[" + idx + "].type\" \>" +  
		"	\<form:options items=\"answers[" + idx + "].answerTypes\" itemValue=\"value\" itemLabel=\"label\" /\>" +
		"\</form:select\>\<br/\>" +
		"Text:  \<input id=\"answers" + idx + ".description\" name=\"answers[" + idx + "].description\"  type=\"text\" value=\"\" size=\"75\" maxlength=\"200\"/\>\<br/\>" +
		"Value: \<input id=\"answers" + idx + ".value\" name=\"answers[" + idx + "].value\"  type=\"text\" value=\"\" size=\"25\" maxlength=\"25\"/\>"
	;
	return html;
}

function bindAnswerBlok(idx)
{
	var typeInputCtrl = document.getElementById("answerDivTypeInput."+idx);
	typeInputCtrl.name="answers[" + idx + "].type";
	typeInputCtrl.id="answers" + idx + ".type";

	var descrInputCtrl = document.getElementById("answerDivDescrInput."+idx);
	descrInputCtrl.name="answers[" + idx + "].description";
	descrInputCtrl.id="answers" + idx + ".description";
	
	var valueInputCtrl = document.getElementById("answerDivValueInput."+idx);
	valueInputCtrl.name="answers[" + idx + "].value";
	valueInputCtrl.id="answers" + idx + ".value";
	
}

function unbindAnswerBlok(idx)
{
	var typeInputCtrl = document.getElementById("answers" + idx + ".type");
	typeInputCtrl.name="";
	typeInputCtrl.id="answerDivTypeInput."+idx;

	var descrInputCtrl = document.getElementById("answers" + idx + ".description");
	descrInputCtrl.name="";
	descrInputCtrl.id="answerDivDescrInput."+idx;
	
	var valueInputCtrl = document.getElementById("answers" + idx + ".value");
	valueInputCtrl.name="";
	valueInputCtrl.id="answerDivValueInput."+idx;
	
}

function addAnswerBlockOld(cnt){
	// find next available block
	var divId = "answerDiv." + cnt;
	var divToFill = document.getElementById(divId);
	bindAnswerBlok(cnt);
//	divToFill.innerHTML= generateAnswerBlock(cnt);
	divToFill.style.display="block";
}

function addAnswerBlock(cnt){
	// find next available block
	var divId = "answerDiv." + cnt;
	var divToFill = document.getElementById(divId);
	var validAnswerCtrl = document.getElementById("answers" + cnt + ".valid");
	validAnswerCtrl.value="true";
	divToFill.style.display="block";
}
