package com.bucketlist.project.exceptions;

public class PermissionDeniedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    private String action;

    public PermissionDeniedException() {
    }


    public PermissionDeniedException(String field, Long fieldId, String resourceName, String action) {
        super(String.format("%s: %s does not have permission to %s %s", field, fieldId, action, resourceName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
        this.action = action;
    }
}
