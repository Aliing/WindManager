/**
 *
 * Template Name: ${.template_name}
 * FreeMarker version: ${.version}
 * Generate Time: ${.now?datetime} 
 *
 */
package ${packagePath};

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryBo;

<#list classes as clazz>
import ${clazz.canonicalName};
</#list>

public class SearchEngineLazyLoad implements QueryBo {

    @Override
    public Collection<HmBo> load(HmBo bo) {
        if (null == bo) {
            return null;
        }
        
        <#macro loadBlock name first="false">
        <#if first == "false">else </#if>if (bo instanceof ${name}) {
            ${name} hmbo = (${name}) bo;
            load${name}(hmbo); <#-- load method for per BO -->
        }
        </#macro>
        <#list classes as clazz>
        <#if clazz_index == 0>
        <@loadBlock name="${clazz.simpleName}" first="true"/>
        <#else>
         <@loadBlock name="${clazz.simpleName}"/>
        </#if>
        </#list>

        return null;
    }
    
    <#list classes as clazz>
    private void load${clazz.simpleName}(${clazz.simpleName} hmbo) {
        if (null == hmbo) {
            return;
        }
        <#list clazz.fields as field>
        <#if field.object>
        if (null != hmbo.get${field.name?cap_first}()) {
            hmbo.get${field.name?cap_first}().getId();
        }
        <#elseif field.collection>
        if (null != hmbo.get${field.name?cap_first}()) {
            hmbo.get${field.name?cap_first}().size();
        }
        </#if>
        </#list>
    }
    </#list>
}