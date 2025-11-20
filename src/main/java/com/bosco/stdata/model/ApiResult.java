package com.bosco.stdata.model;


public class ApiResult {
    public Boolean success;
    public String errorMessage;
    public String resultId;
    public Boolean warning;
    public String message;  // used for warning or success as needed.

    
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getResultId() {
        return resultId;
    }
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
    public Boolean getWarning() {
        return warning;
    }
    public void setWarning(Boolean warning) {
        this.warning = warning;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    

}