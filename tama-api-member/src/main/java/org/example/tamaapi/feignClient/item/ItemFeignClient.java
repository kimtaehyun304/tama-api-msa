package org.example.tamaapi.feignClient.item;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "item-service", url = "http://localhost:5002")
public interface ItemFeignClient {

    @GetMapping("/api/items/totalPrice")
    int getTotalPrice(@RequestBody List<ItemTotalPriceRequest> requests);


    @GetMapping("/api/items/price")
    List<ItemPriceResponse> getItemsPrice(@RequestBody List<Long> colorItemSizeStockIds);
}
