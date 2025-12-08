package org.example.tamaapi.dto.requestDto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tamaapi.dto.feign.requestDto.ItemOrderCountRequest;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemRequestWrapper {

    private List<ItemOrderCountRequest> orderItems;

}
