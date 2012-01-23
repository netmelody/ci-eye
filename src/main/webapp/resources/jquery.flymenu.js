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
            },
            "menuTabStyle": {
                "position": "absolute",
                "zIndex": "499",
                "top": "-3px",
                "right": "8px",
                "width": "8px",
                "height": "8px",
                "color": "#ffffff",
                "padding": "0px",
                "border": "0px",
                "font-family": "verdana,sans-serif",
                "font-size": "1em"
            }
        },
        menuTab = $("<div>&#9660;</div>").hide().css(style.menuTabStyle),
        menu = $("<div></div>").hide().css(style.menuStyle),
        mouseover = false,
        menuTabTimeoutId = undefined,
        menuTimeoutId = undefined;

    function showMenuTab() {
        if (menu.is(":visible")) {
            return;
        }
        
        menuTab.show();
        if (menuTabTimeoutId) {
            window.clearTimeout(menuTabTimeoutId);
            menuTabTimeoutId = undefined;
        }
        menuTabTimeoutId = window.setTimeout(hideMenuTab, 500);
    }

    function hideMenuTab() {
        menuTab.hide();
    }

    function hideMenu() {
        if (!mouseover) {
            menu.animate({ "top": "-50px" }, 100, function() { menu.hide(); });
        }
    }

    function showMenu() {
        hideMenuTab();
        
        if (menuTimeoutId) {
            window.clearTimeout(menuTimeoutId);
            menuTimeoutId = undefined;
        }
        if (!menu.is(":visible")) {
            menu.show();
            menu.animate({ "top": "0px" }, 200);
        }
        menuTimeoutId = window.setTimeout(hideMenu, 500);
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
        menuTab.appendTo("body");

        menuTab.bind("mouseenter", function() { showMenu(); });

        menu.bind("mouseenter", function(){ mouseover = true; });
        menu.bind("mouseleave", function(){
            mouseover = false;
            window.setTimeout(hideMenu, 500);
        });
    });

    $.fn.flyMenu = function(entries) {
        buildMenuFor(entries);

        $(this).bind("mousemove", function(event) {
            showMenuTab();
            return false;
        });

        return this;
    };
})(jQuery);
