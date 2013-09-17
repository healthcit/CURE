/*  Functions */

/* Home Page Log In Box Labels in Fields */


function initOverLabels() {
  if (!document.getElementById) return;

  var labels, id, field;

  // Set focus and blur handlers to hide and show 
  // labels with 'overlabel' class names.
  labels = document.getElementsByTagName('label');
  for (var i = 0; i < labels.length; i++) {

    if (labels[i].className == '') {

      // Skip labels that do not have a named association
      // with another field.
      id = labels[i].htmlFor || labels[i].getAttribute('for');
      if (!id || !(field = document.getElementById(id))) {
        continue;
      }

      // Removes colins from the labels
      var label = labels[i].innerHTML;
      var lab = label.replace(":", "");
      labels[i].innerHTML = lab;

      // Change the applied class to hover the label 
      // over the form field.
      labels[i].className = 'overlabel-apply';

      // Hide any fields having an initial value.
      if (field.value !== '') {
        hideLabel(field.getAttribute('id'), true);
      }

      // Set handlers to show and hide labels.
      field.onfocus = function () {
        hideLabel(this.getAttribute('id'), true);
      };
      field.onblur = function () {
        if (this.value === '') {
          hideLabel(this.getAttribute('id'), false);
        }
      };

      // Handle clicks to label elements (for Safari).
      labels[i].onclick = function () {
        var id, field;
        id = this.getAttribute('for');
        if (id && (field = document.getElementById(id))) {
          field.focus();
        }
      };

    }
  }
}
;

function hideLabel(field_id, hide) {
  var field_for;
  var labels = document.getElementsByTagName('label');
  for (var i = 0; i < labels.length; i++) {
    field_for = labels[i].htmlFor || labels[i].getAttribute('for');
    if (field_for == field_id) {
      labels[i].style.textIndent = (hide) ? '-1000px' : '0px';
      return true;
    }
  }
}

/*  Function for Re-Directing User if they are using an outdated Browser */


(function ($) {
  $(document).ready(function() {
    var pathname = window.location.pathname.split('.com');
    //alert(pathname[0]);
    //    console.log(pathname[0]);
    if (pathname[0] != '/HOW/obsolete-browser-detected') { // Don not redirect if on the redirect landing page
      var userAgent = navigator.userAgent.toLowerCase();
      $.browser.chrome = /chrome/.test(navigator.userAgent.toLowerCase());

      // Is this a version of IE?
      if ($.browser.msie) {
        $('body').addClass('IE');

        // Add the version number
        $('body').addClass('IE' + $.browser.version.substring(0, 1));
        if ($.browser.version.substring(0, 1) < 7) {
          $(window.location).attr('href', 'obsolete-browser-detected');
        }

      }

      // Is this a version of Chrome?
      if ($.browser.chrome) {

        // $('body').addClass('browserChrome');

        //Add the version number
        userAgent = userAgent.substring(userAgent.indexOf('chrome/') + 7);
        userAgent = userAgent.substring(0, 1);
        $('body').addClass('Chrome' + userAgent);

        // If it is chrome then jQuery thinks it's safari so we have to tell it it isn't
        $.browser.safari = false;
      }

      // Is this a version of Safari?
      if ($.browser.safari) {
        // $('body').addClass('browserSafari');

        // Add the version number
        userAgent = userAgent.substring(userAgent.indexOf('version/') + 8);
        userAgent = userAgent.substring(0, 1);        
        $('body').addClass('Safari' + userAgent);

        if (userAgent < 2) {
          $(window.location).attr('href', 'obsolete-browser-detected');
        }
      }

      // Is this a version of Mozilla?
      if ($.browser.mozilla) {

        //Is it Firefox?
        if (navigator.userAgent.toLowerCase().indexOf('firefox') != -1) {
          // $('body').addClass('browserFirefox');          
          // Add the version number
          userAgent = userAgent.substring(userAgent.indexOf('firefox/') + 8);                    
          
          $('body').addClass('Firefox' + userAgent);          
         
          if (userAgent < 2) {
            $(window.location).attr('href', 'obsolete-browser-detected');
          }

        }
        // If not then it must be another Mozilla
        else {
          $('body').addClass('Mozilla');
        }
      }

      // Is this a version of Opera?
      if ($.browser.opera) {
        $('body').addClass('browserOpera');
      }

    }
  });
})(jQuery);
/* ------------------------------------------------------------------------
 prettyCheckboxes

 Developped By: Stephane Caron (http://www.no-margin-for-errors.com)
 Inspired By: All the non user friendly custom checkboxes solutions ;)
 Version: 1.1

 Copyright: Feel free to redistribute the script/modify it, as
 long as you leave my infos at the top.
 ------------------------------------------------------------------------- */

