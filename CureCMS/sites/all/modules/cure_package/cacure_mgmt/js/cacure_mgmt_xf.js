/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

// Theme menu navigation js file, use jQuery
//window.onbeforeunload = linkRedirect;


(function ($) {

  $(document).ready(function (){

     pathArray = window.location.pathname.split( '/' );

    
     var id = pathArray['3'];

     $('#block-cacure_mgmt-1 div.form-element .text').removeClass('active');

     $('#block-cacure_mgmt-1 div.form-element #' + id).addClass('active');
  });
})(jQuery);
