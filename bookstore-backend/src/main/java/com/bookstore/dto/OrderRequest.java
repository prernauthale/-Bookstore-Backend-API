package com.bookstore.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private List<OrderItemRequest> items;
}
