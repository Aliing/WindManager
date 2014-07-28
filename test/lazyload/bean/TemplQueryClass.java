package lazyload.bean;

import java.util.List;

public class TemplQueryClass {

    private String simpleName;
    private String canonicalName;
    private List<TemplQueryField> fields;
    private List<TemplQueryField> ignoredFields;
    
    public String getSimpleName() {
        return simpleName;
    }
    public String getCanonicalName() {
        return canonicalName;
    }
    public List<TemplQueryField> getFields() {
        return fields;
    }
    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }
    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
    public void setFields(List<TemplQueryField> fields) {
        this.fields = fields;
    }
    public List<TemplQueryField> getIgnoredFields() {
        return ignoredFields;
    }
    public void setIgnoredFields(List<TemplQueryField> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }
    
    
}
