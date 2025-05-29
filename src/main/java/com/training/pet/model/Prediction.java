package com.training.pet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Prediction {

    private String className;
    private double confidence;
}
