package com.example.cart.controller;

import com.example.cart.dto.CartDto;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // 장바구니 아이템 담기
    @PostMapping("/{popId}/{itemId}")
    public ResponseEntity<CartDto> putCartItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long popId,
            @PathVariable Long itemId,
            @RequestBody CartDto cartDto) {
        cartService.saveCartItem(token, popId, itemId, cartDto);
        return ResponseEntity.ok().body(cartDto);
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartDto>> getCartItems(@RequestHeader("Authorization") String token) {
        List<CartDto> cartItems = cartService.getCartItems(token);
        return ResponseEntity.ok(cartItems);
    }

    // 장바구니 아이템 빼기
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> decreaseCartItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId,
            @RequestBody CartDto cartDto) {
        cartService.decreaseCartItem(token, itemId, cartDto.getAmount());
        return ResponseEntity.ok().build();
    }

    // 장바구니에서 아이템 삭제
    @DeleteMapping("{cartId}")
    public void deleteCartItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long cartId) {
        cartService.deleteItem(cartId);
    }
}
