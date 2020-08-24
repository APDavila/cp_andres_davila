$.fn.clickOff = function(callback, selfDestroy) {
		var clicked = false;
		var parent = this;
		var destroy = selfDestroy || true;
		
		parent.click(function() {
			clicked = true;
		});
		
		$(document).click(function(event) { 
			if (!clicked) {
				callback(parent, event);
			}
			if (destroy) {
			};
			clicked = false;
		});
	};
	
/** 
 * PrimeFaces Venture Layout
 */
var Venture = {
  
    init: function() {
        this.menuWrapper = $('#layout-menu-cover');
        this.menuScrollContent = $('#layout-menu-cover-scroll-content');
        this.menu = $('#layout-menu');
        this.menulinks = this.menu.find('a.menulink');
        this.menuPopupButton = $('#mobile-menu-btn');
        this.topMenuWrapper = $('#layout-topbar-menu-cover');
        this.topMenuButton = $('#topbar-menu-btn'); 
        this.body = $('body');
        this.expandedMenuitems = this.expandedMenuitems||[];
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
   
        if(this.isPopupMenu()) {
            this.addNanoScroll();
        }
       
        // show and hide popup main menu when click a menu button
        this.menuPopupButton.on('click', function(){
            $this.popupMenuClick = true;

            if($this.menuWrapper.hasClass('active')) {
                $(this).removeClass('active');
                $this.menuWrapper.removeClass('active');
            }
            else {
                $(this).addClass('active');
                $this.menuWrapper.addClass('active');
                $this.topMenuWrapper.removeClass('active');
                $this.topMenuButton.removeClass('active');
            }
        });
        
        
        this.menulinks.on('click', function(e) {
            var menuitemLink = $(this),
            menuitem = menuitemLink.parent(),
            isActive = menuitem.hasClass('active-menu-parent'),
            parentSubmenu = menuitem.parent('ul').parent('li').closest('ul'),
            isPopup = $this.isPopupMenu();

            if(menuitem.closest('ul').attr('id') == "layout-menu" || (parentSubmenu.attr('id') == "layout-menu" && isPopup)) {    
                $this.resetActiveMenuitems(menuitem, isPopup);
            }

            if(isActive) {
                menuitem.removeClass('active-menu-parent');
                menuitemLink.removeClass('active-menu').next('ul').removeClass('active-menu');
                $this.removeMenuitem(menuitem.attr('id'));
                $this.menubarActive = false;
            }
            else {
                var activeSibling = menuitem.siblings('.active-menu-parent');
                if(activeSibling.length) {
                    activeSibling.removeClass('active-menu-parent');
                    $this.removeMenuitem(activeSibling.attr('id'));

                    activeSibling.find('ul.active-menu,a.active-menu').removeClass('active-menu');
                    activeSibling.find('li.active-menu-parent').each(function() {
                        var menuitem = $(this);
                        menuitem.removeClass('active-menu-parent');
                        $this.removeMenuitem(menuitem.attr('id'));
                    });
                }

                menuitem.addClass('active-menu-parent');
                menuitemLink.addClass('active-menu').next('ul').addClass('active-menu');
                $this.addMenuitem(menuitem.attr('id'));
                $this.menubarActive = true;
            }

            if(menuitemLink.next().is('ul')) {
                e.preventDefault();
            }
            
            $this.saveMenuState();
        })
        .on('mouseenter', function() {
            if ($this.isPopupMenu()) {
                return;
            }
            
            var menuitemLink = $(this),
                menuitem = menuitemLink.parent();
                
            if($this.menubarActive && document.documentElement.clientWidth > 960 && menuitem.closest('ul').attr('id') === "layout-menu" && !menuitem.hasClass('active-menu-parent')) {
                var prevMenuLink = menuitem.parent().find('a.active-menu');
                prevMenuLink.removeClass('active-menu').next('ul.active-menu').removeClass('active-menu');
                prevMenuLink.closest('li').removeClass('active-menu-parent');
                $this.removeMenuitem(prevMenuLink.closest('li').attr('id'));
                menuitem.addClass('active-menu-parent');
                menuitemLink.addClass('active-menu').next('ul[role="menu"]').addClass('active-menu');
                $this.addMenuitem(menuitem.attr('id'));
                $this.saveMenuState();
            }
           
        });
        
        this.menuWrapper.clickOff(function(e) {
            if($this.popupMenuClick) {
                $this.popupMenuClick = false;
            }
            else {
                var activeMenuParent = $this.menu.children('li.active-menu-parent');
                if($this.isPopupMenu()) {
                    $this.menuWrapper.removeClass('active');
                    activeMenuParent.children('a').removeClass('active-menu');
                }
                activeMenuParent.removeClass('active-menu-parent').children('ul').removeClass('active-menu');
                $this.menuPopupButton.removeClass('active');
                $this.menubarActive = false;
            }
        });
        
        $(window).resize(function () {
            var isNano = $this.menuWrapper.hasClass('nano'),
                isPopup = $this.isPopupMenu();
            if(isNano && !isPopup)
                $this.removeNanoScroll();
            else if(!isNano && isPopup)
                $this.addNanoScroll();
        });
        
        this.bindTopbarMenuEvents();
    },
    
    bindTopbarMenuEvents: function() {
        var $this = this;
        
        this.topMenuButton.on('click',function(){
            if($this.topMenuWrapper.is(':hidden')) {
                $(this).addClass('active');
                $this.topMenuWrapper.addClass('active');
                $this.menuWrapper.removeClass('active');
                $this.menuPopupButton.removeClass('active');
            }
            else {
                $(this).removeClass('active');
                $this.topMenuWrapper.removeClass('active');
            }
        });
        
        this.topMenuWrapper.find('a').click(function(e) {
            var link = $(this),
            submenu = link.next('ul');
            
            if(submenu.length) {
                if(submenu.hasClass('active')) {
                    submenu.removeClass('active');
                    link.removeClass('active');
                    $this.topMenuActive = false;
                }
                else {
                    $this.topMenuWrapper.find('> li > ul.active').removeClass('active').prev('a').removeClass('active');
                    link.addClass('active').next('ul').addClass('active');
                    $this.topMenuActive = true;
                }
            }
            else {
                if($(e.target).is(':not(:input)')) {
                    $this.topMenuWrapper.find('.active').removeClass('active');
                    $this.topMenuActive = false;
                }
            }
        })
        .on('mouseenter', function() {
            var link = $(this);
    
            if(link.parent().parent().is($this.topMenuWrapper)&&$this.topMenuActive&&document.documentElement.clientWidth > 960) {
                var submenu = link.next('ul');
                
                $this.topMenuWrapper.find('.active').removeClass('active');
                link.addClass('active');
                
                if(submenu.length) {
                    submenu.addClass('active');
                }
            }
        });
        
        this.topMenuWrapper.find('li').clickOff(function() {
            $this.topMenuWrapper.find('.active').removeClass('active');
            $this.topMenuActive = false;
        });
    },
    
    removeMenuitem: function(id) {        
        this.expandedMenuitems = $.grep(this.expandedMenuitems, function(value) {
            return value !== id;
        });
    },
    
    addMenuitem: function(id) {
        if($.inArray(id, this.expandedMenuitems) === -1) {
            this.expandedMenuitems.push(id);
        }
    },
    
    saveMenuState: function() {
        $.cookie('venture_expandeditems', this.expandedMenuitems.join(','), {path:'/'});
    },
    
    clearMenuState: function() {
        $.removeCookie('venture_expandeditems', {path:'/'});
    },
    
    restoreMenuState: function() {
        var menucookie = $.cookie('venture_expandeditems');
        if(menucookie) {
            this.expandedMenuitems = menucookie.split(',');
            for(var i = 0; i < this.expandedMenuitems.length; i++) {
                var id = this.expandedMenuitems[i];
                if(id) {
                    var menuitem = $("#" + this.expandedMenuitems[i].replace(/:/g,"\\:")),
                        parentItem = menuitem.parent(); 
                
                    if(parentItem && parentItem.attr('id') != "layout-menu") {
                        menuitem.addClass('active-menu-parent');
                        menuitem.children('ul').addClass('active-menu');
                    }
                    menuitem.children('a').addClass('active-menu');
                }             
            }
        }
    },
    
    resetActiveMenuitems: function(menuitem, isPopup) {
        var activeItem = [];

        if(!isPopup) {
            activeItem = this.menuWrapper.find('li > a.active-menu');
            if(activeItem.length && menuitem.attr('id') === activeItem.parent().attr('id')) {
                return;
            }
        }

        for(var i = 0; i < this.expandedMenuitems.length; i++) {
            var id = this.expandedMenuitems[i];
            if(id) {
                var menuitem = $("#" + this.expandedMenuitems[i].replace(/:/g,"\\:"));
                menuitem.removeClass('active-menu-parent');
                menuitem.children('a,ul').removeClass('active-menu');
            }             
        }
        
        if(!isPopup && activeItem.length) {
            activeItem.removeClass('active-menu');
        }
        
        this.expandedMenuitems = [];
    },
    
    isMobile: function() {
        return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(window.navigator.userAgent));
    },
    
    isPopupMenu: function() {
        return (this.body.hasClass('PopupMenu')||(document.documentElement.clientWidth <= 960));
    },
    
    addNanoScroll: function() {
        if(!this.isMobile()) {
            var $this = this;
            this.menuWrapper.addClass('nano');
            this.menuScrollContent.addClass('nano-content');
            $(".nano").nanoScroller();
            setTimeout(function(){$this.menuWrapper.addClass("Animated03");},500);
        }
    },
    
    removeNanoScroll: function() {
        if(!this.isMobile()) {
            this.menuWrapper.removeClass("Animated03");
            $(".nano").nanoScroller({ destroy: true });
            this.menuWrapper.removeClass('nano');
            this.menuScrollContent.removeClass('nano-content');
        }
    },
    
    changeBodyClass: function (changedClass) {
        var $this = this,
            bodyClasses = this.body.attr('class')||"";
        
        if(changedClass === "") {
            bodyClasses = bodyClasses.replace("PopupMenu", "").replace("FixedMenu", "");
        }
        else {
            var regex;
            
            if(changedClass.indexOf('Menu') > 0) {
                regex = /(\s)?\w*(Menu)/gm;
            }
            else if(changedClass.indexOf('Topbar') > 0) {
                regex = /(\s)?\w*(Topbar)/gm;
            }
            else if(changedClass.indexOf('BG') > 0) {
                regex = /(\s)?\w*(BG)/gm;
            }
            
            if(bodyClasses.match(regex)) {
                bodyClasses = bodyClasses.replace(regex, " " + changedClass);
            }
            else {
                bodyClasses = bodyClasses + " " + changedClass;
            }
        }

        this.body.removeClass().addClass(bodyClasses);
        this.menuPopupButton.removeClass('active');
        
        if(changedClass === "" || changedClass.indexOf('Menu') > 0) {
            if(!this.menuWrapper.hasClass('nano') && this.isPopupMenu())
                $this.addNanoScroll();   
            else if(this.menuWrapper.hasClass('nano') && !this.isPopupMenu())
                $this.removeNanoScroll();
        }
    }
};

$(function() {
   Venture.init();
});

/* Issue #924 is fixed for 5.3+ and 6.0. (compatibility with 5.3) */
PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend({
    enableModality: function () {
        this._super();
        $(document.body).children(this.jqId + '_modal').addClass('ui-dialog-mask');
    },
    syncWindowResize: function () {

    }
});