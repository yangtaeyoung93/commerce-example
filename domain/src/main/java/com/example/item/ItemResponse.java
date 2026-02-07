package com.example.item;

public record ItemResponse(
        Long id,
        String name,
        Integer price,
        Integer stockQuantity
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getStockQuantity()
        );
    }
}
