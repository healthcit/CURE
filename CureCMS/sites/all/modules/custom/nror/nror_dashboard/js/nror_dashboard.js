/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

var nror_dashboard = {};

(function($){
  $(document).ready(function() {
    $('.sticky-header').remove();

    $('form').keypress(function(e){
      if ( e.which == 13 ) {
        e.preventDefault();
      }
    });

    nror_dashboard.sorter.init();
    nror_dashboard.initSearch();
    nror_dashboard.tabs.init();
    nror_dashboard.cleanMessages();

  });

  nror_dashboard.cleanMessages = function() {
    setTimeout(function(){
      $('#content-messages').remove();
    }, 7000)
  }

  nror_dashboard.sorter = {
    init : function(){
      $(".tablesorter").each(function () {
        var $table = $(this);
        var th = $table.find("thead th");
        tableHeaders = {};

        $(th).each(function(i){
          var $elem = $(this);
          if ($elem.hasClass('actions') || $elem.hasClass('select-all')) {
            tableHeaders[i] = {sorter:false};
          }
        });

        $table.tablesorter({
          headers: tableHeaders
        });
      });

    }
  }

  nror_dashboard.initSearch = function() {
    nror_dashboard.tableSearch.initById('user-forms', 'No forms found');
    nror_dashboard.tableSearch.initByClass('users-table', 'No users found');
    nror_dashboard.tableSearch.initByClass('facilities_table', 'No practices found');
  }

  nror_dashboard.tableSearch = {

    initByClass : function(tableClass, emptyMessage) {
      var self = this;
      $('.search-box').keyup(function() {
        var table = $(this).parent().parent().find("." + tableClass);
        self.initSearchTable($(this).val(), table);
        self.showEmptyMessage(table, emptyMessage);
      });
    },

    initById : function(tableId, emptyMessage) {
      var self = this;
      $('.search-box').keyup(function() {
        var table = $(this).parent().parent().find("#" + tableId);
        self.initSearchTable($(this).val(), table);
        self.showEmptyMessage(table, emptyMessage);
      });
    },
    
    initSearchTable : function(inputVal, table) {
      table.find('tr').each(function(index, row) {
        var allCells = $(row).find('td');
        if (allCells.length > 0) {
          var found = false;
          allCells.each(function(index, td) {
            var regExp = new RegExp(inputVal, 'i');
            if(regExp.test($(td).text())) {
              found = true;
              return false;
            }
          });
          if (found == true) {
            $(row).show();
          }
          else {
            $(row).hide();
          }
        }
      });
    },
    
    showEmptyMessage : function(table, message) {
      var rows = 0;
      var hidden_rows = 0;
      var cols = table.find('thead th').length;
      table.find('tr').each(function(i) {
        if ($(this).css("display") == "none"){
          hidden_rows++;
        }
        rows++;
      });
      table.find('.empty-res').remove();
      if (rows-1 == hidden_rows) {
        table.append('<tr class="empty-res"><td colspan="'+cols+'">'+ message +'</td></tr>');
      }
    }    
  }

  nror_dashboard.tabs = {
    init : function() {
      this.bind();
    },

    bind: function() {
      var self = this;
      $('.dashboard-tab').click(function(){
        $tab = $(this);
        var href = $tab.attr('href');
        $('.dashboard-frame').html('<div class="dashboard-loading-img"><img title="Loading" src="/sites/all/modules/custom/nror/nror_dashboard/css/images/loading.gif" /></div>');
        $.post(href.toString(), {isAjax : 1}, function(res){
          self.setActive($tab);
          $('.dashboard-frame').html(res);
          $('th.select-all').closest('table').once('table-select', Drupal.tableSelect);
          nror_dashboard.initSearch();
          nror_dashboard.sorter.init();
        });
        return false;
      });
    },

    setActive : function($tab) {
      var currentActiveTabLi = $('.dashboard-tabs li.active');
      currentActiveTabLi.removeClass('active');
      $tab.parent().addClass('active');
    }
  }
})(jQuery);

