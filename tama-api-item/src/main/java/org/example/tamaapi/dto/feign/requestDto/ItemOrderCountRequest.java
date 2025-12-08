package org.example.tamaapi.dto.feign.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOrderCountRequest {

    @NotNull
    private Long colorItemSizeStockId;

    @NotNull
    private Integer orderCount;


}
