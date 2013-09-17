/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

(function ($) {
  Drupal.behaviors.cacureG_fixArrow = function(context) {
    $('#block-menu-menu-pvt-account ul.menu a[href*=' + Drupal.settings.fix_arrow_list + ']').attr('class', 'active').parent().attr('class', 'leaf active-trail-0');
  }
})(jQuery)