/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

// Theme menu navigation js file, use jQuery
//window.onbeforeunload = linkRedirect;
(function ($) {

  Drupal.behaviors.cacureManagement = function(context) {
   // var id = $.cookie('drupalManagementSelectId') ? $.cookie('drupalManagementSelectId') : $('div#block-cacure_mgmt-0 a').attr('href');

    /*getContent(id);

    $('div#block-cacure_mgmt-0 a').bind('click', function() {
      cookieDel('drupalManagementSelectId');
      cookieSet('drupalManagementSelectId', $(this).attr('href'));
      getContent($(this).attr('href'));
      return false;
    });*/

//     var id = Drupal.settings.xformCurrentID;

     pathArray = window.location.pathname.split( '/' );
     var id = pathArray['3'];

     $('#block-cacure_mgmt-1 div.form-element .text').removeClass('active');

     $('#block-cacure_mgmt-1 div.form-element #' + id).addClass('active');

     $('#block-cacure_mgmt-3 span.form-module-submit-disabled').click(function (){$('#block-cacure_mgmt-3 #cacure-submit-form').submit();}); 

     //add handlers to print/view pdf
   
     $('#block-cacure_mgmt-2 .view-and-pring a, #block-cacure_mgmt-2 .view-and-pring img, #block-cacure_mgmt-4 .module-body .edit a, #block-cacure_mgmt-4 .module-body .edit img').click(function(e) {
       printRedirect.call(this);return false;
     });
  }

  function printRedirect(){

    var uri = $(this).parent().parent().find('a').attr('href');
    var url = location.protocol + '//' + location.host + '/' + $(this).parent().parent().find('a').attr('href');
    var param = '';

    if(this.tagName == 'IMG'){
      window.open(url + '?param=print','_blank');
      return false;
    }
    $(location).attr('href', url); 
  }

  function getContent(arg){
    $.ajax({
      url: Drupal.settings['basePath'] + 'ajax_management?id=' + arg,
      dataType: 'html',
      success: function (path, textStatus) {
        $('div#block-cacure_mgmt-1 div.content').empty();
        $('div#block-cacure_mgmt-1 div.content').html(path);
        $('span.form-module-submit-enabled').click(function (){
          $(this).prev().submit();
        });
        $('span.form-module-submit-disabled').click(function (){
          alert("You can't submit. Please fill out all forms.(This is test message.)");
        });
      },
      error: function (xhr, ajaxOptions, thrownError) {
      }
    });
  }

  //set cookie
  function cookieSet(item, value) {
    $.cookie(item, value, {expires: null, path: '/'});
  }

  //del cookie
  function cookieDel(item) {
    $.cookie(item, null, {expires: null, path: '/'});
  }
})(jQuery);
