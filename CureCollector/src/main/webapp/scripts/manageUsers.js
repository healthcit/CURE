/**
 * Javascript for adding, editing and deleting users
 */

// Function which displays a form to add a new user
function addNewUser() {
	// clear any filled-out fields in the "Create User" section
	jQuery('#username_-1,#password_-1').val('');
	
	// display the form
	editExistingUser( -1 );
}

// Function which displays a form to update an existing user
function editExistingUser( userId ) {
	showUserUpdateForm( userId );
}

// Function which displays a form to delete an existing user
function deleteExistingUser( userId ) {
	if ( confirm( 'Are you sure you want to delete this user?' ) ) {
		RegistrationService.deleteUser( 
				userId, {
				callback : function( data ) {
					if ( data == "OK" ) {
						hideUserUpdateForm( userId );
						removeRowsForUser( userId );
					}
					else alert("Could not delete user. Please contact your System Administrator.");				
				},
				errorHandler: function( msg, exception ) {
					alert("Error occurred. Please contact your System Administrator.\n" + msg);
				}		
		});
	}
}

// Function which updates a user in the database
function saveUserUpdates( userId ) {
	var user = { id : ( userId > -1 ? userId : null ), 
				 username : jQuery('#username_'+userId).val(),
				 password : jQuery('#password_'+userId).val(),
				 role : jQuery('#role_'+userId).val()};
		
	RegistrationService.registerUser( 
			user, {
			callback : function( data ) {
				if ( data == "OK" ) {
					hideUserUpdateForm( userId );
					if ( userId == -1 ) window.location.reload();
				}
				else {
					var alertMsg = "Could not save user\n\n" + ( data ? data : 'Please contact your System Administrator.'); 
					alert(alertMsg);				
				}
			},
			errorHandler: function( msg, exception ) {
				alert("Error occurred. Please contact your System Administrator.\n" + msg);
			}		
	});
}

// Function which cancels any pending user updates so that they won't be saved to the database
function cancelUserUpdates( userId ) {
	hideUserUpdateForm( userId );
}

// Utility method which displays the form used for creating/editing users
function showUserUpdateForm( userId ) {
	var formRow = jQuery('#editUserRow_'+userId);
	if ( formRow ) {
		formRow.show();
		formRow.find('td div.editUserContent')
			.animate({ 'padding-top': '60px'}, 100)
			.animate({ 'padding-top': '10px'}, 900);	
	}
}

//Utility method which hides the form used for creating/editing users
function hideUserUpdateForm( userId ) {
	var formRow = jQuery('#editUserRow_'+userId);
	if ( formRow ) {
		formRow.find('td div.editUserContent')
			.animate({ 'padding-top': '40px'}, 100)
			.animate({ 'padding-top': '10px'}, 100);	
		setTimeout(function(){formRow.hide();}, 300);
	}
}

// Utility method which removes rows associated with this userId
function removeRowsForUser( userId ) {
	var removeRows = jQuery('#showUserRow_' + userId + ',#showUserRow_'+userId );
	removeRows.animate({ 'padding-top': '40px'}, 100)
			  .animate({ 'padding-top': '10px'}, 100);	
	setTimeout(function(){removeRows.remove();}, 300);
}