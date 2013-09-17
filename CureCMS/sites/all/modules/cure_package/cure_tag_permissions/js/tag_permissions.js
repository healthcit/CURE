/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

var cure_tag_permissions = {};
(function ($) {
  
  $(function() {
    cure_tag_permissions.checks.init();
       
  });

  cure_tag_permissions.checks = {
    init : function() {
      this.bind();
    },

    bind : function() {
      var self = this;
      $('.cure_tag').click(function() {
        self.tagLink($(this));
        return false;
      });

      $('#select_all_tags').click(function(){
        self.selectAllTags();
        return false;
      });

      $('#deselect_all_tags').click(function(){
        self.deselectAllTags();
        return false;
      });

      $('#check_all').click(function(){
        self.checkAll();
        return false;
      });

      $('#uncheck_all').click(function(){
        self.uncheckAll();
        return false;
      });


    },

    tagLink : function($A) {
      var tag = this.getTag($A);
      if (!$A.parent().hasClass('selected')) {
        tag.attr("checked", true);
        $A.parent().addClass('selected');
      }
      else {
        $A.parent().removeClass('selected');
        tag.attr("checked", false);

      }
    },

    selectAllTags : function() {
      var self = this;
      $('.cure_tag').each(function(){
        var $A = $(this);
        var tag = self.getTag($A);
        tag.attr("checked", true);
        $A.parent().addClass('selected');
      });
    },

    deselectAllTags : function() {
      var self = this;
      $('.cure_tag').each(function(){
        var $A = $(this);
        var tag = self.getTag($A);
        $A.parent().removeClass('selected');
        tag.attr("checked", false);
      });
    },

    checkAll : function() {
      $('#tag_permissions input[type=checkbox]').each(function(){
        $(this).attr("checked", true);
      });
    },

    uncheckAll : function() {
      $('#tag_permissions input[type=checkbox]').each(function(){
        $(this).attr("checked", false);
      });
    },

    getTag : function($A) {
      var tid = $A.html();
      return $(document.getElementById('tag_' + tid));
    }
  }
})(jQuery);