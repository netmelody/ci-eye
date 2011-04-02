"use strict";
(function() {
    var style = {
            "menuStyle": {
                "position": "absolute",
                "zIndex": "500"
            },
            "shadowStyle": {
                "backgroundColor": "#000000",
                "position": "absolute",
                "opacity": "0.4",
                "zIndex": "499"
            },
            "titleBarStyle": {
                "padding": "1px",
                "margin": "0",
                "backgroundColor": "#ffffff",
                "border": "1px solid #999",
                "height": "16px"
            },
            "itemListStyle": {
                "listStyle": "none",
                "padding": "1px",
                "margin": "0",
                "backgroundColor": "#ffffff",
                "border": "1px solid #999"
            },
            "itemStyle": {
                "margin": "0px",
                "color": "#000000",
                "display": "block",
                "cursor": "default",
                "padding": "3px",
                "border": "1px solid #ffffff",
                "backgroundColor": "transparent",
                "font-family": "verdana,sans-serif",
                "font-size": "0.8em"
            },
            "itemHoverStyle": {
                "border": "1px solid #0a246a",
                "backgroundColor": "#b6bdd2"
            },
            "closeButtonStyle": {
                "color": "#000000",
                "float": "right",
                "padding": "0 1px 0 0",
                "cursor": "pointer",
                "font-family": "verdana,sans-serif",
                "font-size": "0.8em"
            }
        },
        menu = $("<div></div>")
                   .hide()
                   .css(style.menuStyle)
                   .bind("click", function(event) {
                        event.stopPropagation();
                   }),
        shadow = $("<div></div>")
                     .hide()
                     .css(style.shadowStyle),
        titleBar = $("<div></div>")
                       .css(style.titleBarStyle)
                       .appendTo(menu),
        closeButton = $("<a>X</a>")
                          .css(style.closeButtonStyle)
                          .appendTo(titleBar),
        content = $("<ul></ul>")
                      .css(style.itemListStyle)
                      .appendTo(menu);
    
    function hideMenu() {
        menu.hide();
        shadow.hide();
    }
    
    function applyHoverStyle() {
        $(this).css(style.itemHoverStyle);
    }
    
    function removeHoverStyle() {
        $(this).css(style.itemStyle);
    }
    
    function listItemFor(itemData) {
        var item = $("<li></li>")
                       .css(style.itemStyle)
                       .hover(applyHoverStyle, removeHoverStyle)
                       .bind("click", function() {
                                 hideMenu();
                                 itemData.handler();
                             });
        
        item.text(itemData.label);
        return item;
    }
    
    function showMenu(x, y, items) {
        content.empty();
        $.each(items, function(index, item) {
            content.append(listItemFor(item));
        });
        
        menu.css({
                "left": x,
                "top": y})
            .show();
        
        shadow.css({
                  "width": menu.width(),
                  "height": menu.height(),
                  "left": x + 2,
                  "top": y + 2})
              .show();
    }
    
    $(document).ready(function() {
        menu.appendTo("body");
        shadow.appendTo("body");
        closeButton.click(hideMenu);
    });
    
    $.fn.popupMenu = function(items, guardFunction) {
        $(this).bind("click", function(event) {
            if (!guardFunction || guardFunction()) {
                showMenu(event.pageX, event.pageY, items);
                return false;
            }
        });
        return this;
    };
})();