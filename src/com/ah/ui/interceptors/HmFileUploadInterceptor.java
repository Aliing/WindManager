package com.ah.ui.interceptors;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.FileUploadInterceptor;

import com.ah.util.fileupload.FileUploadUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;

public class HmFileUploadInterceptor extends FileUploadInterceptor {

    private static final long serialVersionUID = -8897032373068369470L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(getTextMessage("struts.messages.bypass.request", new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }

            return invocation.invoke();
        }

        ValidationAware validation = null;

        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;

        if (multiWrapper.hasErrors()) {
            for (String error : multiWrapper.getErrors()) {
                if (validation != null) {
                    validation.addActionError(error);
                }
            }
        }

        // bind allowed Files
        Enumeration<?> fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            // get the value of this input tag
            String inputName = (String) fileParameterNames.nextElement();

            // get the content type
            String[] contentType = multiWrapper.getContentTypes(inputName);

            if (isNonEmpty(contentType)) {
                // get the name of the file from the input tag
                String[] fileName = multiWrapper.getFileNames(inputName);

                if (isNonEmpty(fileName)) {
                    // get a File object for the uploaded File
                    File[] files = multiWrapper.getFiles(inputName);
                    if (files != null && files.length > 0) {
                        List<File> acceptedFiles = new ArrayList<File>(files.length);
                        List<String> acceptedContentTypes = new ArrayList<String>(files.length);
                        List<String> acceptedFileNames = new ArrayList<String>(files.length);
                        String contentTypeName = inputName + "ContentType";
                        String fileNameName = inputName + "FileName";

                        for (int index = 0; index < files.length; index++) {
                            if (acceptFile(action, files[index], fileName[index], contentType[index], inputName, validation)) {
                                if(acceptFile2(action, files[index], fileName[index], contentType[index], inputName, validation)) {
                                    acceptedFiles.add(files[index]);
                                    acceptedContentTypes.add(contentType[index]);
                                    acceptedFileNames.add(fileName[index]);
                                } else {
                                    // not allow to upload unacceptable file
                                    return "error503";
                                }
                            }
                        }

                        if (!acceptedFiles.isEmpty()) {
                            Map<String, Object> params = ac.getParameters();

                            params.put(inputName, acceptedFiles.toArray(new File[acceptedFiles.size()]));
                            params.put(contentTypeName, acceptedContentTypes.toArray(new String[acceptedContentTypes.size()]));
                            params.put(fileNameName, acceptedFileNames.toArray(new String[acceptedFileNames.size()]));
                        }
                    }
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(getTextMessage(action, "struts.messages.invalid.file", new String[]{inputName}));
                    }
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(getTextMessage(action, "struts.messages.invalid.content.type", new String[]{inputName}));
                }
            }
        }

        // invoke action
        return invocation.invoke();
    }
    
    private boolean acceptFile2(Object action, File file, String filename,
            String contentType, String inputName, ValidationAware validation) {
        if (FileUploadUtil.containsNotAcceptableExtension(filename)) {
            final String errMsg = getTextMessage(action, "error.common.file.upload.invalid.extension", new String[]{filename});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }
            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
            return false;
        } else if (FileUploadUtil.isPathTraversal(filename)) {
            final String errMsg = getTextMessage(action, "error.common.file.upload.invalid.pathtraversal", new String[]{filename});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }
            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
            return false;
        }
        return true;
    }
    
    private boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (int index = 0; index < objArray.length && !result; index++) {
            if (objArray[index] != null) {
                result = true;
            }
        }
        return result;
    }
}
