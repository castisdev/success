package com.example.success.data;

import com.example.success.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JobData {
    private String id; // = jobId
    private Job job;
    private String sourceFileName;
    private String presetName;
    private boolean isDashEncode;
    private boolean isHybridEncode;
}
