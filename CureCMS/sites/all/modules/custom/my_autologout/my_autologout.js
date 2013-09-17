
var my_autologout = {};

(function($) {

  $(function() {
    my_autologout.activePages.init();
  });

  my_autologout.activePages = {
    COOKIE_PREFIX : 'System.page.',

    init : function() {
      if (this.checkCookiesEnabled()) {
        this.initListeners();
        this.initCurrentPage();
      }
    },

    checkCookiesEnabled : function() {
      helpers.cookies.setCookie('TestCookiesEnabled', '1');
      if (helpers.cookies.getCookie('TestCookiesEnabled')) {
        helpers.cookies.deleteCookie('TestCookiesEnabled');
        return true;
      }
      return false;
    },

    initListeners : function() {
      var self = this;
      window.onunload = function(){
        self.windowUnload();
      };
      return this;
    },

    windowUnload : function() {
      this.initCurrentPage();
    },

    initCurrentPage : function() {
      var self = this;
      var page = window.location.pathname;
      self.setPageIsActive(page);
      setInterval(function(){
        self.setPageIsActive(page);
      }, 2000);
    },

    setPageIsActive : function(page) {
      page = helpers.trim(page, '/');
      helpers.cookies.setCookie(this.COOKIE_PREFIX+page, '1', {expires : 3, path : '/'})
    },

    isActivePage : function(page) {
      page = helpers.trim(page, '/');
      return helpers.cookies.getCookie(this.COOKIE_PREFIX+page);
    }
  }

})(jQuery)

