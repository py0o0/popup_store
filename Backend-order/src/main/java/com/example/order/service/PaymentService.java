package com.example.order.service;

import com.example.order.dto.PaymentDto;
import com.example.order.entity.PaymentEntity;
import com.example.order.jwt.JwtUtil;
import com.example.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final JwtUtil jwtUtil;

    
    // 결제 정보 날짜순으로 조회
    public List<PaymentDto> getPayment(String token) {
        String email = jwtUtil.getEmail(token);
        List<PaymentEntity> paymentEntities = paymentRepository.findByBuyerEmail(email);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return paymentEntities.stream()
                .sorted(Comparator.comparing(
                        p -> parseDate(p.getOrderDate(), formatter), Comparator.reverseOrder()))
                .map(p -> PaymentDto.builder()
                        .paymentId(p.getPaymentId())
                        .orderItem(p.getOrderItem())
                        .buyerEmail(p.getBuyerEmail())
                        .orderDate(p.getOrderDate())
                        .totalPrice(p.getTotalPrice())
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

    /*
    // 판매일자순으로 판매한 내역을 조회 -> payment에는 popId가 없어서 일단 보류
    public List<PaymentDto> getSoldItem(long popId) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByPopId(popId);

        return paymentEntities.stream()
                .sorted(Comparator.comparing(
                        p -> parseDate(p.getOrderDate(), formatter), Comparator.reverseOrder()))
                .map(p -> PaymentDto.builder()
                        .paymentId(p.getPaymentId())
                        .orderItem(p.getOrderItem())
                        .buyerEmail(p.getBuyerEmail())
                        .orderDate(p.getOrderDate())
                        .totalPrice(p.getTotalPrice())
                        .build())
                .toList();
    }
    **/
}
