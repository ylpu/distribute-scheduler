package com.yl.distribute.scheduler.client.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.yl.distribute.scheduler.common.bean.JobConf;

/**
 * 解析jobplan.xml病上传
 * @author asus
 *
 */
public class JobParser {
    
    private static final String JOB_ID = "jobId";
    private static final String JOB_DEPENDS = "depends";
    
    private Set<String> visited = new HashSet<String>();
    
    private File jobPlanFile = null;
    
    private InputStream is = null;
    
    private String content = null;
    
    public JobParser(InputStream is) {
        this.is = is;
    }  
    
    public JobParser(String content) {
        this.content = content;
    }  
    
    public JobParser(File jobPlanFile) {
        this.jobPlanFile = jobPlanFile;
    }     
    
    public Element readFile() {
        Element element = null;
        try {
            element = readStream(new FileInputStream(jobPlanFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return element;
    }
    
    public Element readString() {
        return readStream(new ByteArrayInputStream(content.getBytes()));
    }
    
    public Element readStream() {
        return readStream(is);
    }
    
    public Element readStream(InputStream is) {
        SAXReader reader = new SAXReader();
        Element rootElement = null;
        try {
            Document document = reader.read(is);
            rootElement = document.getRootElement();             
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return rootElement;
    }
    /**
     * 生成root任务链
     * @param rootElement
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JobConf getRootJob(Element rootElement){
        JobConf job = null;
        Iterator rootIt = rootElement.elementIterator();          
        
        while(rootIt.hasNext()){
            job = new JobConf();
            Element jobElement = (Element) rootIt.next();
            List<Attribute> attributes = jobElement.attributes();
            String jobId = "";
            for(Attribute attribute : attributes){
                if(attribute.getName().equals(JOB_ID)){
                    jobId = attribute.getValue();
                    job.setJobId(jobId);
                }
                if(attribute.getName().equals(JOB_DEPENDS)){
                    String depends = attribute.getValue();
                    //depends为空表明是最上层任务
                    if(StringUtils.isBlank(depends)) {
                        job.getJobReleation().setParentJobs(null);
                        job.getJobReleation().setChildJobs(getChildJobs(job,rootElement));
                        break;
                    }
                }
            }   
            break;
        }
        return job;
    }
    /**
     * 创建任务之间的依赖关系
     * @param currentJob
     * @param rootElement
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<JobConf> getChildJobs(JobConf currentJob,Element rootElement){
        Iterator jobit = rootElement.elementIterator();

        List<JobConf> childJobs = new ArrayList<JobConf>();
        JobConf job = null;
        while(jobit.hasNext()){            
            Element jobElement = (Element) jobit.next();
            List<Attribute> attributes = jobElement.attributes();
            String jobId = "";
            for(Attribute attribute : attributes){                
                if(attribute.getName().equals(JOB_ID)){
                    jobId = attribute.getValue();                    
                }
                if(attribute.getName().equals(JOB_DEPENDS)){
                    String depends = attribute.getValue();
                    if(StringUtils.isNotBlank(depends)) {
                        List<String> dependList = Arrays.asList(depends.split(","));
                        if(dependList.contains(currentJob.getJobId())) {
                            job = new JobConf();
                            job.setJobId(jobId);
                            childJobs.add(job);
                            currentJob.getJobReleation().setChildJobs(childJobs);
                            //为子任务设置父任务列表
                            for(String parentjobId : dependList) {
                                JobConf parentJob = new JobConf();
                                parentJob.setJobId(parentjobId);
                                job.getJobReleation().getParentJobs().add(parentJob);
                            }                            
                            job.getJobReleation().setChildJobs(getChildJobs(job,rootElement));
                        }
                    }        
                }
            } 
         }  
        if(childJobs.size() == 0) {
            return null;
        }else {
            return new ArrayList<JobConf>(childJobs);
        }
    }
    /**
     * 遍历文件中任务的个数
     * @param rootElement
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
    public int getFileJobsCount(Element rootElement){
        Set<String> fileJobs = new HashSet<String>();
        Iterator jobIt = rootElement.elementIterator();          
        while(jobIt.hasNext()){
            Element jobElement = (Element) jobIt.next();
            List<Attribute> attributes = jobElement.attributes();
            String jobId = "";
            for(Attribute attribute : attributes){
                if(attribute.getName().equals(JOB_ID)){
                    jobId = attribute.getValue();
                    fileJobs.add(jobId);
                }
            }                 
        }
        return fileJobs.size();
    }
    /**
     * 深度遍历根任务并计算任务个数
     * @param parentJob
     */
    public void visitJobs(JobConf parentJob) {
        if(parentJob == null ) {
            throw new RuntimeException("job can not empty");
        } 
        if(parentJob.getJobReleation().getParentJobs() == null) {
            visited.add(parentJob.getJobId());
        }
        if(parentJob.getJobReleation().getChildJobs() != null){
            List<JobConf> childs = parentJob.getJobReleation().getChildJobs();
            for(JobConf jobConf : childs) {
                visitJobs(jobConf);   
                visited.add(jobConf.getJobId());              
            }   
        }
    }    

    /**
     * 对比文件中任务个数和根任务遍历的任务个数来确定是否可以组成完整的依赖关系
     * @return
     */
    public boolean hasReleation(Element element,JobConf rootJob) {
        visitJobs(rootJob);
        int fileJobCount = getFileJobsCount(element);
        int jobCount = getVisited().size();
        if(jobCount == fileJobCount) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断任务链是否存在环形依赖
     * @param job
     * @return
     */
    public boolean detectCycle(JobConf job) {
        return detect(job, new HashSet<JobConf>());
    }
 
    private boolean detect(JobConf job, HashSet<JobConf> jobs) {
        if (job == null) {
            return false;
        } else if (jobs.contains(job)) {
            return true;
        }
        jobs.add(job);
        if(job.getJobReleation().getChildJobs() != null) {
            for (JobConf child : job.getJobReleation().getChildJobs()) {
                if (detect(child, jobs)) {
                    return true;
                }
            }
        }
        jobs.remove(job);
        return false;
    }   

    public Set<String> getVisited() {
        return visited;
    }    
}
