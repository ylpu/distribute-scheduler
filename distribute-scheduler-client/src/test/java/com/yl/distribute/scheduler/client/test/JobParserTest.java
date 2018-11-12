package com.yl.distribute.scheduler.client.test;

import java.io.File;
import org.dom4j.Element;
import org.junit.Test;

import com.yl.distribute.scheduler.client.schedule.JobParser;
import com.yl.distribute.scheduler.common.bean.JobRequest;

public class JobParserTest {
    
    @Test
    public void testJobParser() {
        File file = new File("src/main/resources/jobplan.xml");
        JobParser parser = new JobParser(file);
        Element element = parser.readFile();
        JobRequest rootJob = parser.getRootJob(element);
        boolean hasReleation = parser.hasReleation(element,rootJob);        
        System.out.println(hasReleation);
    }    
}
