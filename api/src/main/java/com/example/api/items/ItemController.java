package com.example.api.items;

import com.example.item.ItemResponse;
import com.example.items.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable("id") Long id) {
        ItemResponse item = itemService.getItem(id);
        return  ResponseEntity.status(HttpStatus.OK).body(item);
    }
}
