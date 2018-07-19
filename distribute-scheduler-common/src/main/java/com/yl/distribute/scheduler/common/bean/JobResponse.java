package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class JobResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private String responseId;
    private String runningServer;        
    private String jobStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;

    public String getResponseId() {
        return responseId;
    }
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }
    public String getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
    public String getErrorOutputUrl() {
        return errorOutputUrl;
    }
    public void setErrorOutputUrl(String errorOutputUrl) {
        this.errorOutputUrl = errorOutputUrl;
    }
    public String getStdOutputUrl() {
        return stdOutputUrl;
    }
    public void setStdOutputUrl(String stdOutputUrl) {
        this.stdOutputUrl = stdOutputUrl;
    }
    public String getRunningServer() {
        return runningServer;
    }
    public void setRunningServer(String runningServer) {
        this.runningServer = runningServer;
    }       
}
