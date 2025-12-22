package microarch;

import libs.common.Timex;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.courier.CourierBuilder;
import microarch.delivery.domain.model.courier.StoragePlaceBuilder;
import microarch.delivery.domain.model.courier.StoragePlaceType;
import microarch.delivery.domain.model.order.OrderBuilder;
import microarch.delivery.domain.model.order.OrderStatus;

import java.util.List;

public class TestHelper {

    public static final String COURIER_NAME = "John";
    public static final String DEFAULT_USER = "default";
    public static final int DEFAULT_COURIER_SPEED = 10;
    public static final Location DEFAULT_LOCATION = Location.create(1, 1);
    public static final Id DEFAULT_ID = Id.of(1L);
    public static final int DEFAULT_VOLUME = 10;


    public static CourierBuilder fullCourierBuilder() {
        return CourierBuilder.courier()
                .id(DEFAULT_ID)
                .name(COURIER_NAME)
                .speed(DEFAULT_COURIER_SPEED)
                .location(DEFAULT_LOCATION)
                .storagePlaces(List.of(fullStoragePlace().build()))
                .createdAt(Timex.currentOffsetDateTime())
                .createdBy(DEFAULT_USER)
                .modifiedAt(Timex.currentOffsetDateTime())
                .modifiedBy(DEFAULT_USER)
                .version(0);
    }

    public static StoragePlaceBuilder fullStoragePlace() {
        return StoragePlaceBuilder.storagePlace()
                .id(DEFAULT_ID)
                .placeType(StoragePlaceType.BACKPACK)
                .orderId(DEFAULT_ID);
    }

    public static OrderBuilder fullOrder() {
        return OrderBuilder.order()
                .id(DEFAULT_ID)
                .courierId(DEFAULT_ID)
                .volume(DEFAULT_VOLUME)
                .location(DEFAULT_LOCATION)
                .status(OrderStatus.CREATED)
                .createdAt(Timex.currentOffsetDateTime())
                .createdBy(DEFAULT_USER)
                .modifiedAt(Timex.currentOffsetDateTime())
                .modifiedBy(DEFAULT_USER)
                .version(0);
    }
}
