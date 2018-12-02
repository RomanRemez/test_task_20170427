package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.entity.*;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final TariffGridDao tariffGridDao;

    public ParcelServiceImpl(TariffGridDao tariffGridDao) {
        this.tariffGridDao = tariffGridDao;
    }

    @Override
    public BigDecimal calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);
        final W2wVariation w2wVariation = getW2wVariation(shipment);

        return shipment.getParcels().stream()
                .peek(parcel -> {
                        TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
                        if (parcel.getWeight() < tariffGrid.getWeight() &&
                                parcel.getLength() < tariffGrid.getLength()) {
                            tariffGrid = tariffGridDao.getByDimension(parcel.getWeight(), parcel.getLength(), w2wVariation);
                        }

                        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                                parcel.getWeight(), parcel.getLength(), w2wVariation, tariffGrid);

                        if (tariffGrid == null) {
                            parcel.setPrice(BigDecimal.ZERO);
                        } else {
                            float price = tariffGrid.getPrice() + getSurcharges(shipment);
                            parcel.setPrice(new BigDecimal(Float.toString(price)));
                        }
                    })
                .map(Parcel::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private W2wVariation getW2wVariation(Shipment shipment) {
        Address senderAddress = shipment.getSender().getAddress();
        Address recipientAddress = shipment.getRecipient().getAddress();
        W2wVariation w2wVariation = W2wVariation.COUNTRY;
        if (AddressUtil.isSameTown(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.TOWN;
        } else if (AddressUtil.isSameRegion(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.REGION;
        }
        return w2wVariation;
    }

    private float getSurcharges(Shipment shipment) {
        float surcharges = 0;
        if (shipment.getDeliveryType().equals(DeliveryType.D2W) ||
                shipment.getDeliveryType().equals(DeliveryType.W2D)) {
            surcharges += 9;
        } else if (shipment.getDeliveryType().equals(DeliveryType.D2D)) {
            surcharges += 12;
        }
        return surcharges;
    }
}
