/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

(function($){

  $(function() {
    $('.showhidelink').click(function(){
      show_hide_module($(this).attr('id'));
      return false;
    });
  });

  function show_hide_module(module_id) {
    if ($('.module_forms_'+module_id).css('display') == 'none') {
      $('.sh_link_'+module_id).html('-');
      $('.module_forms_'+module_id).fadeIn();
    } else {
      $('.sh_link_'+module_id).html('+');
      $('.module_forms_'+module_id).fadeOut();
    }
  }
})(jQuery);
