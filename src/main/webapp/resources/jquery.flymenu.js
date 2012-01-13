(function($) {
    "use strict";
    var style = {
            "menuStyle": {
                "position": "absolute",
                "backgroundColor": "#ffffff",
                "opacity": "1",
                "top": "-50px",
                "right": "0px",
                "zIndex": "500",
                "cursor": "pointer",
                "color": "#000000",
                "padding": "3px",
                "border": "1px solid #000000",
                "font-family": "verdana,sans-serif",
                "font-size": "0.8em"
            }
        },
        menuAction = function() { },
        menu = $("<div>Enter Desktop Mode</div>")
                   .hide()
                   .css(style.menuStyle)
                   .bind("click", function(event) {
                       event.stopPropagation();
                       menuAction();
                   }),
        timeoutId = undefined;
    
    function hideMenu() {
        menu.animate({ "top": "-50px" }, 100, function() { menu.hide(); });
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
            menu.animate({ "top": "0px" }, 200);
        }
        timeoutId = window.setTimeout(hideMenu, 500);
    }
    
    $(document).ready(function() {
        menu.appendTo("body");
    });
    
    $.fn.flyMenu = function(clickHandler) {
        menuAction = function() {};
        if (typeof clickHandler === "function") {
            menuAction = clickHandler;
        }
        
        $(this).bind("mousemove", function(event) {
            showMenu();
            return false;
        });
        return this;
    };
})(jQuery);