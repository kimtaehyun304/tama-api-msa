package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.command.item.ItemService;
import org.example.tamaapi.dto.feign.requestDto.ItemOrderCountRequest;
import org.example.tamaapi.dto.feign.responseDto.ItemPriceFeignResponse;
import org.example.tamaapi.query.item.service.ItemQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
public class FeignApiController {

    private final ItemQueryService itemQueryService;
    private final ItemService itemService;

    @GetMapping("/api/items/totalPrice")
    public int getTotalPrice(@RequestBody List<ItemOrderCountRequest> requests) {
        return itemQueryService.getItemsTotalPrice(requests);
    }

    //주문 아이템 생성시 필요
    @GetMapping("/api/items/price")
    public List<ItemPriceFeignResponse> getItemsPrice(@RequestBody List<Long> colorItemSizeStockIds) {
        return itemQueryService.getItemsPrice(colorItemSizeStockIds);
    }

    @PutMapping("/api/items/stocks/increase")
    public void increaseStocks(@RequestBody List<ItemOrderCountRequest> requests) {
        itemService.increaseStocks(requests);
    }

    @PutMapping("/api/items/stocks/decrease")
    public void decreaseStocks(@RequestBody List<ItemOrderCountRequest> requests) {
        itemService.decreaseStocks(requests);
    }

}
