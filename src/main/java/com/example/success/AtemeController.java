package com.example.success;

import com.example.success.constant.Constants;
import com.example.success.data.DataCenter;
import com.example.success.data.JobData;
import com.example.success.entity.Job;
import com.example.success.service.DashEncodingService;
import com.example.success.service.EncodingService;
import com.example.success.service.HlsEncodingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static com.example.success.service.EncodingService.saveJobDataToJobDataList;
import static com.example.success.service.EncodingService.saveJobToJobList;

@RestController
@RequestMapping("/restapi/jobs")
@Slf4j
public class AtemeController {

    @Autowired
    HlsEncodingService hlsEncodingService;

    @Autowired
    DashEncodingService dashEncodingService;

    @Value("${resource.hls.file}")
    private String m3u8FileResource;

    @Value("${resource.dash.folder}")
    private String dashResource;
    // data


    @Autowired
    DataCenter dataCenter;

    @Autowired
    JobData jobData;

    @GetMapping("/dash")
    public void testEncode() throws Exception {
        String sourceFilename = "E:\\NAS_INGEST01\\cms\\repository\\asset\\202208\\test\\ott_dash\\test.mpd";
        dashEncodingService.encode(sourceFilename, dashResource);
    }


    // API dummy for getJobByUUID(uuid)
    @GetMapping(value = "/{uuid}", produces = "text/xml")
    public ResponseEntity<?> getAtemeStateByUuid(@PathVariable String uuid) throws Exception {
        // get AtemeJob by uuId;
        log.info(String.format("[API getAtemeStateByUuid] job list: %s", dataCenter.getJobList().toString()));

        Job job = null;
        if (!Strings.isEmpty(uuid)) {
            job = EncodingService.getJobByUuid(uuid, dataCenter.getJobList());
        } else {
            throw new Exception(String.format("Job with uuid={%s} is NULL", uuid));
        }

        log.info(String.format("[API getAtemeStateByUuid] with uuid= %s, response Job {%s}", uuid, job.toString()));

        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    // 1. create Job
    @PostMapping(value = "", produces = "text/xml")
    public ResponseEntity<?> createJob(@RequestBody String cmsOutputBody) {
        // outputBody = "<job><name>" + sourceFileName + "</name></job>"
        log.info(String.format("[API createJob] createJob with cmsOutputBody: %s", cmsOutputBody));

        String uuid = UUID.randomUUID().toString();

        Job job = new Job();
        job.setId(uuid);
        job.setState(Constants.JobState.ENCODING.getState());
        job.setProgress("1%");

        log.info(String.format("create job with body: %s", job));

        JobData jobData = new JobData();
        jobData.setJob(job);
        jobData.setId(job.getId());


        dataCenter.getJobList().add(job);
        dataCenter.getJobDataList().add(jobData);

        dataCenter.setJobList(dataCenter.getJobList());
        dataCenter.setJobDataList(dataCenter.getJobDataList());

        log.info(String.format("[API createJob] data center: : %s", dataCenter.toString()));
        log.info(String.format("[API createJob] job list: %s", dataCenter.getJobList().toString()));
        log.info(String.format("[API createJob] job: {%s}", job.toString()));

        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    } // expected code: 201

    // 2. add preset
    @PostMapping("/{uuid}/preset")
    public ResponseEntity<?> addPreset(@PathVariable String uuid, @RequestBody String cmsOutputBody) throws ParserConfigurationException, IOException, SAXException {
        //outputBody = "<preset><name>" + encodingJobQueue.getPresetName() + "</name></preset>"
        // get presetName from cmsOutput body:
        log.info(String.format("[API addPreset] addPreset with uuid %s, cmsOutputBody %s", uuid, cmsOutputBody));

        String presetName = getValueByTagName(cmsOutputBody, "name");

        log.info(String.format("[API addPreset] addPreset with uuid %s, presetName %s", uuid, presetName));


        boolean isHlsEncode = true;
        // ott
        if (presetName.contains("dash") || presetName.contains("DASH")) {
            // encoding by dash
            isHlsEncode = false;
        }
        // todo: encoding by stb --- do nothing: processing with .ts file

        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        jobData.setPresetName(presetName);
        jobData.setHlsEncode(isHlsEncode);

        saveJobDataToJobDataList(jobData, dataCenter.getJobDataList());
        dataCenter.setJobDataList(dataCenter.getJobDataList());
        // stb
        // ...
        log.info(String.format("[API addPreset] Job Data {%s}", EncodingService.getJobDataById(uuid, dataCenter.getJobDataList())));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 3. set output
    @PostMapping("/{uuid}/output")
    public ResponseEntity<?> setOutput(@PathVariable String uuid, @RequestBody String cmsOutputBody) throws ParserConfigurationException, IOException, SAXException {
        // <output><file>" + sourceFileName + "</file></output>
        String sourceFileName  = getValueByTagName(cmsOutputBody,"file");
        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        jobData.setSourceFileName(sourceFileName);


        saveJobDataToJobDataList(jobData, dataCenter.getJobDataList());
        dataCenter.setJobDataList(dataCenter.getJobDataList());
        // stb
        // ...
        log.info(String.format("[API setOutput] Job Data {%s}", EncodingService.getJobDataById(uuid, dataCenter.getJobDataList())));
        log.info(String.format("[API setOutput] set Output to Ateme, uuid %s, cmsOutputbody: %s", uuid, cmsOutputBody));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 4. add segment
    @PostMapping("/{uuid}/segments")
    public ResponseEntity<?> addSegment(@PathVariable String uuid) {
        log.info(String.format("[Add addSegment] addSegment to Ateme, uuid %s ", uuid));
        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        log.info(String.format("[Add addSegment] JobData %s", jobData));
        return ResponseEntity.status(HttpStatus.CREATED).body(uuid); // 201
    }
    // 5. add input to segment
    @PostMapping("/{uuid}/segments/1/inputs")
    public ResponseEntity<?> addInputToSegment(@PathVariable String uuid, @RequestBody String cmsOutputBody) {
        // outputBody = "<input><uri>" + sourceFileName + "</uri></input>"
        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        log.info(String.format("[API addInputToSegment] addInputToSegment to Ateme, uuid %s cmsRequestBody %s", uuid, cmsOutputBody));
        log.info(String.format("[API addInputToSegment] JobData %s", jobData));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 6. add drm
    @PostMapping("/{uuid}/drm")
    public ResponseEntity<?> addDrm(@PathVariable String uuid, @RequestBody String cmsRequestBody) {
        // 				requestBody = "<drm type=\"apple_encryption\"><apple_encryption mode=\"CBC\" autogenerated=\"false\"><key mode=\"AES_128\" type=\"Hexa\">"
        //						+ "<data>" + kgsKey.getString("key") + "</data>" + "</key>" + "<uri>" + kgsKey.getString("url")
        //						+ "</uri>" + "</apple_encryption></drm>";

        // String.format("<drm type=\"%s\"><%s><resource_id>%s</resource_id></%s></drm>",
        //						encryptionName, encryptionName, jobResourceId, encryptionName);

        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        log.info(String.format("[API addDrm] addDrm to Ateme, uuid %s, cmsRequestBody %s", uuid, cmsRequestBody));
        log.info(String.format("[API addDrm] addDrm to Ateme: JobData %s", jobData.toString()));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }

    // 7. start job
    @PostMapping("/{uuid}/state")
    public ResponseEntity<?> startJob(@PathVariable String uuid ) throws Exception {
        log.info(String.format("[API startJob] startJob with uuid %s", uuid));
        // encode here:
        JobData jobData = EncodingService.getJobDataById(uuid, dataCenter.getJobDataList());
        log.info(String.format("[API startJob] JobData: %s", jobData.toString()));

        if (jobData.isHlsEncode()) {
            log.info(String.format("[API startJob] UUID {%s} start encode: HLS," +
                    "\n{sourceFileName: %s, m3u8FileResource: %s}", uuid, jobData.getSourceFileName(), m3u8FileResource));
            hlsEncodingService.encode(jobData.getSourceFileName(), m3u8FileResource);
        } else {
            log.info(String.format("[API startJob] UUID {%s} start encode: DASH, " +
                    "\n{sourceFileName: %s, dashResource: %s}", uuid, jobData.getSourceFileName(), dashResource));
            dashEncodingService.encode(jobData.getSourceFileName(), dashResource);
        }
        Job job  = EncodingService.getJobByUuid(uuid, dataCenter.getJobList());
        job.setState(Constants.JobState.COMPLETE.getState());
        job.setProgress(Constants.JobState.COMPLETE.getProgress());

        // update:
        saveJobToJobList(job, dataCenter.getJobList());
        saveJobDataToJobDataList(jobData, dataCenter.getJobDataList());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }

    private static String getValueByTagName(String cmsOutputBody, String tagName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource src = new InputSource();
        src.setCharacterStream(new StringReader(cmsOutputBody));

        Document doc = builder.parse(src);
        String value = doc.getElementsByTagName(tagName).item(0).getTextContent();

        return  value;
    }

}
