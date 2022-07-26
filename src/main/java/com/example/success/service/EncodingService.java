package com.example.success.service;



import com.example.success.constant.Constants;
import com.example.success.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;

@Slf4j
public class EncodingService {

    public static Job getJobByUuid(String uuid, List<Job> jobList) throws Exception {
        Job jobRet = null;
        if (!jobList.isEmpty()) {
            for (Job job : jobList) {
                jobRet = job.getId() == uuid ? job : null;
            }
        }
        if (Objects.isNull(jobRet)) {
            throw new Exception(String.format("Not found Job with uuid: %s", uuid));
        }

        return jobRet;
    }

    public static void saveJobToDB(Job job, List<Job> jobList) {
        if (jobList.isEmpty()) { // add a new job
            jobList.add(job);
            log.info("add more job [%s] to list successfully", job);
        } else { // update job
            int count = 0;
            for (int i = 0; i < jobList.size(); i++) {
                if (Objects.equals(jobList.get(i).getId(), job.getId())) {
                    count ++;
                    // update:
                    jobList.set(i, job);
                    log.info(String.format("Update job [%s] to list successfully", job));
                    break;
                    }
                }
            if (count == 0) {
                // add job:
                jobList.add(job);
                log.info("add more job [%s] to list successfully", job);
            }
        }
    }

    public static Job updateProgress(Job job, List<Job> jobList) {
        String originalProgress = job.getProgress();
        String initProgress ;
        initProgress = Strings.isBlank(job.getProgress()) ? "20" : job.getProgress();
        int progress;
        progress = Integer.valueOf(initProgress);
        int proIncre;
        proIncre = progress < 100 ? (progress+20) : 100;

        if (proIncre >= 100) {
            proIncre = 100;
            job.setState(Constants.JobState.COMPLETE.getState());
        }
        job.setProgress(String.valueOf(proIncre));
        //update to jobList:
        saveJobToDB(job, jobList);
        log.info(String.format("Update job with uuid %s from % to %s successfully", job.getId(), originalProgress, job.getProgress()));
        return job;
    }
}
