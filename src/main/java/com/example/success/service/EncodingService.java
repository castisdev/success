package com.example.success.service;



import com.example.success.constant.Constants;
import com.example.success.data.JobData;
import com.example.success.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;

@Slf4j
public class EncodingService {

    public static Job getJobByUuid(String uuid, List<Job> jobList) throws Exception {
        Job jobRet = null;
        if (Objects.nonNull(jobList)) {
            if (!jobList.isEmpty()) {
                for (Job job : jobList) {
                    if (job.getId().equalsIgnoreCase(uuid)) {
                        jobRet = job;
                        break;
                    }
                }
            } else {
                throw new Exception(String.format("JobList is empty"));
            }
        } else {
            throw new Exception(String.format("JobList is null"));
        }

        if (Objects.isNull(jobRet)) {
            throw new Exception(String.format("job{uuid=%s} is null", uuid));
        }

        return jobRet;
    }

    public static JobData getJobDataById(String id, List<JobData> jobDataList) throws Exception {

        if (!id.isEmpty()) {
            if (Objects.nonNull(jobDataList))  {
                for (JobData jobData1 : jobDataList) {
                    if (jobData1.getId().equalsIgnoreCase(id)) {
                        return jobData1;
                    }
                }
            } else {
                throw new IllegalArgumentException("jobDataList is null");
            }
        } else {
            throw new IllegalArgumentException(String.format("id = %s is invalid", id));
        }
        throw new Exception(String.format("Job (id = %s) is not found", id));
    }

    public static void saveJobToJobList(Job job, List<Job> jobList) {
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

    public static void saveJobDataToJobDataList(JobData jobData, List<JobData> jobDataList) {
        if (jobDataList.isEmpty()) {
            jobDataList.add(jobData);
            log.info("add more jobdata [%s] to list successfully", jobData);
        } else { // update job data
            int count = 0;
            for (int i = 0; i < jobDataList.size(); i++) {
                if (Objects.equals(jobDataList.get(i).getId(), jobData.getId())) {
                    count ++;
                    // update:
                    jobDataList.set(i, jobData);
                    log.info(String.format("Update job data [%s] to list successfully", jobData));
                    break;
                }
            }
            if (count == 0) {
                // add job data:
                jobDataList.add(jobData);
                log.info("add more job data [%s] to list successfully", jobData);
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
        saveJobToJobList(job, jobList);
        log.info(String.format("Update job with uuid %s from % to %s successfully", job.getId(), originalProgress, job.getProgress()));
        return job;
    }
}
