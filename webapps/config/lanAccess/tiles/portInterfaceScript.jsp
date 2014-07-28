<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="%{jsonMode==false}">
<script src="<s:url value="/js/innerhtml.js" includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
    src="<s:url value="/js/doT.min.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script
    src="<s:url value="/js/widget/ports/ports.js"  includeParams="none"/>?v=<s:property value="verParam" />"></script>
<script>!window.jQuery && document.write(unescape("%3Cscript src='"+"<s:url value='/js/jquery.min.js' includeParams='none'/>?v=<s:property value='verParam' />"+"' type='text/javascript'%3E%3C/script%3E"))</script>
</s:if>
<!-- Templates -->
<script id="portGroupTmpl" type="text/x-dot-template">
    <div class="portContainer" ref="{{=it.num}}">
        {{? it.type==='USB' && it.logo}}
        <div class="companyLogo"></div>
        {{?}}
        <ul class="port-list" id="{{=it.id}}">
        {{? !it.vertical }}
        {{ for(var i=0; i<it.num; i++) { }}
            <li class="port {{=it.className}} unselected {{? it.disabled0 && i+it.startIndex == 0}}disabled {{=it.disabled0Class||''}}{{?}}"
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">
                {{? it.label && it.type==='USB' && it.logo}}<span class="labelDown">{{=it.type}}</span>{{?}}
                {{? it.label && it.type==='USB' && !it.logo}}<span class="labelUp">{{=it.type}}</span>{{?}}
                {{? it.label && it.type==='LTE'}}<span class="labelUp">{{=it.type}}</span>{{?}}
                {{? it.label && (it.type!=='USB' && it.type!=='LTE')}}<span class="labelUp">{{=i+it.labelStartIndex}}</span>{{?}}
            </li>
        {{ } }}
        {{?? true }}
        {{ for(var i=0; i<it.num; i+=2) { }}
            <li class="port {{=it.className}} unselected" 
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">{{? it.label}}<span class="labelUp">{{=i+it.labelStartIndex}}</span>{{?}}</li>
        {{ } }}
        {{ for(var i=1; i<it.num; i+=2) { }}
            <li class="port {{=it.className}} unselected" 
                id="{{=it.type}}_{{=i+it.startIndex}}" 
                ref="{{=it.titlePrefix}}{{=i+it.startIndex}}">{{? it.label}}<span class="labelDown">{{=i+it.labelStartIndex}}</span>{{?}}</li>
        {{ } }}
        {{?}}
        </ul>
    </div>
</script>
<script id="gdTmpl" type="text/x-dot-template">
    <div>
        <p>Port Assignment:</p>
        <div style="padding-left: 25px;">
        {{~it.array :assignment:index}}
            <p>{{=assignment.key}}&nbsp;&nbsp;({{=assignment.value}})</p>
        {{~}}
        </div>
    </div>
</script>