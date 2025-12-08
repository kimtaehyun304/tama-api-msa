package org.example.tamaapi.query.item.service;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.item.ColorItemSizeStock;
import org.example.tamaapi.dto.feign.requestDto.ItemOrderCountRequest;
import org.example.tamaapi.dto.feign.responseDto.ItemPriceFeignResponse;
import org.example.tamaapi.query.item.ColorItemSizeStockQueryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemQueryService {

    private final ColorItemSizeStockQueryRepository colorItemSizeStockQueryRepository;

    public int getItemsTotalPrice(List<ItemOrderCountRequest> orderItems) {
        List<Long> colorItemSizeStockIds = orderItems.stream().map(ItemOrderCountRequest::getColorItemSizeStockId).toList();
        List<ColorItemSizeStock> colorItemSizeStocks = colorItemSizeStockQueryRepository
                .findAllWithColorItemAndItemByIdIn(colorItemSizeStockIds);

        Map<Long, Integer> idPriceMap = new HashMap<>();
        for (ColorItemSizeStock colorItemSizeStock : colorItemSizeStocks) {
            Integer nowPrice = colorItemSizeStock.getColorItem().getItem().getNowPrice();
            idPriceMap.put(colorItemSizeStock.getId(), nowPrice);
        }

        return orderItems.stream()
                .mapToInt(i -> idPriceMap.get(i.getColorItemSizeStockId()) * i.getOrderCount())
                .sum();
    }

    public List<ItemPriceFeignResponse> getItemsPrice(List<Long> colorItemSizeStockIds) {
        //자동으로 중복 제거 됨
        List<ColorItemSizeStock> colorItemSizeStocks = colorItemSizeStockQueryRepository
                .findAllWithColorItemAndItemByIdIn(colorItemSizeStockIds);

        List<ItemPriceFeignResponse> itemPriceResponses = new ArrayList<>();

        for (ColorItemSizeStock colorItemSizeStock : colorItemSizeStocks) {
            Integer nowPrice = colorItemSizeStock.getColorItem().getItem().getNowPrice();
            itemPriceResponses.add(new ItemPriceFeignResponse(colorItemSizeStock.getId(), nowPrice));
        }

        return itemPriceResponses;
    }

}

