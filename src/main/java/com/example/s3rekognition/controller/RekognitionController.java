package com.example.s3rekognition.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.s3rekognition.PPEClassificationResponse;
import com.example.s3rekognition.PPEResponse;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RestController
public class RekognitionController implements ApplicationListener<ApplicationReadyEvent> {

    private final AmazonS3 s3Client;
    private final AmazonRekognition rekognitionClient;

    private static final Logger logger = Logger.getLogger(RekognitionController.class.getName());

    public RekognitionController() {
        this.s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
        this.rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
    }

    /**
     * This endpoint takes an S3 bucket name in as an argument, scans all the
     * Files in the bucket for Protective Gear Violations.
     * <p>
     *
     * @param bucketName
     * @return
     */
    @GetMapping(value = "/scan-ppe", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PPEResponse> scanForPPE(@RequestParam String bucketName) {
        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);

        // This will hold all of our classifications
        List<PPEClassificationResponse> classificationResponses = new ArrayList<>();

        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        List<String> requiredProtection = new ArrayList<>();

        // Iterate over each object and scan for PPE
        for (S3ObjectSummary image : images) {
            requiredProtection.add("FACE_COVER");
            logger.info("scanning " + image.getKey() + " for labels");

            // Scanning for labels and checking if label is a child of "tool".
            DetectLabelsRequest labelsRequest = new DetectLabelsRequest()
                    .withImage(new Image()
                            .withS3Object(new S3Object()
                                    .withBucket(bucketName)
                                    .withName(image.getKey())))
                    .withMaxLabels(10).withMinConfidence(80F);
            DetectLabelsResult labelsResult = rekognitionClient.detectLabels(labelsRequest);

            // Checks if any of the parents of all the labels is tool.
            // If label has "tool" as a parent, hand cover is added to required protection.
            List<Label> labels = labelsResult.getLabels();
            for (Label label : labels) {
                List<Parent> parents = label.getParents();
                if(isTool(parents)){
                    requiredProtection.add("HAND_COVER");
                }
            }
            logger.info("scanning " + image.getKey() + " for required protection " + requiredProtection);

            // Scanning image for required protection and set violation to true if the required protection is not found.
            DetectProtectiveEquipmentRequest request = new DetectProtectiveEquipmentRequest()
                    .withImage(new Image()
                            .withS3Object(new S3Object()
                                    .withBucket(bucketName)
                                    .withName(image.getKey())))
                    .withSummarizationAttributes(new ProtectiveEquipmentSummarizationAttributes()
                            .withMinConfidence(80f)
                            .withRequiredEquipmentTypes(requiredProtection));

            DetectProtectiveEquipmentResult result = rekognitionClient.detectProtectiveEquipment(request);

            // If the list with persons without required equipment is not empty, then a violation is there.
            boolean violation = !result.getSummary().getPersonsWithoutRequiredEquipment().isEmpty();

            logger.info("scanning " + image.getKey() + ", violation result " + violation);
            // Categorize the current image as a violation or not.
            int personCount = result.getPersons().size();
            PPEClassificationResponse classification = new PPEClassificationResponse(image.getKey(), personCount, violation);
            classificationResponses.add(classification);

            // Clear the required protection for next image.
            requiredProtection.clear();
        }
        PPEResponse ppeResponse = new PPEResponse(bucketName, classificationResponses);
        return ResponseEntity.ok(ppeResponse);
    }



    // Check if label is under type "tool".
    private static boolean isTool(List<Parent> parents) {
        if (!parents.isEmpty()) {

            for (Parent parent : parents) {
                if (parent.getName().equalsIgnoreCase("tool")) {
                    logger.info("person is holding tool");
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    }
}
