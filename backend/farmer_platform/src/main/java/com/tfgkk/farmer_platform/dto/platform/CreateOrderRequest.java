package com.tfgkk.farmer_platform.dto.platform;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {
    @NotEmpty(message = "Order items are required")
    private List<OrderItemDto> items;

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
