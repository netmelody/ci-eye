(function($) {
    "use strict";
    var style = {
            "menuStyle": {
                "position": "absolute",
                "backgroundColor": "#ffffff",
                "opacity": "1",
                "right": "-70px",
                "top": "0px",
                "width": "100px",
                "zIndex": "500",
                "color": "#000000",
                "padding": "3px",
                "border": "1px solid #000000",
                "font-family": "verdana,sans-serif",
                "font-size": "0.8em"
            }
        },
        menu = $("<div>Enter Desktop Mode</div>")
                   .hide()
                   .css(style.menuStyle)
                   .bind("click", function(event) {
                       event.stopPropagation();
                   }),
        timeoutId = undefined;
    
    function hideMenu() {
        menu.animate({ "right": "-70px" }, 100, function() { menu.hide(); });
    }
    
    function applyHoverStyle() {
        $(this).css(style.itemHoverStyle);
    }
    
    function removeHoverStyle() {
        $(this).css(style.itemStyle);
    }
    
    function showMenu() {
        if (timeoutId) {
            window.clearTimeout(timeoutId);
            timeoutId = undefined;
        }
        if (!menu.is(":visible")) {
            menu.show();
            menu.animate({ "right": "0px" }, 200);
        }
        timeoutId = window.setTimeout(hideMenu, 500);
    }
    
    $(document).ready(function() {
        menu.appendTo("body");
    });
    
    $.fn.flyMenu = function() {
        $(this).bind("mousemove", function(event) {
            showMenu();
            return false;
        });
        return this;
    };
})(jQuery);