package com.example.s3rekognition.weaponscan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class WeaponClassificationResponse implements Serializable {

    private String fileName;
    private double confidence;
    private String name;
    private String parentName;
    private boolean violation;

}
