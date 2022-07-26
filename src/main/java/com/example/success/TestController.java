package com.example.success;

import com.example.success.constant.Constants;
import com.example.success.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Value("${resource.test}")
    private String testStr;

    @GetMapping("")
    public String testAPI() {
        log.info("this is testAPI");

        String videoEncodingResponsePath = "E:\\NAS_INGEST01\\cms\\encoding\\processing_ott\\dash";
        File fileDir = new File(videoEncodingResponsePath);

        String[] directories = fileDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        if (directories.length == 0) {
            log.error("There is nothing in " + videoEncodingResponsePath);
        }
        System.out.println(Arrays.toString(directories));

        return "success";
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createJob(@RequestBody String cmsOutputBody) {
        // outputBody = "<job><name>" + sourceFileName + "</name></job>"
        log.info(String.format("createJob with cmsOutputBody: %s", cmsOutputBody));

        String uuid = UUID.randomUUID().toString();

        Job job = new Job();
        job.setId(uuid);
        job.setState(Constants.JobState.ENCODING.getState());
        job.setProgress("1%");

        log.info(String.format("create job with body: %s", job));

        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }
}
