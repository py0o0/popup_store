package com.example.order.service;

import com.example.order.dto.ItemDto;
import com.example.order.dto.OrderDto;
import com.example.order.dto.PurchaseDto;
import com.example.order.entity.*;
import com.example.order.jwt.JwtUtil;
import com.example.order.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;
    private final ItemRepository itemRepository;
    private final PopUpRepository popUpRepository;
    private final JwtUtil jwtUtil;
    private final OrderKafkaProducer orderKafkaProducer;

    // 주문 처리 및 결제 정보 저장
    @Transactional
    public void orderItem(String token, OrderDto orderDto) {
        String email = jwtUtil.getEmail(token);
        User user = usersRepository.getReferenceById(email);
        List<CartEntity> cartItems = cartRepository.findByEmail(email);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어있어 주문할 수 없습니다.");
        }

        List<ItemEntity> itemEntities = itemRepository.findListByPopIdAndItemId(orderDto.getPopId(), orderDto.getItemId());

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        orderDto.setOrderDate(currentDate);

        long totalPrice = 0L;
        String firstItemName = cartItems.get(0).getItemName(); // 첫 번째 아이템 이름 저장

        for (CartEntity cartItem : cartItems) {
            totalPrice += cartItem.getPrice();
        }

        // 잔액 검사
        if (user.getPoint() < totalPrice) {
            throw new IllegalArgumentException("구매자의 잔액이 부족합니다.");
        }

        // 수량 차감
        for (CartEntity cartItem : cartItems) {
            ItemEntity itemEntity = itemRepository.findById(cartItem.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));

            if (itemEntity.getAmount() < cartItem.getAmount()) {
                throw new IllegalStateException("상품의 재고가 부족합니다.");
            }
            // 잔액 차감
            user.setPoint(user.getPoint() - totalPrice);
            usersRepository.save(user);

            // 재고 차감
            itemEntity.setAmount(itemEntity.getAmount() - cartItem.getAmount());

            itemRepository.save(itemEntity);
        }

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .buyerEmail(email)
                .orderDate(currentDate)
                .totalPrice(totalPrice)
                .orderItem(firstItemName)
                .build();

        paymentEntity = paymentRepository.save(paymentEntity);
        Long paymentId = paymentEntity.getPaymentId();

        for (CartEntity cartItem : cartItems) {
            OrderEntity orderEntity = OrderEntity.builder()
                    .itemId(cartItem.getItemId())
                    .popId(cartItem.getPopId())
                    .email(email)
                    .totalPrice(cartItem.getPrice())
                    .itemName(cartItem.getItemName())
                    .buyerName(orderDto.getBuyerName())
                    .buyerAddress(orderDto.getBuyerAddress())
                    .buyerPhone(orderDto.getBuyerPhone())
                    .totalAmount(cartItem.getAmount())
                    .orderDate(orderDto.getOrderDate())
                    .imageUrl(cartItem.getImageUrl())
                    .paymentId(paymentId)
                    .build();

            orderRepository.save(orderEntity);
        }

        // 장바구니 비우기
        cartRepository.deleteByEmail(email);

    // Kafka 메시지 전송, Kafka 테스트
        for (CartEntity cartItem : cartItems) {
            // popId 기반으로 판매자 이메일 조회
            PopUpEntity popUpEntity = popUpRepository.findById(cartItem.getPopId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 popId에 대한 판매자를 찾을 수 없습니다: " + cartItem.getPopId()));

            String sellerEmail = popUpEntity.getEmail(); // 판매자의 이메일 가져오기

            // 구매 정보 DTO 생성
            PurchaseDto purchaseDto = new PurchaseDto(
                    List.of(new ItemDto(cartItem.getItemName(), cartItem.getAmount(), cartItem.getPrice())),
                    email,           // 구매자 이메일
                    sellerEmail      // 판매자 이메일
            );

            // Kafka 메시지 전송 (각 판매자에게 개별적으로)
            orderKafkaProducer.sendPurchaseMessage(purchaseDto);
        }
    }

    // 주문 상세정보
    public List<OrderDto> getOrderItems(String token, Long paymentId) {
        String email = jwtUtil.getEmail(token);

        // paymentId로 PaymentEntity 조회
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId);
        if (paymentEntity == null) {
            throw new RuntimeException("해당 paymentId의 결제 정보를 찾을 수 없습니다: " + paymentId);
        }

        // email과 paymentId로 OrderEntity 조회
        List<OrderEntity> orderEntities = orderRepository.findByEmailAndPaymentId(email, paymentEntity.getPaymentId());

        if (orderEntities == null) {
            throw new RuntimeException("해당 email의 주문을 찾을 수 없습니다.: " + email + "해당 paymentId의 주문을 찾을 수 없습니다.: " + paymentId);
        }

        return orderEntities.stream()
                .map(order -> OrderDto.builder()
                        .orderId(order.getOrderId())
                        .itemId(order.getItemId())
                        .popId(order.getPopId())
                        .email(order.getEmail())
                        .totalPrice(order.getTotalPrice())
                        .itemName(order.getItemName())
                        .buyerName(order.getBuyerName())
                        .buyerAddress(order.getBuyerAddress())
                        .buyerPhone(order.getBuyerPhone())
                        .totalAmount(order.getTotalAmount())
                        .orderDate(order.getOrderDate())
                        .imageUrl(order.getImageUrl())
                        .paymentId(paymentEntity.getPaymentId())
                        .build())
                .toList();
    }

    // 판매자의 판매 내여 조회 (popId로 기반한 주문 내역 조회)
    public List<OrderDto> getSoldItem(long popId) {
        List<OrderEntity> orderEntities = orderRepository.findByPopId(popId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderEntities.stream()
                .sorted(Comparator.comparing(
                        order -> parseDate(order.getOrderDate(), formatter), Comparator.reverseOrder()))
                .map(order ->  OrderDto.builder()
                        .orderId(order.getOrderId())
                        .itemId(order.getItemId())
                        .popId(order.getPopId())
                        .email(order.getEmail())
                        .totalPrice(order.getTotalPrice())
                        .itemName(order.getItemName())
                        .buyerName(order.getBuyerName())
                        .buyerAddress(order.getBuyerAddress())
                        .buyerPhone(order.getBuyerPhone())
                        .totalAmount(order.getTotalAmount())
                        .orderDate(order.getOrderDate())
                        .imageUrl(order.getImageUrl())
                        .paymentId(order.getPaymentId())
                        .build())
                .toList();
    }

    // 문자열을 LocalDateTime으로 변환
    private LocalDateTime parseDate(String dateStr, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDateTime.MIN; // 변환 실패 시 가장 오래된 날짜로 설정
        }
    }
}
