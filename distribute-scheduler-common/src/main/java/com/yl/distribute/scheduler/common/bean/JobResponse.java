package com.yl.distribute.scheduler.common.bean;

import java.io.Serializable;

public class JobResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    //responseId和requestId相同
    private String jobId;
    private String runningServer;        
    private String jobStatus;
    private String errorOutputUrl;
    private String stdOutputUrl;

    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
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
