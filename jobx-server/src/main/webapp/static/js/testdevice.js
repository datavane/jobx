jQuery.extend({
    isAndroid: function() {
        return navigator.userAgent.match(/Android/i) ? true : false;
    },
    isBlackBerry: function() {
        return navigator.userAgent.match(/BlackBerry/i) ? true : false;
    },
    isiOS: function() {
        return navigator.userAgent.match(/iPhone|iPad|iPod/i) ? true : false;
    },
    isWindows: function() {
        return navigator.userAgent.match(/IEMobile/i) ? true : false;
    },
    isMobile: function() {
        return (jQuery.isAndroid() || jQuery.isBlackBerry() || jQuery.isiOS() || jQuery.isWindows())||false;
    },
    isPC:function () {
        return !jQuery.isMobile();
    }
})
