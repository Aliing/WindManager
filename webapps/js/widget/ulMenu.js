;(function($) {
	var root = this;
	Aerohive = root.Aerohive || {};
	root.Aerohive = Aerohive;
	Aerohive.menuObj = Aerohive.menuObj || {};
	
	var getPageWH = function() {
		var pageWidth = window.innerWidth,
			pageHeight = window.innerHeight;
		
		if (typeof pageWidth != 'number') {
			if (document.compatMode == 'CSS1Compat') {
				pageWidth = document.documentElement.clientWidth;
				pageHeight = document.documentElement.clientHeight;
			} else {
				pageWidth = document.body.clientWidth;
				pageHeight = document.body.clientHeight;
			}
		}
		
		return {
			width: pageWidth,
			height: pageHeight
		}
	};
	
	Aerohive.menuObj.ULMenu = function(options) {
		var self = this;
		var btnPath = options.btnPath;
		$(btnPath).unbind('click').click(function(e) {
			var $this = $(this);
			var $parent = $this.parents("div.dropdown");
			$parent.toggleClass("open");
			var $mainMenu = $parent.find("> .dropdown-menu");
			var btnPos = $this.position();
			var offPos = $this.offset();
			var pageSize = getPageWH();
			var menuPos = {
				top: btnPos.top + $this.height() + 8,
				left: btnPos.left
			};
			$parent.removeClass("my-out-right-page");
			if (offPos.left + $mainMenu.width() > pageSize.width) {
				$parent.addClass("my-out-right-page");
				// plus 10 for padding+margin
				menuPos.left = menuPos.left + $this.width() - $mainMenu.width() + 10;
			}
			$mainMenu.css({
				top: menuPos.top,
				left: menuPos.left
			});
			$this.focus();
			self.bln_click_in_menu_area = false;
			e.preventDefault();
			e.stopPropagation();
		}).blur(function(e) {
			if (!self.bln_click_in_menu_area) {
				$(this).parents("div.dropdown").removeClass("open");
			}
		});
		
		self.hideMenu = function() {
			$(btnPath).parents("div.dropdown").removeClass("open");
		};
		
		self.addAdditionalEventsToMenu = function() {
			$(btnPath).siblings("ul.dropdown-menu").unbind("mouseover")
				.unbind("mouseout")
				.mouseover(function(e) {
					self.bln_click_in_menu_area = true;
				}).mouseout(function(e) {
					self.bln_click_in_menu_area = false;
				});
			$(btnPath).siblings("ul.dropdown-menu").find("li").click(function(e) {
				self.hideMenu();
			});
		}
		if (options.menuRendered) {
			self.addAdditionalEventsToMenu();
		}
	};
}).call(this, jQuery);