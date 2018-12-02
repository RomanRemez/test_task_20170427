package com.opinta.service;

import com.opinta.entity.Shipment;

import java.math.BigDecimal;

public interface ParcelService {

    BigDecimal calculatePrice(Shipment shipment);
}