jQuery.fn.prettyCheckboxes = function(settings) {
  settings = jQuery.extend({
    checkboxWidth: 17,
    checkboxHeight: 17,
    className : 'prettyCheckbox',
    display: 'list'
  }, settings);

  $(this).each(function() {
    // Find the label
    $label = $('label[for="' + $(this).attr('id') + '"]');

    // Add the checkbox holder to the label
    $label.prepend("<span class='holderWrap'><span class='holder'></span></span>");

    // If the checkbox is checked, display it as checked
    if ($(this).is(':checked')) {
      $label.addClass('checked');
    }

    // Assign the class on the label
    $label.addClass(settings.className).addClass($(this).attr('type')).addClass(settings.display);

    // Assign the dimensions to the checkbox display
    $label.find('span.holderWrap').width(settings.checkboxWidth).height(settings.checkboxHeight);
    $label.find('span.holder').width(settings.checkboxWidth);

    // Hide the checkbox
    $(this).addClass('hiddenCheckbox');

    // Associate the click event
    $label.bind('click', function() {

      $('input#' + $(this).attr('for')).triggerHandler('click');

      if ($('input#' + $(this).attr('for')).is(':checkbox')) {

        //alert($('input#' + $(this).attr('for')).attr('checked'));
        if ($('input#' + $(this).attr('for')).is(':checked') && !$('input#' + $(this).attr('for')).is(':disabled')) {
          //$(this).removeClass('checked');
          $(this).toggleClass('checked');
          $('input#' + $(this).attr('for')).checked = false;


        }
        else {
          //$(this).addClass('checked');
          //$(this).toggleClass('checked');
          //$('input#' + $(this).attr('for')).checked = true;

        }

        $(this).find('span.holder').css('top', 0);

      }//end if
      else {
        var $toCheck = $('input#' + $(this).attr('for'));

        // Uncheck all radio
        $('input[name="' + $toCheck.attr('name') + '"]').each(function() {
          $('label[for="' + $(this).attr('id') + '"]').removeClass('checked');
        });

        $(this).addClass('checked');
        $toCheck.checked = true;

      }
    });// End click, bind


    $('input#' + $label.attr('for')).bind('keypress', function(e) {
      if (e.keyCode == 32) {
        if ($.browser.msie) {
          $('label[for="' + $(this).attr('id') + '"]').toggleClass("checked");
        } else {
          $(this).trigger('click');
        }
        return false;
      }
    });
  });
};

checkAllPrettyCheckboxes = function(caller, container) {
  if ($(caller).is(':checked')) {
    // Find the label corresponding to each checkbox and click it
    $(container).find('input[type=checkbox]:not(:checked)').each(function() {
      $('label[for="' + $(this).attr('id') + '"]').trigger('click');
      if ($.browser.msie) {
        $(this).attr('checked', 'checked');
      } else {
        $(this).trigger('click');
      }
    });
  } else {
    $(container).find('input[type=checkbox]:checked').each(function() {
      $('label[for="' + $(this).attr('id') + '"]').trigger('click');
      if ($.browser.msie) {
        $(this).attr('checked', '');
      } else {
        $(this).trigger('click');
      }
    });
  }
};


jQuery.fn.howCheckboxes = function(settings) {
  settings = jQuery.extend({
    checkboxWidth: 17,
    checkboxHeight: 17,
    className : 'prettyCheckbox',
    display: 'list'
  }, settings);

  $(this).each(function() {
    // Find the label

    var $status;    

    $labe = $(this);
    $name = $(this).attr('name');
    $value = $(this).attr('value');
    $value = $value.replace('/', '-');
    $id = $name + $value;
    $(this).attr('id', $id);
    $type = $(this).attr('type');
    if ($(this).is(':checked')) {
      $status = 'checked';
    }
    else {
      $status = ' '
    }

    // Add the checkbox holder to the label
    $labe.before("<label class='" + $status + " option prettyCheckbox " + $type + " list' for='" + $id + "'><span class='holderWrap'><span class='holder'></span></span></label>");

    //$label = $('label[class="option"]');
    var $label = $('label[for="' + $(this).attr('id') + '"]');


    // If the checkbox is checked, display it as checked
    //if($(this).is(':checked')) {  $label.addClass('checked'); };

    // Assign the class on the label
    $label.addClass(settings.className).addClass($(this).attr('type')).addClass(settings.display);

    // Assign the dimensions to the checkbox display
    $label.find('span.holderWrap').width(settings.checkboxWidth).height(settings.checkboxHeight);
    $label.find('span.holder').width(settings.checkboxWidth);

    // Hide the checkbox
    $(this).addClass('hiddenCheckbox');

    // Associate the click event
    $label.bind('click', function() {

      $('input#' + $(this).attr('for')).triggerHandler('click');

      if ($('input#' + $(this).attr('for')).is(':checkbox')) {


        if ($('input#' + $(this).attr('for')).is(':checked') && !$('input#' + $(this).attr('for')).is(':disabled')) {
          $('input#' + $(this).attr('for')).checked = false;
          $(this).removeClass('checked');
          //$(this).toggleClass('checked');


        }
        else {
          $('input#' + $(this).attr('for')).checked = true;
          $(this).addClass('checked');

        }

        $(this).find('span.holder').css('top', 0);

      }//end if
      else {
        var $toCheck = $('input#' + $(this).attr('for'));

        // Uncheck all radio
        $('input[name="' + $toCheck.attr('name') + '"]').each(function() {
          $('label[for="' + $(this).attr('id') + '"]').removeClass('checked');
        });

        $(this).addClass('checked');
        $toCheck.checked = true;

      }
    });// End click, bind


    $('input#' + $label.attr('for')).bind('keypress', function(e) {
      if (e.keyCode == 32) {
        if ($.browser.msie) {
          $('label[for="' + $(this).attr('id') + '"]').toggleClass("checked");
        } else {
          $(this).trigger('click');
        }
        return false;
      }
    });
  });
};

checkAllPrettyCheckboxes = function(caller, container) {
  if ($(caller).is(':checked')) {
    // Find the label corresponding to each checkbox and click it
    $(container).find('input[type=checkbox]:not(:checked)').each(function() {
      $('label[for="' + $(this).attr('id') + '"]').trigger('click');
      if ($.browser.msie) {
        $(this).attr('checked', 'checked');
      } else {
        $(this).trigger('click');
      }
    });
  } else {
    $(container).find('input[type=checkbox]:checked').each(function() {
      $('label[for="' + $(this).attr('id') + '"]').trigger('click');
      if ($.browser.msie) {
        $(this).attr('checked', '');
      } else {
        $(this).trigger('click');
      }
    });
  }
}

 
	