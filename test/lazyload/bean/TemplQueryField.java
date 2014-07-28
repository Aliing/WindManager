package lazyload.bean;

import javax.persistence.Transient;

public class TemplQueryField {

    private boolean object;
    private boolean collection;
    private String name;
    
    @Transient
    private Class<?> objectClass;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isObject() {
        return object;
    }
    public boolean isCollection() {
        return collection;
    }
    public void setObject(boolean object) {
        this.object = object;
    }
    public void setCollection(boolean collection) {
        this.collection = collection;
        this.setObjectClass(null);
    }
    public Class<?> getObjectClass() {
        return objectClass;
    }
    public void setObjectClass(Class<?> objectClass) {
        this.objectClass = objectClass;
    }
    
}
