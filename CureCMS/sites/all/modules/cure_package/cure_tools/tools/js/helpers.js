/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

var helpers = {};

helpers.trim = function( str, charlist ) {
  charlist = !charlist ? ' \\s\xA0' : charlist.replace(/([\[\]\(\)\.\?\/\*\{\}\+\$\^\:])/g, '\$1');
  var re = new RegExp('^[' + charlist + ']+|[' + charlist + ']+$', 'g');
  return str.replace(re, '');
}

helpers.cookies = {
  getCookie : function(name, decode)
  {
    var matches = document.cookie.match(new RegExp(
      "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    var result = false;
    if (matches) {
      decode ? result = decodeURIComponent(matches[1]) : result = matches[1];
    }
    return result;
  },

  setCookie : function(name, value, props, encode)
  {
    props = props || {}
    var exp = props.expires
    if (typeof exp == "number" && exp) {
      var d = new Date()
      d.setTime(d.getTime() + exp*1000)
      exp = props.expires = d
    }
    if(exp && exp.toUTCString) { props.expires = exp.toUTCString() }
    if (encode) {
      value = encodeURIComponent(value)
    }
    var updatedCookie = name + "=" + value
    for(var propName in props){
      updatedCookie += "; " + propName
      var propValue = props[propName]
      if(propValue !== true){ updatedCookie += "=" + propValue }
    }
    document.cookie = updatedCookie
  },

  deleteCookie : function(name) {
    this.setCookie(name, null, { expires: -1 })
  }
}
