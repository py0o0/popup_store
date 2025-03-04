package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.PaymentDto;
import com.example.order.service.OrderService;
import com.example.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;

    // 주문 하기
    @PostMapping
    public ResponseEntity<OrderDto> createItem(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderDto orderDto) {
        orderService.orderItem(token, orderDto);
        return ResponseEntity.ok().body(orderDto);
    }

    // 결제 정보 조회 (이메일의 전체 결제 정보를 조회)
    @GetMapping("/payment")
    public ResponseEntity<List<PaymentDto>> getPayment(@RequestHeader("Authorization") String token) {
        List<PaymentDto> paymentItems = paymentService.getPayment(token);
        return ResponseEntity.ok().body(paymentItems);
    }

    // 주문 조회 (결제 정보를 클릭 또는 상세정보 보기를 누르면 보이는 정보 -> 1개의 주문만 return)
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<OrderDto>> getOrderItems(@RequestHeader("Authorization") String token,
                                                  @PathVariable long paymentId) {
        List<OrderDto> orderItems = orderService.getOrderItems(token, paymentId);
        return ResponseEntity.ok().body(orderItems);
    }

    // 주문 조회 판매자시점 (popId를 기반으로 판매한 내역을 볼 수 있음)
    @GetMapping("/seller/{popId}")
    public ResponseEntity<List<OrderDto>> getSoldItem(@RequestHeader("Authorization") String token,
                                                        @PathVariable long popId) {
        List<OrderDto> soldItems = orderService.getSoldItem(popId);
        return ResponseEntity.ok().body(soldItems);
    }
}
