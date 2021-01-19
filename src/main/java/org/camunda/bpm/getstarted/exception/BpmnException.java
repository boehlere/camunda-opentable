package org.camunda.bpm.getstarted.exception;

public class BpmnException extends RuntimeException {
        private String errorCode;

        public BpmnException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public BpmnException(String errorCode) {
            super();
            this.errorCode = errorCode;
        }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
