package com.yl.distribute.scheduler.client.test;

import java.io.File;
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

public class JobParseTest {
    


    public List<JobConf> getJobs(File file){

        SAXReader reader = new SAXReader();
        List<JobConf> jobList = new ArrayList<JobConf>();
        try {
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Iterator rootIt = rootElement.elementIterator();
            
            JobConf job = null;
            while(rootIt.hasNext()){

                job = new JobConf();
                Element jobElement = (Element) rootIt.next();
                //遍历bookElement的属性
                List<Attribute> attributes = jobElement.attributes();
                String jobName = "";
                for(Attribute attribute : attributes){                    
                    if(attribute.getName().equals("jobName")){
                        jobName = attribute.getValue();
                        job.setJobName(jobName);
                    }
                    if(attribute.getName().equals("depends")){
                        String depends = attribute.getValue();
                        job.getJobReleation().setParentJobs(getParentJobs(depends));
                        job.getJobReleation().setChildJobs(getChildJobs(job,rootElement));
                    }
                    
                }   
                jobList.add(job);
                job = null;

            }
        } catch (DocumentException e) {

            e.printStackTrace();
        }
        return jobList;
    }
    
    private List<JobConf> getParentJobs(String depends){
        Set<JobConf> jobs = new HashSet<JobConf>();
        if(StringUtils.isBlank(depends)) {
            return null;
        }else {
            String[] jobNames = depends.split(",");
            for(String jobName : jobNames) {
                JobConf jobConf = new JobConf();
                jobConf.setJobName(jobName);
                jobs.add(jobConf);
            }
            return new ArrayList<JobConf>(jobs);
        }
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<JobConf> getChildJobs(JobConf currentJob,Element rootElement){
        Iterator storeit = rootElement.elementIterator();

        Set<JobConf> childJobs = new HashSet<JobConf>();
        JobConf job = null;
        while(storeit.hasNext()){            
            Element jobElement = (Element) storeit.next();
            //遍历jobElement的属性
            List<Attribute> attributes = jobElement.attributes();
            String jobName = "";
            for(Attribute attribute : attributes){                
                if(attribute.getName().equals("jobName")){
                    jobName = attribute.getValue();                    
                }
                if(attribute.getName().equals("depends")){
                    String depends = attribute.getValue();
                    if(StringUtils.isNotBlank(depends)) {
                        List<String> dependList = Arrays.asList(depends.split(","));
                        if(dependList.contains(currentJob.getJobName())) {
                            job = new JobConf();
                            job.setJobName(jobName);
                            childJobs.add(job);
                            job.getJobReleation().getParentJobs().add(currentJob);
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
     * @param args
     */
    public static void main(String[] args) {
        File file = new File("src/main/resources/jobplan.xml");
        List<JobConf> jobList = new JobParseTest().getJobs(file);
        for(JobConf book : jobList){
            System.out.println(book);
        }
    }    

}
