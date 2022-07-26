package com.example.success;

import com.example.success.constant.Constants;
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

    public static String sharedSourceFileName = "";
    public static String sharedPresetName = "";
    public static boolean isHlsEncode = true;

    public static List<Job> jobList = new ArrayList<Job>();

    @GetMapping("")
    public ResponseEntity<?> testApi() throws Exception {
        Random rn = new Random();
        int t = 10 + rn.nextInt(99 - 10 + 1);

        // test encoding:
        String sourceFileName = "E:\\NAS_INGEST01\\cms\\repository\\asset\\202207\\330919\\330919_44V.mp4";

        hlsEncodingService.encode(sourceFileName, m3u8FileResource);
        dashEncodingService.encode(sourceFileName, dashResource); // ok

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }




    // API dummy for getJobByUUID(uuid)
    @GetMapping(value = "/{uuid}", produces = "text/xml")
    public ResponseEntity<?> getAtemeStateByUuid(@PathVariable String uuid) throws Exception {
        // get AtemeJob by uuId;

        Job job = EncodingService.getJobByUuid(uuid, jobList);
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    // 1. create Job
    @PostMapping(value = "", produces = "text/xml")
    public ResponseEntity<?> createJob(@RequestBody String cmsOutputBody) {
        // outputBody = "<job><name>" + sourceFileName + "</name></job>"
        log.info(String.format("createJob with cmsOutputBody: %s", cmsOutputBody));

        String uuid = UUID.randomUUID().toString();

        Job job = new Job();
        job.setId(uuid);
        job.setState(Constants.JobState.ENCODING.getState());
        job.setProgress("1%");

        log.info(String.format("create job with body: %s", job));
        jobList.add(job);

        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    } // expected code: 201
    // 2. add preset
    @PostMapping("/{uuid}/preset")
    public ResponseEntity<?> addPreset(@PathVariable String uuid, @RequestBody String cmsOutputBody) throws ParserConfigurationException, IOException, SAXException {
        //outputBody = "<preset><name>" + encodingJobQueue.getPresetName() + "</name></preset>"
        // get presetName from cmsOutput body:
        log.info(String.format("addPreset with uuid %s, cmsOutputBody %s", uuid, cmsOutputBody));
        sharedPresetName = getValueByTagName(cmsOutputBody, "name");
        String presetName = sharedPresetName;
        // ott
        if (presetName.contains("dash") || presetName.contains("DASH")) {
            // encoding by dash
            isHlsEncode = false;
        }
        // stb
        // ...

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 3. set output
    @PostMapping("/{uuid}/output")
    public ResponseEntity<?> setOutput(@PathVariable String uuid, @RequestBody String cmsOutputBody) throws ParserConfigurationException, IOException, SAXException {
        // <output><file>" + sourceFileName + "</file></output>
        sharedSourceFileName = getValueByTagName(cmsOutputBody,"file");
        String sourceFileName = sharedSourceFileName;

        log.info(String.format("set Output to Ateme, uuid %s, cmsOutputbody: %s", uuid, cmsOutputBody));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 4. add segment
    @PostMapping("/{uuid}/segments")
    public ResponseEntity<?> addSegment(@PathVariable String uuid, @RequestBody String cmsOutputBody) {
        log.info("addSegment to Ateme, uuid %s cmsRequestBody %s", uuid, cmsOutputBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(uuid); // 201
    }
    // 5. add input to segment
    @PostMapping("/{uuid}/segments/1/inputs")
    public ResponseEntity<?> addInputToSegment(@PathVariable String uuid, @RequestBody String cmsOutputBody) {
        // outputBody = "<input><uri>" + sourceFileName + "</uri></input>"
        log.info("addInputToSegment to Ateme, uuid %s cmsRequestBody %s", uuid, cmsOutputBody);
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

        log.info("addDrm to Ateme, uuid %s cmsRequestBody %s", uuid, cmsRequestBody);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(uuid);
    }
    // 7. start job
    @PostMapping("/{uuid}/state?value=pending")
    public ResponseEntity<?> startJob(@PathVariable String uuid ) throws Exception {
        log.info(String.format("addPreset with uuid %s", uuid));
        // encode here:
        if (isHlsEncode) {
            hlsEncodingService.encode(sharedSourceFileName, m3u8FileResource);
        } else {
            dashEncodingService.encode(sharedSourceFileName, dashResource);
        }
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
