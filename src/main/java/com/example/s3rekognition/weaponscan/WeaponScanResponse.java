package com.example.s3rekognition.weaponscan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeaponScanResponse implements Serializable {

    String bucketName;
    List<WeaponClassificationResponse> results;
}
