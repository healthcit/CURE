/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

(function ($) {
  $(document).ready(function (){
    $('#block-menu-menu-pvt-account ul.menu a[href*="/my-current-questionnaire"]').attr('class', 'active').parent().attr('class', 'leaf active-trail-0');

    pathArray = window.location.pathname.split( '/' );
    var id = pathArray['3'];

    $('#block-cacure_mgmt-1 div.form-element .text').removeClass('active');

    $('#block-cacure_mgmt-1 div.form-element #' + id).addClass('active');

    $('table.hcitComplexTable').each(function(){
      $(this).find('th:last').attr('colspan', 10);
    });
//    $('.menu li a').click(function(){
//      menuclicks++;
//      if (menuclicks == 1) {
//        if (confirm('Are you sure you want to navigate away from this page?' + "\r\n" +
//          'Navigating away from this screen will cause any changes to be lost. You can click on \'Save\' at the bottom of the form page before trying to navigate away.' + "\r\n" +
//          'Please OK to continue, or Cancel to stay on the current page.')) {
//          return true;
//        } else {
//          return false;
//        }
//      } else {
//        menuclicks = 0;
//      }
//    });
    //correct buttons
    correctButtons('.hcit-approve-action-group', '.hcit-decline-action-group');
    correctButtons('.hcit-save-for-later-group', '.hcit-submit-action-group');
    /*var content = $('#sidebar-first').html();
     content = content + "\r\n" + '<div class="sidebar-first-spacer"></div>';
     $('#sidebar-first').html(content);*/
    $('.hcitSimpleTable tr').each(function(){
      var items = $(this).find('.xforms-item');

      if (items.length == 5)
      {
        $(this).find('.xforms-itemset').css('width', '100%');
        items.css({'padding' : 0, 'margin' : 0, 'width' : '80px'});
      }
    });
  });

  function correctButtons(main, related)
  {
    main = $(main);

    if (main.length) {
      main.css('overflow', 'hidden').css('visibility', 'visible');
      related = $(related + ' .xforms-group-content');
      main.find('.xforms-group-content').css({'float' : 'left', 'margin-right' : '10px'});
      related.css({'float' : 'left'});
      main.append(related);
    }
  }
})(jQuery);