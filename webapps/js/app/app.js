/**
 *@App
 *@Overview profile
 */
(function($,AE){
	var Mod = AE.Mod,
		cfgs = AE.hm.configs.mdm,
		tools = AE.hm.tools,
		formCfg = AE.hm.configs.form,
		pfes = cfgs.profile,
		attrs = pfes.attrs,
		cla = pfes.cla,
		tmpl = cfgs.tmpl;

	Mod.add('AE.hm.mdm.profile',{
		elems : {
			'.mdm-set-item-con-wrap' : '_mdmWrap',
			'#mdmProfiles' : '_profileForm',
			'.mdm-set' : '_mdmSetEl'
		},
		events : {
			'change .J-tag-select' : '_handleSelectChange',
			'click .mdm-set-item-title' : '_handleShowCon',
			'click .J-tag-checkbox' : '_handleClkCheckbox',
			'click .mdm-set-item-config span' : '_handleShCon',
			'click .mdm-item-action' : '_handleDoArea',
			'click .J-button' : '_handleButton',
			'click .scepSelect':'_handleScepSelect',
			'click .passwordSameCheckbox':'_handleSameAsIncomingChk',
			'click .J-reduce-domain' : '_handleReduceDomain',      // add new
			'click .J-add-domain' : '_handleAddDomain',             // add new
			'change #resContentRegionRating': '_handleResRegionRating',
			'change .J-change-domain' : '_handleChaDomain'         // add new
		},
		init : function(){
			this._handleScepSelect();
			this._triggerInit(this._mdmSetEl.eq(0));
			// init validate form
			this._profileForm.validate(formCfg.profile);
			this._initRequiredFields();
		},
		_handleChaDomain : function(e){                                  // add new function
			var t = $(e.target),
				//inp = t.prev('input');
				inp=t.parent().find('input');
				inp.attr('name',t.val());
			
		},
		_handleResRegionRating : function(e){
			var t = $(e.target),
				v = t.val(),
				appm = " Don`t Allow Apps,4+,9+,12+,17+, Allow All Apps",
				appv = "0,100,200,300,600,1000",
				ustv = "Don`t Allow TVShows,TV-Y,TV-Y7,TV-G,TV-PG,TV-14,TV-MA,Allow All TVShows",
				ustvv = "0,100,200,300,400,500,600,1000",
				usmv = "Don`t Allow Movies,G,PG,RG-13,R,NC-17,Allow All Movies",
				usmvv = "0,100,200,300,400,500,1000",
				autv = "Don`t Allow TVShows,P,C,G,PG,M,MA15+,AV15+,Allow All TVShows",
				autvv = "0,100,200,300,400,500,550,575,1000",
				aumv = "Don`t Allow Movies,G,PG,M,MA15+,R18+,Allow All Movies",
				aumvv = "0,100,200,350,375,400,1000",
				catv = "Don`t Allow TVShows,C,C8,G,PG,14+,18+,Allow All TVShows",
				catvv = "0,100,200,300,400,500,600,1000",
				camv = "Don`t Allow Movies,G,PG,14A,18A,R,Allow All Movies",
				camvv = "0,100,200,325,400,500,1000",
				detv = "Don't Allow TVShows,ab 0 Jahren,ab 6 Jahren,ab 12 Jahren,ab 16 Jahren,ab 18 Jahren,Allow All TVShows",
				detvv = "0,75,100,200,500,600,1000",
				demv = "Don`t Allow Movies,ab 0 Jahren,ab 6 Jahren,ab 12 Jahren,ab 16 Jahren,ab 18 Jahren,Allow All Movies",
				demvv = "0,75,100,200,500,600,1000",
				frtv = "Don`t Allow TVShows,10,-12,-16,-18,Allow All TVShows",
				frtvv = "0,100,200,500,600,1000",
				frmv = "Don`t Allow Movies,-10,-12,-16,-18,Allow All Movies",
				frmvv = "0,100,200,500,600,1000",
				iemv = "Don`t Allow Movies,G,PG,12,15,16,18,Allow All Movies",
				iemvv = "0,100,200,300,350,375,400,1000",
				ietv = "Don`t Allow TVShows,GA,CH,YA,PS,MA,Allow All TVShows",
				ietvv = "0,100,200,400,500,600,1000",
				jpmv = "Don`t Allow Movies,G,PG-12,R-15,R-18,Allow All Movies",
				jpmvv = "0,100,200,300,400,1000",
				jptv = "Don`t Allow TVShows,Explicit Allowed,Allow All TVShows",
				jptvv = "0,500,1000",
				nzmv = "Don`t Allow Movies,G,PG,M,R13,R15,R16,R18,R,RP16,Allow All Movies",
				nzmvv = "0,100,200,300,325,350,375,400,500,600,1000",
				nztv = "Don`t Allow TVShows,G,PGR,AO,Allow All TVShows",
				nztvv = "0,200,400,600,1000",
				gbmv = "Don`t Allow Movies,U,Uc,PG,12,12A,15,18,Allow All Movies",
				gbmvv ="0,100,150,200,300,325,350,400,1000",
				gbtv = "Don`t Allow TVShows,Explicit Allowed,Allow All TVShows",
				gbtvv = "0,500,1000",
				eleM = $("#movieSelectId"),
				eleT = $("#tvShowSelectId"),
				eleA = $("#appSelectId")
				
			this._handleRatingRelationFn(appm.split(","), appv.split(","), eleA);
			if(v == "us"){
					this._handleRatingRelationFn(usmv.split(","),usmvv.split(","),eleM);
					
					this._handleRatingRelationFn(ustv.split(","),ustvv.split(","),eleT);
				}
			if(v == "au"){
				this._handleRatingRelationFn(aumv.split(","),aumvv.split(","),eleM);
				
				this._handleRatingRelationFn(autv.split(","),autvv.split(","),eleT);
			}
			if(v == "ca"){
				this._handleRatingRelationFn(camv.split(","),camvv.split(","),eleM);
				
				this._handleRatingRelationFn(catv.split(","),catvv.split(","),eleT);
			}
			if(v == "de"){
				this._handleRatingRelationFn(demv.split(","),demvv.split(","),eleM);
				
				this._handleRatingRelationFn(detv.split(","),detvv.split(","),eleT);
			}
			if(v == "fr"){
				this._handleRatingRelationFn(frmv.split(","),frmvv.split(","),eleM);
				
				this._handleRatingRelationFn(frtv.split(","),frtvv.split(","),eleT);
			}
			if(v == "ie"){
				this._handleRatingRelationFn(iemv.split(","),iemvv.split(","),eleM);
				
				this._handleRatingRelationFn(ietv.split(","),ietvv.split(","),eleT);
			}
			if(v == "jp"){
				this._handleRatingRelationFn(jpmv.split(","),jpmvv.split(","),eleM);
				
				this._handleRatingRelationFn(jptv.split(","),jptvv.split(","),eleT);
			}
			if(v == "nz"){
				this._handleRatingRelationFn(nzmv.split(","),nzmvv.split(","),eleM);
				
				this._handleRatingRelationFn(nztv.split(","),nztvv.split(","),eleT);
			}
			if(v == "gb"){
				this._handleRatingRelationFn(gbmv.split(","),gbmvv.split(","),eleM);
				
				this._handleRatingRelationFn(gbtv.split(","),gbtvv.split(","),eleT);
			}
		},
		_handleRatingRelationFn : function (str, val ,ele){
			oj = ele.children().eq(0);
			oj.empty();
			for(var i = 0; i < str.length; i ++){
				i < str.length -1 ? oj.append("<option value=" + val[i] + ">"+str[i]+"</option>") :oj.append("<option value=" + val[i] + " selected='selected'>"+str[i]+"</option>"); 
			}
		},
		_handleReduceDomain : function(e){                                // add new function
			var t = $(e.target),
				p = t.closest('p'),
				c = t.closest(cla.mdmCon).find('.J-enable-vpnDemand');
			
			!this._rnm && !this._pcl && 
				(this._pcl = p.clone(true),this._rnm++);
			
			if(!c[0].checked) return;
			p.remove();
		},
		_handleAddDomain : function(e){                                  // add new function
			var t = $(e.target),
				td = t.closest('td'),
				p = td.find('p.mt10:eq(0)'),
				c = t.closest(cla.mdmCon).find('.J-enable-vpnDemand');
			
			!this._anm && !this._pcl && 
				(this._pcl = p.clone(true),this._anm++);
			
			if(!c[0].checked) return;
			td.append(this._pcl.clone(true));
		},
		_handleSameAsIncomingChk:function(e){
			var index=$(e.target).attr('name').replace('emailProfileInfos[','').replace('].outgoingPasswordSameAsIncomingPassword','');
			if($(".passwordSameCheckbox:eq("+(index)+")").is(':checked')){
				$(".outgoingPasswordText:eq("+(index)+")").attr('readonly','true');
				$(".outgoingPasswordText:eq("+(index)+")").val($(".incomingPasswordText:eq("+(index)+")").val());
			}else{
				$(".outgoingPasswordText:eq("+(index)+")").removeAttr('readonly');
			}
		},
		_handleScepSelect:function(){
			
			$(".scepList").html("");
			$(".scepList").append("<option value='None'>None</option>");
			
			$("input[name^='credentialsProfileInfos'][name$='certificateFileName']").each(function(i){
				var name=$(this).val();
				$(".scepList").append("<option value='credentials:"+$(this).val()+"'>credentials:"+$(this).val()+"</option>");
			});
			
			$("input[name^='scepProfileInfos'][name$='url']").each(function(i){
				var name=$(this).attr('name').replace("url","") + "name";
				$(".scepList").append("<option value='scep:"+$(this).val()+"'>"+$("input[name='"+name+"']").val()+"</option>");
			});
			
			$(".scepList").each(function(i){
				var nameHid = $(this).attr('name')+"Hid";
				
				$(this).val($("[name=nameHid]").val());
			});
			
		},
		_initRequiredFields : function(){
			this._doValid(this.el,'add');
		},
		_handleButton : function(e){
			var t = $(e.target),name = t.attr('name');				

			name == 'save' && this._profileForm.submit();
		},
		_handleShCon : function(e){
			var t = $(e.target),c = t.closest(cla.mdmConfig),
				d = t.closest(cla.mdmConWrap),
				li = t.closest(cla.mdmitem),
				name = li.attr(attrs.name),wEl = $(tmpl[name]);//,s = c.next(cla.mdmCon);			   
			// may be use append method
			c.hide(),d.append(wEl);//,s.show();
			
			// initalize trigger hide or show area
			this._triggerInit(wEl);

			//add validator
			this._doValid(wEl,'add');
		},
		_handleSelectChange : function(e){
			var t = $(e.target),
				v = t.val(),
				sp = $("#J-tag-special-choice"),
				name = t.attr(attrs.name),
				out = t.attr('data-outer'),
				fy = new RegExp('\\b'+name+'\\b'),  //add new name Regexp
				fx = new RegExp('\\b'+v+'\\b'),
				cttr = t.closest("tr").parent().children().eq(1),
				cttd = cttr.children().eq(1).children().eq(0);
				liEl = t.closest(cla.mdmitem),
				conEl = t.closest(cla.mdmCon),
				tabEl = t.closest(cla.mdmtable),
				trEl = t.closest('td').prev('td'),
				pEl = liEl.find(cla.mdmtitlep),
				selCons = (!out ? tabEl : conEl).find(cla.selCon);	
			
			selCons.filter(function(){
				return fy.test(this.getAttribute(attrs.name)); // add new change the method filter
			}).each(function(){
				
				var x = $(this),type = x.attr(attrs.type),
					c = x.find(cla.jtagsel);
				
				x[fx.test(type) ? 'show' : 'hide']();
				
				if(cttd.val() == "PPTP"){
					if(type == "Password" || type == "SecurID" ){
						sp.hide();
					}
				}
				/**************add new change*************/
				if(c.length){
					var el = $('#J-special-select');
					selCons.filter(function(){
						return new RegExp('\\b' + c.attr(attrs.name) + '\\b').test(this.getAttribute(attrs.name)); 
					}).css('display',x.css('display'));
					x.css('display')!=='none' && c.trigger('change');
					c = el.closest('.J-tag-select-con');
					c.css('display')!=='none' && el.length && el.trigger('change');
				}
			});
//            this._doFilter({
//                selCons : !f ? selCons : $(cla.selConSub,this.el),
//               fy : fy,
//               fx : fx
//            });ljd

		},
//		 _doFilter : function(o){
//             var fnx = [];
//             o.selCons.filter(function(){
//                      return o.fy.test(this.getAttribute(attrs.name)); // add new change the method filter
//             }).each(function(){
//                      var x = $(this),type = x.attr(attrs.type),
//                                c = x.find(cla.jtagsel);       
//                      
//                      x[o.fx.test(type) ? 'show' : 'hide']();
//                      
//                      if(c.length && x.css('display') != 'none'){
//                                fnx.push(function(){
//                                         c.trigger('change',true);
//                                });
//                      }
//                      
//             });
//             fnx.length && $.each(fnx,function(i,v){v()});
//    },

		_handleShowCon : function(e){
			var t = $(e.target),type = t.attr(attrs.type),
				c = t.next(cla.mdmConWrap),f = type == 'off';				 
			
			t[f ? 'addClass' : 'removeClass'](cla.mdmtitle);
			c[f ? 'show' : 'hide']();
			t.attr(attrs.type,pfes.state.swh[type]);
		},
		_triggerInit : function(c){
			var _sels = c.find(cla.jtagsel).filter(function(){return !$(this).closest('tr.J-tag-select-con').length}),
				_ckboxs = c.find(cla.jtagche),
				_ckboxsSames = c.find(cla.jtagchesame),
				_self = this;			   
			/*if(c.attr(attrs.ready) == 'yes'){
				return;
			}*/
			_sels.length && _sels.trigger('change');
			_ckboxs.length && _ckboxs.each(function(){
				_self._rendCheckShow(c,this.checked,this.getAttribute(attrs.type));
			});
			//=====================================================
			_ckboxsSames.length && _ckboxsSames.each(function(){
				var index=$(this).attr('name').replace('emailProfileInfos[','').replace('].outgoingPasswordSameAsIncomingPassword','');
				if($(".passwordSameCheckbox:eq("+(index)+")").is(':checked')){
					$(".outgoingPasswordText:eq("+(index)+")").attr('readonly','true');
					$(".outgoingPasswordText:eq("+(index)+")").val($(".incomingPasswordText:eq("+(index)+")").val());
				}else{
					$(".outgoingPasswordText:eq("+(index)+")").removeAttr('readonly');
				}
			});			
			//===================================================== 
			//c.attr(attrs.ready,'yes');
		},
		_handleClkCheckbox : function(e){
			var t = $(e.target),type = t.attr(attrs.type),					 
				liEl = t.closest(cla.mdmitem);
			
			this._rendCheckShow(liEl,e.target.checked,type);
		},
		_rendCheckShow : function(c,f,type){
			var dd = c.find('.J-'+type),dt;
			if(type == 'camera' || type == 'siri' || type == 'safari'){
				dt = dd.find('input[type=checkbox]');
				/*dt.attr("checked",false);*/
				!f ? dt.attr('disabled',true) : dt.removeAttr('disabled');
				return;
			}
			dd.toggle(f);				 
		},
		_handleDoArea : function(e){
			var t = $(e.target),
				liEl = t.closest(cla.mdmitem),
				wrapEl = t.closest(cla.mdmConWrap),
				conEl = t.closest(cla.mdmCon),
				type = t.attr(attrs.type),
				name = liEl.attr(attrs.name);				

			this['_do'+type](wrapEl,conEl,name);
		},
		_doadd : function(w,c,name){
			var cEl = $(tmpl[name]);
			
			cEl.appendTo(w)[0].scrollIntoView();
			
			/* trigger select and checkbox*/
			this._triggerInit(cEl);

			// replace number to right numner
			this._replaceRightName(w);
			
			// add required validate
			this._doValid(cEl,'add');
		},
		_dodel : function(w,c){
			//remove required validate
			this._doValid(c,'remove');

			c.remove();			

			// replace number to right numner
			this._replaceRightName(w);

			if(w.find(cla.mdmCon).length == 0){
				w.find(cla.mdmConfig).show();
				//c.closest(cla.mdmConWrap).attr(attrs.ready,'yes');
			}
		},
		_doValid : function(c,m){
			var rEls = c.find('.J-required');
			console.log(rEls);
			//debugger;
			rEls.length && 
				rEls.each(function(){
					var t = $(this),
						rules = eval('('+t.attr('data-rules')+')');
					t.rules(m,m == 'remove' ? null : (rules || {required : true}));
				});
		},
		_replaceRightName : function(wEl){
			//alert("replace");
			var conEls = wEl.find(cla.mdmCon);					

			conEls.each(function(i){
				var conEl = $(this);

//				conEl.find('input').add(conEl.find('select')).each(function(){
//					var t = $(this),name = t.attr('name');	
//					
//					t.attr('name',name.replace(/\[\d+\]/g,'['+i+']'));
//					//====================
//					if ("domainAction"==name){
//						 t.empty();
//						 t.append("<option value='vpnProfileInfos[" + i +"].onDemandMatchDomainsAlwaysHost'>Always establish</option>");
//						 t.append("<option value='vpnProfileInfos[" + i +"].onDemandMatchDomainsNever'>Never establish</option>");
//						 t.append("<option value='vpnProfileInfos[" + i +"].onDemandMatchDomainsOnRetry'>Establish if needed</option>");
//					}
//					//====================
                conEl.find('input,select').each(function(){
                    var t = $(this),name = t.attr('name');      

                    name && t.attr('name',name.replace(/\[\d+\]/g,'['+i+']'));
   

				});
			});
		}
	});
})(jQuery,AE);


