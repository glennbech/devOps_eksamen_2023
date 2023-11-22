package com.example.s3rekognition.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.s3rekognition.ppescan.PPEClassificationResponse;
import com.example.s3rekognition.ppescan.PPEResponse;
import com.example.s3rekognition.weaponscan.WeaponClassificationResponse;
import com.example.s3rekognition.weaponscan.WeaponScanResponse;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RekognitionService implements ApplicationListener<ApplicationReadyEvent> {


    private static int personsInConstructionArea = 0;

    private MeterRegistry meterRegistry;
    private AmazonS3 s3Client;
    private AmazonRekognition rekognitionClient;


    public RekognitionService(MeterRegistry meterRegistry) {
        this.s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
        this.rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
        this.meterRegistry = meterRegistry;
    }

    /**
     * Iterate through the bucket objects and counting the persons.
     * @param bucketName
     * @return Integer, the count of how many persons was scanned entering area.
     */
    public Integer enterConstructionArea(String bucketName) {
        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);
        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        int scanCount = 0;
        for (S3ObjectSummary image : images) {
            scanCount += countPersonsInImage(bucketName, image.getKey());
        }

        personsInConstructionArea += scanCount;

        return scanCount;
    }

    /**
     * Iterate through the bucket objects and counting the persons.
     * @param bucketName
     * @return Integer, the count of how many persons was scanned exiting area.
     */
    public Integer exitConstructionArea(String bucketName) {
        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);
        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        int scanCount = 0;
        for (S3ObjectSummary image : images) {
            scanCount += countPersonsInImage(bucketName, image.getKey());
        }

        personsInConstructionArea -= scanCount;

        return scanCount;
    }

    /**
     * Scan all images in bucket for PPE violations.
     * FACE_COVER is required, and if X holds a tool, HAND_COVER is also added to required equipment.
     *
     * @param bucketName
     * @return
     */
    public PPEResponse scanForPPE(String bucketName) {

        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);

        // This will hold all of our classifications
        List<PPEClassificationResponse> classificationResponses = new ArrayList<>();

        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        List<String> requiredProtection = new ArrayList<>();

        int scanCount = 0;
        int scanViolations = 0;

        // Iterate over each object and scan for PPE
        for (S3ObjectSummary image : images) {
            requiredProtection.add("FACE_COVER");

            log.info("scanning " + image.getKey() + " for labels");

            // Scanning for labels in image.
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
                if (isTool(parents)) {
                    requiredProtection.add("HAND_COVER");
                }
            }
            log.info("scanning " + image.getKey() + " for required protection " + requiredProtection);

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

            log.info("scanning " + image.getKey() + ", violation result " + violation);

            // Categorize the current image as a violation or not.
            int personCount = result.getPersons().size();
            PPEClassificationResponse classification = new PPEClassificationResponse(image.getKey(), personCount, violation);
            classificationResponses.add(classification);

            // Update scanCount with how many persons scanned in image.
            scanCount += personCount;
            // Update scanViolations with how many persons scanned in image not wearing required equipment.
            scanViolations += result.getSummary().getPersonsWithoutRequiredEquipment().size();

            // Clear the required protection for next image.
            requiredProtection.clear();
        }

        // Increment ppe_scan_count metric with how many persons is scanned.
        meterRegistry.counter("ppe_scan_count").increment(scanCount);
        // Increment ppe_scan_violation_count with how many of the persons has violated the requirement.
        meterRegistry.counter("ppe_scan_violation_count").increment(scanViolations);

        return new PPEResponse(bucketName, classificationResponses);
    }


    // TODO Should i just make this check for all moderation's instead of only weapons?
    /**
     * Scan all images in the bucket for moderation labels.
     * If a weapon label is found in the image, the relevant classification-information is added to the response.
     * If a weapon label is not found it is not added to the response.
     *
     * @param bucketName
     */
    public WeaponScanResponse scanForWeapons(String bucketName) {

        int scanCount = 0;
        int scanViolations = 0;

        // List with all objects in the bucket.
        ListObjectsV2Result objectList = s3Client.listObjectsV2(bucketName);

        // List with all our weapon checks.
        List<WeaponClassificationResponse> classificationResponses = new ArrayList<>();

        // List with all the images in the bucket
        List<S3ObjectSummary> images = objectList.getObjectSummaries();


        for (S3ObjectSummary image : images) {

            // Create the request to detect labels.
            DetectModerationLabelsRequest request = new DetectModerationLabelsRequest()
                    .withImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(image.getKey())))
                    .withMinConfidence(60F);

            try {
                // Scan for labels.
                DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(request);
                List<ModerationLabel> labels = result.getModerationLabels();

                // Iterate through the labels, if weapon is found classification is created with relevant info and added to response.
                for (ModerationLabel label : labels) {
                    if (label.getName().equalsIgnoreCase("weapons")) {
                        WeaponClassificationResponse classification = new WeaponClassificationResponse(image.getKey(), label.getConfidence(), label.getName(), label.getParentName(), true);
                        classificationResponses.add(classification);
                        scanViolations++;
                    }
                }

                System.out.println(scanViolations);
                scanCount += countPersonsInImage(bucketName, image.getKey());

            } catch (AmazonRekognitionException e) {
                e.printStackTrace();
            }
        }

        // Increment weapon_scan_count metric with how many persons is scanned.
        meterRegistry.counter("weapon_scan_count").increment(scanCount);

        // Increment weapon_scan_violation_count with how many of persons with weapon discovered.
        meterRegistry.counter("weapon_scan_violation_count").increment(scanViolations);


        return new WeaponScanResponse(bucketName, classificationResponses);
    }


    // Check if label is under type "tool".
    private static boolean isTool(List<Parent> parents) {
        if (!parents.isEmpty()) {

            for (Parent parent : parents) {
                if (parent.getName().equalsIgnoreCase("tool")) {
                    log.info("person is holding tool");
                    return true;
                }
            }
        }
        return false;
    }


    // TODO find a better way than use PPE scan to get out amount of persons in image.
    /**
     * Scan an image and count the persons in that image.
     *
     * @param bucketName
     * @param imageName
     * @return Integer amount of persons on the image.
     */
    public Integer countPersonsInImage(String bucketName, String imageName) {


        DetectProtectiveEquipmentRequest request = new DetectProtectiveEquipmentRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withBucket(bucketName)
                                .withName(imageName)))
                .withSummarizationAttributes(new ProtectiveEquipmentSummarizationAttributes()
                        .withMinConfidence(80f)
                        .withRequiredEquipmentTypes("FACE_COVER"));

        DetectProtectiveEquipmentResult result = rekognitionClient.detectProtectiveEquipment(request);

        return result.getPersons().size();
    }


    /**
     * Called only once, when the event "application ready" comes.
     * (When everything else in code is ready, method is run)
     * Register the gauge to keep track on how many persons are in the construction area.
     * @param applicationReadyEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Gauge.builder("persons_in_area_count", () -> personsInConstructionArea).register(meterRegistry);
    }
}
