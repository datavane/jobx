package com.jobxhub.service;


import com.jobxhub.service.api.JobService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JobServiceTest extends JobXServiceTestApplication {

    @Autowired
    private JobService jobService;

    @Test
    public void testSaveJob(){
        System.out.println();
        System.out.println(jobService);
        System.out.println();
        System.out.println("xxxxxx");
    }

}
