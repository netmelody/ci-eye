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
                "opacity": "0.2",
                "zIndex": "499"
            },
            "itemListStyle": {
                "listStyle": "none",
                "padding": "1px",
                "margin": "0px",
                "backgroundColor": "#fff",
                "border": "1px solid #999",
                "width": "100px"
            },
            "itemStyle": {
                "margin": "0px",
                "color": "#000",
                "display": "block",
                "cursor": "default",
                "padding": "3px",
                "border": "1px solid #fff",
                "backgroundColor": "transparent"
            },
            "itemHoverStyle": {
                "border": "1px solid #0a246a",
                "backgroundColor": "#b6bdd2"
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
                     .css(style.shadowStyle);
    
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
        var content = $("<ul></ul>").css(style.itemListStyle);
        
        $.each(items, function(index, item) {
            content.append(listItemFor(item));
        });
        content.append(listItemFor({"label": "Cancel",
                                    "handler": hideMenu}));
        
        menu.empty();
        menu.append(content);
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
    });
    
    $.fn.popupMenu = function(items) {
        $(this).bind("click", function(event) {
            showMenu(event.pageX, event.pageY, items);
            return false;
        });
        return this;
    };
})();