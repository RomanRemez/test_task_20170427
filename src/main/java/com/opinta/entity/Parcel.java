package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Parcel {

    @Id
    @GeneratedValue
    private long id;
    @OneToMany
    private List<ParcelItem> parcelItems;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;

    public Parcel(List<ParcelItem> parcelItems, float weight, float length, float width, float height,
                  BigDecimal declaredPrice, BigDecimal price) {
        this.parcelItems = parcelItems;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = price;
    }
}
