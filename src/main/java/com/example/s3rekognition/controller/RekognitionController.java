package com.example.s3rekognition.controller;


import com.example.s3rekognition.ppescan.PPEClassificationResponse;
import com.example.s3rekognition.ppescan.PPEResponse;
import com.example.s3rekognition.services.RekognitionService;
import com.example.s3rekognition.weaponscan.WeaponScanResponse;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;


@RestController
public class RekognitionController {


    private RekognitionService rekognitionService;
    private static final Logger logger = Logger.getLogger(RekognitionController.class.getName());

    @Autowired
    public RekognitionController(RekognitionService rekognitionService) {
        this.rekognitionService = rekognitionService;
    }


    @GetMapping(path = "/enter", consumes = "*/*", produces = "application/json")
    public ResponseEntity<String> enteringConstructionAreaScan(@RequestParam String bucketName) {
        logger.info("Entering construction area with " + bucketName );
        return ResponseEntity.ok(rekognitionService.enterConstructionArea(bucketName) + " Entered");
    }

    @GetMapping(path = "/exit", consumes = "*/*", produces = "application/json")
    public ResponseEntity<String> exitingConstructionAreaScan(@RequestParam String bucketName) {
        logger.info("Exiting construction area with " + bucketName );
        return ResponseEntity.ok(rekognitionService.exitConstructionArea(bucketName) + " Exited");
    }

    @Timed(value = "scan_time")
    @GetMapping(value = "/scan-weapon", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<WeaponScanResponse> scanForWeapon(@RequestParam String bucketName) {
        logger.info("Scanning for weapon with " + bucketName );
        return ResponseEntity.ok(rekognitionService.scanForWeapons(bucketName));
    }

    @Timed(value = "scan_time")
    @GetMapping(value = "/scan-ppe", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PPEResponse> scanForPPE(@RequestParam String bucketName) {
        logger.info("Scanning for PPE with " + bucketName );
        return ResponseEntity.ok(rekognitionService.scanForPPE(bucketName));
    }

}
