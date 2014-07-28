package com.ah.util.fileupload;

public class FileUploadUtil {
    
    /**
     * Fixed CFD-378: avoid upload the executable file <br>
     * Now below extensions are not allowed to upload: [".jsp", ".cgi", ".pl", ".py", ".rb", ".asp", ".php"]
     * 
     * @author Yunzhi Lin
     * - Time: May 4, 2014 4:24:10 PM
     * @return Boolean
     */
    public static boolean containsNotAcceptableExtension(String filename) {
        final String[] disallowedExtensions = new String[] {".jsp", ".cgi", ".pl", ".py", ".rb", ".asp", ".php"};
        for (String extensions : disallowedExtensions) {
            if(filename.toLowerCase().endsWith(extensions)) {
                return true;
            }
        }
         return false;
    }
    
    /**
     * Check the file name to avoid path traversal attack. <br>
     * In Unix-like system, uses the "../" characters for directly traversal. <br>
     * And also need to handle the NULL characters (%00) which will bypass rudimentary file extension checks.  
     * 
     * @author Yunzhi Lin
     * - Time: May 7, 2014 2:48:41 PM
     * @param filename
     * @return Boolean
     */
    public static boolean isPathTraversal(String filename) {
        final String lowerCaseFileName = filename.toLowerCase();
        if(lowerCaseFileName.contains("../")
                || lowerCaseFileName.contains("%2e%2e%2f") // "../"
                || lowerCaseFileName.contains("%2e%2e/") // "../"
                || lowerCaseFileName.contains("..%2f") // "../"
                || lowerCaseFileName.contains("%00")) { // "NULL"
            return true;
        }
        return false;
    }
}
