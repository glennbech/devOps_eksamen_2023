package com.example.s3rekognition.controller;


import com.example.s3rekognition.ppescan.PPEClassificationResponse;
import com.example.s3rekognition.ppescan.PPEResponse;
import com.example.s3rekognition.services.RekognitionService;
import com.example.s3rekognition.weaponscan.WeaponScanResponse;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;


@RestController
public class RekognitionController implements ApplicationListener<ApplicationReadyEvent> {


    private static Integer scanCount;
    private RekognitionService rekognitionService;

    MeterRegistry meterRegistry;
    private static final Logger logger = Logger.getLogger(RekognitionController.class.getName());

    @Autowired
    public RekognitionController(MeterRegistry meterRegistry, RekognitionService rekognitionService) {
        this.meterRegistry = meterRegistry;
        this.rekognitionService = rekognitionService;
    }


    @GetMapping(value = "/scan-weapon", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<WeaponScanResponse> scanForWeapon(@RequestParam String bucketName) {
        return ResponseEntity.ok(rekognitionService.scanForWeapons(bucketName));
    }


    @GetMapping(value = "/scan-ppe", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PPEResponse> scanForPPE(@RequestParam String bucketName) {
        PPEResponse ppeResponse = rekognitionService.scanForPPE(bucketName);
        scanCount += ppeResponse.getResults().stream().mapToInt(PPEClassificationResponse::getPersonCount).sum();
        return ResponseEntity.ok(ppeResponse);
    }


    /**
     * Called only once, when the event "application ready" comes.
     * (When everything else in code is ready, method is run)
     *
     * @param applicationReadyEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Gauge.builder("ppe_scan_count", scanCount, Integer::intValue).register(meterRegistry);

    }
}
