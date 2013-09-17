/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

(function ($) {

  $(document).ready(function() {
    cacure_mgmt_sidebar.controller.init();
  });

  cacure_mgmt_sidebar = {

    controller : {
      init : function() {
        this.showActiveInstanceTab();
        this.showActiveFormTab();
        this.bind();
      },

      bind : function() {
        var self = this;
        this.bindTabOpen('.instance-tab-expand');
        this.bindTabOpen('.instance-tab-expand-active');
      },

      bindTabOpen : function(selector) {
        var self = this;
        selector = selector.toString();
        $(selector).click(function(){
          var link = $(this);
          if (link.hasClass('tab-opened')) {
            self.closeTab(link);
          } else {
            self.showTab(link);
          }
          return false;
        });
      },

      showActiveFormTab : function() {
        var activeFormName = $('.form-title-active');
        var parentTab = $(activeFormName.parent());
        if (parentTab.hasClass('instance-tab')) {
          this.showParentTab(parentTab);
        }
        return this;
      },

      showActiveInstanceTab : function() {
        var activeLink = $('.instance-tab-expand-active');
        if (activeLink.length > 0) {
          var instanceTab = $(activeLink.parent());
          var parentTab = $(instanceTab.parent());
          if (parentTab.hasClass('instance-tab')) {
            this.showParentTab(parentTab);
          }
        }
        return this;
      },

      showParentTab : function(tab) {
        var tabId = tab.attr('id');
        var tabLinkId = '#link_'+tabId;
        tabLinkId = tabLinkId.toString();
        var tabLink = $(tabLinkId);
        if (tabLink.length > 0) {
          this.showTab(tabLink);
        }
      },

      showTab : function(link) {
        var id = link.attr('href');
        id = id.toString();
        var instanceTab = $(link.parent());
        var parentTab = $(instanceTab.parent());
        if (parentTab.hasClass('instance-tab')) {
          this.showParentTab(parentTab);
        }
        link.addClass('tab-opened');
        var tab = $(id);
        tab.attr('style', 'display: block');
      },

      closeTab : function(link) {
        var id = link.attr('href');
        id = id.toString();
        var tab = $(id);
        var childLinks = tab.find('a.tab-opened');
        if (childLinks.length > 0) {
          for (var i=0; i<childLinks.length; i++) {
            this.closeTab($(childLinks[i]));
          }
        }
        link.removeClass('tab-opened');
        tab.attr('style', 'display: none');
      }

    }
  }

})(jQuery)