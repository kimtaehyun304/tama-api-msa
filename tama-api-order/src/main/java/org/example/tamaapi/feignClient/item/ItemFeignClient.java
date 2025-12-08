package org.example.tamaapi.feignClient.item;

import org.example.tamaapi.dto.requestDto.order.PortOneOrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "item-service", url = "http://localhost:5002")
public interface ItemFeignClient {

    @GetMapping("/api/items/totalPrice")
    int getTotalPrice(@RequestBody List<ItemOrderCountRequest> requests);

    @GetMapping("/api/items/price")
    List<ItemPriceResponse> getItemsPrice(@RequestBody List<Long> colorItemSizeStockIds);

    @PutMapping("/api/items/stocks/increase")
    void increaseStocks(@RequestBody List<ItemOrderCountRequest> requests);


    @PutMapping("/api/items/stocks/decrease")
    void decreaseStocks(@RequestBody List<ItemOrderCountRequest> requests);

}
