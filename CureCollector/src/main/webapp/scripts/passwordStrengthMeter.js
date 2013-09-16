/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
﻿﻿// Password strength meter
// This jQuery plugin was written by Firas Kassem [2007.04.05] and modified by Amin Rajaee [2009.07.26]
// Firas Kassem  phiras.wordpress.com || phiras at gmail {dot} com
// Amin Rajaee  rajaee at gmail (dot) com
//
// Modified by Bill Tulskie 08-30-2009
//


var shortPass = 'Password too short.'
var badPass = 'Password is weak. Use letters and numbers.'
var goodPass = 'Password strength is medium.  Add special characters.'
var strongPass = 'This is a strong password.'
var sameAsUsername = 'Password is the same as username.'
var passwordStrengthId = '#passwordStrengthPercent' 


function passwordStrength(password,username)
{
    score = passwordStrengthPercent(password,username)
    
    //verifing 0 < score < 100
    if ( score < 0 )  score = 0 
    if ( score > 100 )  score = 100 
    
    if (score < 34 )  return badPass 
    if (score < 68 )  return goodPass
    return strongPass
}



function passwordStrengthPercent(password,username)
{
    score = 0 
    
    //password < 4
    if (password.length < 4 ) { return 0 }
    
    //password == username
    if (password.toLowerCase()==username.toLowerCase()) return 0
    
    //password length
    score += password.length * 4
    score += ( checkRepetition(1,password).length - password.length ) * 1
    score += ( checkRepetition(2,password).length - password.length ) * 1
    score += ( checkRepetition(3,password).length - password.length ) * 1
    score += ( checkRepetition(4,password).length - password.length ) * 1

    //password has 3 numbers
    if (password.match(/(.*[0-9].*[0-9].*[0-9])/))  score += 5 
    
    //password has 2 sybols
    if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~].*[!,@,#,$,%,^,&,*,?,_,~])/)) score += 5 
    
    //password has Upper and Lower chars
    if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/))  score += 10 
    
    //password has number and chars
    if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/))  score += 15 
    //
    //password has number and symbol
    if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([0-9])/))  score += 15 
    
    //password has char and symbol
    if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([a-zA-Z])/))  score += 15 
    
    //password is just a nubers or chars
    if (password.match(/^\w+$/) || password.match(/^\d+$/) )  score -= 10 
    if (score > 100) return 100
    
    // set the password strength field
    $(passwordStrengthId).val( score )
    
  return (score)
 
}

function checkRepetition(pLen,str) {
    res = ""
    for ( i=0; i<str.length ; i++ ) {
        repeated=true
        for (j=0;j < pLen && (j+i+pLen) < str.length;j++)
            repeated=repeated && (str.charAt(j+i)==str.charAt(j+i+pLen))
        if (j<pLen) repeated=false
        if (repeated) {
            i+=pLen-1
            repeated=false
        }
        else {
            res+=str.charAt(i)
        }
    }
    return res
}


function validateFieldsEmail() {
	var email=document.getElementById('email').value;
	if(document.getElementById('confirmPassword').value !="" && document.getElementById('confirmPassword').value !="Test$123" && document.getElementById('confirmPassword').value == document.getElementById('password').value){
		if ( document.getElementById('email').value == "" ) { 
				alert("Your email address is required");
				
				return false;
		}else if(!email.match(/[a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9\w\.-]+\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]/)){
			alert('Your Email Address is an invalid e-mail address.  Please make sure you have "@" and "." in your email address. ');
			
			return false;
		}
	}
}
function validateFieldsCEmail(){
	if(document.getElementById('email').value !="" && document.getElementById('email').value.match(/([a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z])/)){
	if ( document.getElementById('confirmEmailAddr').value != document.getElementById('email').value ) { 

			alert("Confirm Email must be same as User Email");
			
			return false;
		}
	}
}

/* Setup keyup events on load */
jQuery(document).ready(function() {
	
		var bpos = "";
		var perc = 0 ;
		var minperc = 0 ;
		$('#password').css( {backgroundPosition: "0 0"} );
		$('#username').keyup(function(){
		$('#result').html(passwordStrength($('#password').val(),$('#username').val())) ;
		perc = passwordStrengthPercent($('#password').val(),$('#username').val());
		
		bpos=" $('#colorbar').css( {backgroundPosition: \"0px -" ;
		bpos = bpos + perc + "px";
		bpos = bpos + "\" } );";
		bpos=bpos +" $('#colorbar').css( {width: \"" ;
		bpos = bpos + (perc * 2) + "px";
		bpos = bpos + "\" } );";
		eval(bpos);
			$('#percent').html(" " + perc  + "% ");
		              })
		$('#password').keyup(function(){
		$('#result').html(passwordStrength($('#password').val(),$('#username').val())) ; 
		perc = passwordStrengthPercent($('#password').val(),$('#username').val());
		
		//alert(perc);
		if( perc > "67" ) {
			//enableContent('registrationButton');
			//HideContent("registrationButtondisablemsg");
		}
		if( perc <= "67" ) {
			//DisabledContent('registrationButton');
			//ShowContent("registrationButtondisablemsg");
		}		
		bpos=" $('#colorbar').css( {backgroundPosition: \"0px -" ;
		bpos = bpos + perc + "px";
		bpos = bpos + "\" } );";
		bpos=bpos +" $('#colorbar').css( {width: \"" ;
		bpos = bpos + (perc * 2) + "px";
		bpos = bpos + "\" } );";
		eval(bpos);
			$('#percent').html(" " + perc  + "% ");
		})
})
