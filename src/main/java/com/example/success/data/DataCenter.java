package com.example.success.data;

import com.example.success.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Data
public class DataCenter {
    private List<JobData> jobDataList;
    private List<Job> jobList;

    public DataCenter() {
        if (Objects.isNull(jobDataList)) {
            jobDataList = new ArrayList<>();
        }

        if (Objects.isNull(jobList)) {
            jobList = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "DataCenter{" +
                "jobDataList=" + jobDataList +
                ", jobList=" + jobList +
                '}';
    }
}
