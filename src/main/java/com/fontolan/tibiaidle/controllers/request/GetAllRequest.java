package com.fontolan.tibiaidle.controllers.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetAllRequest {
    @Min(1)
    private int page = 1;

    @Min(1)
    @Max(100)
    private int limit = 10;
}
