package com.opticmanager.dto;

import com.opticmanager.entity.Eye;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class LensDetailsDTO {

    private UUID id;
    private Eye eye;
    private BigDecimal sphere;
    private BigDecimal cylinder;
    private Integer axis;
    private BigDecimal prismAmount;
    private String prismBase;
    private BigDecimal addPower;
    private BigDecimal power;
    private BigDecimal backCurve;
    private BigDecimal diameter;
    private String duration;
    private String color;
    private String brand;
    private String notes;
}
