package lazyload;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lazyload.bean.TemplQueryClass;
import lazyload.bean.TemplQueryField;

import com.ah.bo.HmBo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class AutoQueryBoLoader {

    private File templateFile;
    private String generateFileName;
    private List<Class<?>> classes;
    private List<Class<?>> ignoredClassFields;
    
    public AutoQueryBoLoader(File templateFile, String generateFilePath) {
        this.templateFile = templateFile;
        this.generateFileName = generateFilePath;
    }
    
    public boolean generate(List<Class<?>> classesList, String packagePath) {
        if(null == classesList) {
            return false;
        }
        classes = classesList;
        return _generate(packagePath);
    }
    public boolean generate(Class<?>[] classArray, String packagePath) {
        if(null == classArray) {
            return false;
        }
        classes = Arrays.asList(classArray);
        return _generate(packagePath);
    }
    
    /*---------------_methods-------------------------*/
    private boolean _generate(String packagePath) {
        if(null == classes) {
            return false;
        }
        List<TemplQueryClass> beans = new ArrayList<>();
        for (Class<?> clazz : classes) {
            TemplQueryClass templObj = convertHmBo2TemplateBean(clazz);
            if(null != templObj) {
                beans.add(templObj);
            }
        }
        
        return generateJavaFile(beans, packagePath);
    }
    
    private TemplQueryClass convertHmBo2TemplateBean(Class<?> clazz) {
        
        if(!HmBo.class.isAssignableFrom(clazz)) {
            return null;
        }
        List<TemplQueryField> templQueryFields = new ArrayList<>();
        List<TemplQueryField> templQueryIgnoredFields = new ArrayList<>();
        
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            
            TemplQueryField templQueryField = convertField(field);
            
            if(null != templQueryField) {
                if(existsInIgnoredList(templQueryField)) {
                    templQueryIgnoredFields.add(templQueryField);
                } else {
                    templQueryFields.add(templQueryField);
                }
            }
        }
        if(templQueryFields.isEmpty()) {
            return null;
        } else {
            TemplQueryClass bean = new TemplQueryClass();
            bean.setCanonicalName(clazz.getCanonicalName());
            bean.setSimpleName(clazz.getSimpleName());
            
            bean.setFields(templQueryFields);
            bean.setIgnoredFields(templQueryIgnoredFields);
            
            return bean;
        }
    }
    
    private boolean generateJavaFile(List<TemplQueryClass> classes, String packagePath) {
        final File file = new File(generateFileName);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try(Writer writer = new FileWriter(file)) {
            /* Create and adjust the configuration */
            Configuration cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File(templateFile.getParentFile().getAbsolutePath()));
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            /* ------------------------------------------------------------------- */    
            /* You usually do these for many times in the application life-cycle:  */    
                    
            /* Get or create a template */
            Template template = cfg.getTemplate(templateFile.getName());

            /* Create a data-model */
            Map<String, Object> root = new HashMap<>();
            root.put("packagePath", packagePath);
            root.put("classes", classes);

            /* Merge data-model with template */
            Writer out = new OutputStreamWriter(System.out);
            template.process(root, out);
            out.flush();
            
            // File output
            template.process(root, writer);
            writer.flush();
            
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsInIgnoredList(TemplQueryField templQueryField) {
        if(null != ignoredClassFields) {
            for (Class<?> clazz : ignoredClassFields) {
                if(null != templQueryField.getObjectClass()
                        && templQueryField.getObjectClass().equals(clazz)) {
                        return true;
                }
            }
        }
        return false;
    }

    private TemplQueryField convertField(Field field) {
        final int modifiers = field.getModifiers();
        if(Modifier.isFinal(modifiers)) {
            return null;
        }
        
        if(!field.isAccessible()) {
            field.setAccessible(true);
        }
        
        Class<?> typeClazz = field.getType();
        TemplQueryField templQueryField = null;
        
        if (typeClazz.isPrimitive() || typeClazz.equals(boolean.class)
                || isWrapped(typeClazz)
                || typeClazz.equals(String.class) || typeClazz.isArray()
                || typeClazz.isEnum()) {
            // do nothing
        } else if (Collection.class.isAssignableFrom(typeClazz)
                || Map.class.isAssignableFrom(typeClazz)) {
            // collection
            FetchType fetchType = getFetchType4Set(field);
            
            if (null != fetchType && fetchType.equals(FetchType.LAZY)) {
                templQueryField = createField4Collection(field);
            }
        } else if (HmBo.class.isAssignableFrom(typeClazz)) {
            // Hm BO
            final FetchType fetchType = getFetchType4HmBo(field);
            if(null != fetchType && fetchType.equals(FetchType.LAZY)) {
                templQueryField = createField4HmBo(field, typeClazz);
            }
        }
        return templQueryField;
    }

    private FetchType getFetchType4Set(Field field) {
        FetchType fetchType = null;
        if (null != field.getAnnotation(ManyToMany.class)) {
            fetchType = field.getAnnotation(ManyToMany.class).fetch();
        } else if (null != field.getAnnotation(ElementCollection.class)) {
            fetchType = field.getAnnotation(ElementCollection.class).fetch();
        } else if (null != field.getAnnotation(OneToMany.class)) {
            fetchType = field.getAnnotation(OneToMany.class).fetch();
        }
        return fetchType;
    }

    private FetchType getFetchType4HmBo(Field field) {
        FetchType fetchType = null;
        if(null != field.getAnnotation(ManyToOne.class)) {
            fetchType = field.getAnnotation(ManyToOne.class).fetch();
        } else if(null != field.getAnnotation(OneToOne.class)) {
            fetchType = field.getAnnotation(OneToOne.class).fetch();
        }
        return fetchType;
    }

    private boolean isWrapped(Class<?> typeClazz) {
        if(typeClazz.equals(Boolean.class)
                || typeClazz.equals(Character.class)
                || typeClazz.equals(Byte.class)
                || typeClazz.equals(Short.class)
                || typeClazz.equals(Integer.class)
                || typeClazz.equals(Long.class)
                || typeClazz.equals(Float.class)
                || typeClazz.equals(Double.class)
                || typeClazz.equals(Void.class)) {
            return true;
        }
        return false;
    }

    private TemplQueryField createField4HmBo(Field field, Class<?> typeClazz) {
        TemplQueryField templQueryField;
        templQueryField = new TemplQueryField();
        templQueryField.setObject(true);
        templQueryField.setCollection(false);
        templQueryField.setName(field.getName());
        
        templQueryField.setObjectClass(typeClazz);
        return templQueryField;
    }

    private TemplQueryField createField4Collection(Field field) {
        TemplQueryField templQueryField;
        templQueryField = new TemplQueryField();
        templQueryField.setObject(false);
        templQueryField.setCollection(true);
        templQueryField.setName(field.getName());
        return templQueryField;
    }

    public void setIgnoredClassFields(List<Class<?>> ignoredClassFields) {
        this.ignoredClassFields = ignoredClassFields;
    }

}
