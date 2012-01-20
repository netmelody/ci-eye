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
                "color": "#000000",
                "padding": "3px",
                "border": "1px solid #000000",
                "font-family": "verdana,sans-serif",
                "font-size": "0.8em"
            }
        },
        menuTab = $("<div></div>").hide().css(style.menuStyle),
        menu = $("<div></div>").hide().css(style.menuStyle),
        mouseover = false,
        timeoutId = undefined;
    
    function hideMenu() {
        if (!mouseover) {
            menu.animate({ "top": "-50px" }, 100, function() { menu.hide(); });
        }
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
    
    function elementFor(id, label, initialState, changeHandler) {
        var ele = $("<input type='checkbox'/>").attr("id", id).attr("name", id),
            labelEle = $("<label></label>").attr("for", id).text(label);
        
        ele.attr("checked", initialState);
        ele.bind("change", function() { changeHandler(ele.prop("checked")); });
        
        menu.append(ele);
        menu.append(labelEle);
        menu.append($("<br/>"));
    }

    function buildMenuFor(entries) {
        menu.empty();
        $.each(entries, function(index, entry) {
            elementFor("flymenu-" + index, entry.label, entry.initialState, entry.changeHandler);
        });
    }
    
    $(document).ready(function() {
        menu.appendTo("body");
    });
    
    $.fn.flyMenu = function(entries) {
        buildMenuFor(entries);
        $(this).bind("mousemove", function(event) {
            if (event.pageY < 200 && event.pageX > ($("body").width() - 200)) {
                showMenu();
                return false;
            }
            return true;
        });
        menu.bind("mouseenter", function(){ mouseover = true; });
        menu.bind("mouseleave", function(){ mouseover = false; });
        return this;
    };
})(jQuery);
