var Sidebar = {};

(function(Sidebar) {
	
	Sidebar.ANIMATION_TIME = 150; // milliseconds
	Sidebar.TAB_ANIMATION_TIME = 100;
	
	Sidebar.tabs = {};
	
	// must wait for DOM to finish loading
	Sidebar.setRoot = function(root) {
		Sidebar.root = root;
		// reset tabs on new root
		Sidebar.tabs = {};
	}
	
	class Tab {
		constructor(title) {
			this.title = title;
			this.ele = Sidebar.root.find(".tab-sidebar_tab[data-tippy-content='" + title + "']");
			
			// if we couldn't find the tab
			if(!this.ele.length) throw "No such Tab: " + title;

			// register tab
			Sidebar.tabs[title] = this;
			
			this.relocateSidebar();
			
			// unhide tab when object is created 
			this.ele.removeClass("tab-sidebar_tab-removed");
		}
		
		relocateSidebar() {
			var sidebar = this.ele.find(".content-sidebar");
			if(!sidebar.length) return; // no packaged-sidebar
			
			// set 'data-sidebar' and relocate to 'root'
			sidebar.attr("data-sidebar", this.title).appendTo(Sidebar.root);
			
			// create/assign sidebar from element
			this.sidebar = new ContentSidebar(this, sidebar);
		}
		
		removeTab() {
			// remove from tab-listing
			Sidebar.tabs[this.title] = undefined;
			
			if(this.sidebar) {
				// if tab has sidebar, close then remove it
				this.sidebar.closeThen(() => this.sidebar.ele.remove());
			}
			
			// hide this tab, then remove it from DOM
			this.ele.addClass("tab-sidebar_tab-removed")
				// wait for side-off animation to finnish
				.delay(Sidebar.TAB_ANIMATION_TIME)
				.queue(next => {
					this.ele.remove();
					next();
				});
		}
	}
	
	class ContentSidebar {
		constructor(tab, ele) {
			// check if sidebar was provided
			if(!ele || !ele.length) throw "Sidebar element cannot be 'null'";
			
			this.tab = tab;
			this.ele = ele;

			tab.sidebar = this;
			this.initContent();
		}
		
		initContent() {
			this.setLoading(false);
			
			// add scroll listener
			this.ele.find(".content-sidebar_content").on("scroll", e => this.onScroll(e));
			// update scroll message status
			this.onScroll(null);
			
			// check for a loading overlay
			if(!this.ele.find(".content-sidebar_content .content-sidebar_loading-overlay").length) {
				// if there's no loading overlay, add the default one
				this.ele.find(".content-sidebar_content").append(`
					<div class="content-sidebar_loading-overlay">
						<i class="fa fa-sync-alt fa-spin content-sidebar_loading-icon"></i>
						<span> Loading, please wait... </span>
					</div>`)
			}
			
			var replaceProp = this.ele.find("[data-replace]");
			// look for a content-replace attr
			if(replaceProp.length) {
				var url = replaceProp.attr("data-replace");
				Sidebar.replaceContents(this.tab.title, url);
			}

			var titleProp = this.ele.find("[data-title]");
			var subTitleProp = this.ele.find("[data-sub_title]");
			// look for a content-title attr
			if(titleProp.length || subTitleProp.length) {
				this.setTitle(
					titleProp.length ? titleProp.attr("data-title") : undefined,
					subTitleProp.length ? subTitleProp.attr("data-sub_title") : undefined
				);
			}
		}
		
		replaceContent(content) {
			var current = this.ele.find(".content-sidebar_content");
			
			// clean-up old content
			if(current.attr("data-uid")) {
				UID.disconnect(current.attr("data-uid"));
			}
			
			// ensure content has class
			content.addClass("content-sidebar_content");
			current.replaceWith(content);
			
			this.initContent();
		}
		
		setTitle(title, subTitle) {
			if(typeof title !== typeof undefined) {
				title = title || '';
				this.ele.find(".content-sidebar_header #sidebar-title").text(title);
			}
			
			if(typeof subTitle !== typeof undefined) {
				subTitle = subTitle || '';
				this.ele.find(".content-sidebar_header #sidebar-sub_title").text(subTitle);
			}
		}
		
		setLoading(loading) {
			if(loading)
				this.ele.addClass("loading");
			else 
				this.ele.removeClass("loading");
		}
		
		onScroll(event) {
			var scrollMessage = this.ele.find(".content-sidebar_scroll");
			if(!scrollMessage.length) return; // if there's no scroll message
			
			var content = this.ele.find(".content-sidebar_content")[0];
			// if there is no loaded content (nothing to scroll)
			if(!content) {
				// hide message
				scrollMessage.addClass("hide-scroll_message");
				return;
			}
			
			var contentHeight = content.getBoundingClientRect().height;
			
			// calculate what percentage of element scroll
			// NOTE: +1 is for when no scrolling is needed, divisor will be 0 and 0/0 == Nan while 1/0 = Inf.
			var scrollPercent = (content.scrollTop + 1) / Math.max(content.scrollHeight - contentHeight, 0);
			
			// if past 90% scroll
			if(scrollPercent > .9) {
				scrollMessage.addClass("hide-scroll_message");
			} else {
				scrollMessage.removeClass("hide-scroll_message");
			}
		}
		
		isOpen() { return this.ele.hasClass("content-sidebar_open"); }
		
		open() { 
			Sidebar.root.find(".content-sidebar_open").removeClass("content-sidebar_open")
			this.ele.addClass("content-sidebar_open"); 
		}
		
		closeThen(callback) {
			var open = this.isOpen();
			// close sidebar and wait for animation to finish
			this.ele.removeClass("content-sidebar_open")
				// delay for animation, unless already closed
				.delay(open && Sidebar.ANIMATION_TIME || 0)
				.queue(next => {
					callback && callback();
					next();
				});
		}
	}
	
	
	Sidebar.addTab = function(title) {
		if(Sidebar.tabs[title]) throw "Tab '" + title + "' already exists! Use replace-content";
		return Sidebar.tabs[title] = new Tab(title);
	};
	
	Sidebar.removeTab = function(title) {
		var tab = Sidebar.tabs[title];
		// if tab exists, remove it
		tab && tab.removeTab();
	};
	
	
	
	Sidebar.openTab = function(tabName) {
		var tab = Sidebar.tabs[tabName];
		tab && tab.sidebar && tab.sidebar.open();
	};
	
	Sidebar.closeTab = function(tabName) {
		var tab = Sidebar.tabs[tabName];
		tab && tab.sidebar && tab.sidebar.closeThen();
	};
	
	Sidebar.setTabLoading = function(tabName, loading) {
		var tab = Sidebar.tabs[tabName];
		tab && tab.sidebar && tab.sidebar.setLoading(loading);
	};
	
	
	Sidebar.closeTabThen;
	Sidebar.isOpen;
	
	Sidebar.openOrLoad = function(tab, url) {
		if(Sidebar.tabs[tab]) {
			Sidebar.openTab(tab);
		
		} else {
			Sidebar.loadAndReplace(url);
		}
	};
	
	Sidebar.loadAndReplace = function(url) {
		$.ajax({
			url: url,
			method: "GET"

		}).done(function(data, status, xhr) {
			if(status === "success") {
				var ele = $(data);
				var tab_ele = ele.hasClass("tab-sidebar_tab") && ele || ele.find(".tab-sidebar_tab");
				var tabName = tab_ele.attr("data-tippy-content");
				
				if(!tabName) 
					throw "Response does not contain tab!";
				
				var tab = Sidebar.tabs[tabName];
				
				if(!tab) {
					// tab should auto-register 
					Sidebar.root.find(".tab-sidebar").append(ele);
				
				} else {
					// TODO: replace header
					tab.sidebar.replaceContent(ele.find(".content-sidebar_content"));
				}
				
				// short delay to allow DOM to finish loading
				window.setTimeout(() => Sidebar.openTab(tabName), Sidebar.ANIMATION_TIME / 2);
			}
		}).fail(function(xhr, status, error) {
		});
	};
	
	Sidebar.replaceContents = function(tabTitle, url) {
		var tab = Sidebar.tabs[tabTitle];
		if(!tab) throw "Tab '" + tabTitle + "' doesn't exist! Cannot replace content";
		
		// mark sidebar as loading (swapping content)
		tab.sidebar.setLoading(true);
		
		$.ajax({
			url: url,
			method: "GET"

		}).done(function(data, status, xhr) {
			if(status === "success") {
				tab.sidebar.replaceContent($(data));
				// loading done
				tab.sidebar.setLoading(false);
			}
		}).fail(function(xhr, status, error) {
		});
	};
	
	Sidebar.replaceHeader;
	
	Sidebar.setTitle = function(tab, title, subTitle) {
		var tab = Sidebar.tabs[tabTitle];
		if(!tab || !tab.sidebar) throw "Tab '" + tabTitle + "' doesn't exist! Cannot set title of 'null'";
		
		tab.sidebar.setTitle(title, subTitle);
	};
	
	Sidebar.onScrollClick = function(event) {
		// TODO: should be based on TAB
		
		var scroll = $(event.target).closest("div");
		var content = scroll.siblings(".content-sidebar_content")[0];
		var bounds = content.getBoundingClientRect();
		
		// scroll content by half height
		content.scrollBy(0, bounds.height / 2);
	}
	
})(Sidebar);