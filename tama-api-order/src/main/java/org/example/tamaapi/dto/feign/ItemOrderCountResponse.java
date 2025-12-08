package org.example.tamaapi.dto.feign;

import org.example.tamaapi.domain.order.OrderItem;

public class ItemOrderCountResponse {

    private Long colorItemSizeStockId;

    private int count;

    public ItemOrderCountResponse(OrderItem orderItem) {
        this.colorItemSizeStockId = orderItem.getColorItemSizeStockId();
        this.count = orderItem.getCount();
    }
}
