var Behavior = {
	// Convert xp:radioGroups to Bootstrap
	"table[role=presentation]": {
		found: function(table) {
			var radios = dojo.query("input[type=radio]", table);
			if(radios.length > 0) {
				var div = document.createElement("div");

				dojo.forEach(radios, function(radio) {
					var label = radio.parentNode;
					dojo.addClass(label, " radio inline");
					div.appendChild(label);
				});
				
				table.parentNode.insertBefore(div, table);
				table.parentNode.removeChild(table);
			}
		}
	},
        
	// Convert xe:dialogs to Bootstrap
	// h/t http://www.bootstrap4xpages.com/bs4xp/site.nsf/article.xsp?documentId=F7E581AA0B402846C1257B6B004582A1&action=openDocument
	// now http://www.bootstrap4xpages.com/bs4xp/demos.nsf/dialog.xsp
	"div.dijitDialogs": {
		found: function(div) {
			// Clear out margins for the inner form
			dojo.query("form", div).style("margin", "0");

			var titleBar = $(".dijitDialogTitleBar", div).addClass("modal-header");

			var titleNode = $(".dijitDialogTitle", titleBar);
			var title = titleNode.text();
			titleNode.remove();
			
			$(".dijitDialogCloseIcon", titleBar).removeClass("dijitDialogCloseIcon").addClass("close");
			titleBar.append("<h3>" + title + "</h3>");
			
//				dojo.addClass(div, "modal in");
//				div.style.display = "block"
//				
			var wrapper = document.createElement("div");
			wrapper.className = "modal-dialog";
			var wrapper2 = document.createElement("div");
			wrapper2.className = "modal-content";
			wrapper.appendChild(wrapper2);
			
			var children = div.childNodes;
			for(var i = 0; i < children.length; i++) {
				//wrapper2.appendChild(children[i]);
				dojo.place(children[i], wrapper2);
			}
			div.appendChild(wrapper);
		},
		shown: function(div) {
			XSP.alert("shown!")
		}
	},
	".usernames-multi-select": {
		found: function(input) {
			var base = dojo.attr(dojo.query("link[rel='base']")[0], "href")
			$(input).ajaxChosen({
				type: "GET",
				url: base + "xsp/namelookup",
				dataType: "json",
				minTermLength: 1
			}, function(data) { return data })
		}
	},
//		".wysiwyg-editor": {
//			found: function(input) {
//				$(input).ace_wysiwyg({
//					toolbar: [
//				        'bold',
//				        'italic',
//						'strikethrough',
//						null,
//						'insertunorderedlist',
//						'insertorderedlist',
//						null,
//						'justifyleft',
//						'justifycenter',
//						'justifyright'
//					],
//					speech_button:false
//				})
//			}
//		},
	".widget-wysiwyg-editor": {
		found: function(input) {
			$(input).ace_wysiwyg({
				toolbar_place: function(toolbar) {
					return $(this).closest('.widget-box').find('.widget-header').prepend(toolbar).children(0).addClass('inline');
				}
			})
		}
	},
	".wysiwygify": {
		found: function(input) {
//			var wrapped = $('.wysiwygify')
			jQuery(function($) {
				// Our task here is to hide the original and plop its contents into a WYSIWYG editor
				var contents = $(input).text();
				$(input).hide();
				$(input).removeClass("wysiwygify");
			
				var editor = $("<div class='wysiwyg-editor'></div>").html(contents).insertBefore(input).ace_wysiwyg({
					toolbar:
					[
						'font',
						null,
						'fontSize',
						null,
						{name:'bold', className:'btn-info'},
						{name:'italic', className:'btn-info'},
						{name:'strikethrough', className:'btn-info'},
						{name:'underline', className:'btn-info'},
						null,
						{name:'insertunorderedlist', className:'btn-success'},
						{name:'insertorderedlist', className:'btn-success'},
						{name:'outdent', className:'btn-purple'},
						{name:'indent', className:'btn-purple'},
						null,
						{name:'justifyleft', className:'btn-primary'},
						{name:'justifycenter', className:'btn-primary'},
						{name:'justifyright', className:'btn-primary'},
						{name:'justifyfull', className:'btn-inverse'},
						null,
						{name:'createLink', className:'btn-pink'},
						{name:'unlink', className:'btn-pink'},
						null,
						{name:'insertImage', className:'btn-success'},
						null,
						'foreColor',
						null,
						{name:'undo', className:'btn-grey'},
						{name:'redo', className:'btn-grey'}
					],
					'wysiwyg': {
						fileUploadError: function(reason, detail) {
							var msg='';
							if (reason==='unsupported-file-type') { msg = "Unsupported format " +detail; }
							else {
								console.log("error uploading file", reason, detail);
							}
							XSP.alert("File upload error\n\n" + msg);
	//						$('<div class="alert"> <button type="button" class="close" data-dismiss="alert">&times;</button>'+ 
	//						 '<strong>File upload error</strong> '+msg+' </div>').prependTo('#alerts');
						}
					}
				});
				editor.prev().addClass('wysiwyg-style2');
				
				
				XSP.addPreSubmitListener(XSP.findForm(input).id, function() {
					try {
						$(input).text(editor.cleanHtml());
						//$(input).show();
					} catch(e) {
						XSP.alert(e)
					}
				}, null, null);
			})
		}
	}
};

dojo.ready(function() {
	dojo.behavior.add(Behavior);
	dojo.behavior.apply();
});