package com.example.cart.service;

import com.example.cart.dto.CartDto;
import com.example.cart.entity.CartEntity;
import com.example.cart.entity.ItemEntity;
import com.example.cart.entity.PopUpEntity;
import com.example.cart.jwt.JwtUtil;
import com.example.cart.repository.CartRepository;
import com.example.cart.repository.ItemRepository;
import com.example.cart.repository.PopUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final PopUpRepository popUpRepository;
    private final ItemRepository itemRepository;
    private final JwtUtil jwtUtil;
    
    // 장바구니에 아이템 추가
    public void saveCartItem(String token, Long popId, Long itemId, CartDto cartDto) {
        PopUpEntity popUpEntity = popUpRepository.findById(popId)
                .orElseThrow(() -> new RuntimeException("해당 popId에 해당하는 팝업스토어를 찾을 수 없습니다."));

        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 itemId에 해당하는 아이템을 찾을 수 없습니다."));

        if (!itemEntity.getPopId().equals(popId)) {
            throw new IllegalArgumentException("해당 아이템은 요청한 popId에 속하지 않습니다.");
        }

        String email = jwtUtil.getEmail(token);

        CartEntity existingCartItem = cartRepository.findByEmailAndItemId(email, itemId);

        if (existingCartItem != null) {
            // 이미 장바구니에 존재하는 경우 → 수량 증가 + 가격 업데이트
            Long newAmount = existingCartItem.getAmount() + cartDto.getAmount();
            Long newTotalPrice = itemEntity.getPrice() * newAmount;

            existingCartItem.setAmount(newAmount);
            existingCartItem.setPrice(newTotalPrice);

            cartRepository.save(existingCartItem);
        } else {
            // 장바구니에 존재하지 않는 경우 → 새로 추가
            Long totalPrice = itemEntity.getPrice() * cartDto.getAmount();

            // 아이템의 첫번째 이미지만 가져오기 (대표 이미지)
            String firstImageUrl = (itemEntity.getImage() != null && !itemEntity.getImage().isEmpty())
                    ? itemEntity.getImage()
                    : null; // 이미지가 없으면 null

            CartEntity cartEntity = CartEntity.builder()
                    .itemId(itemEntity.getItemId()) // 아이템 ID
                    .email(email) // 장바구니 추가한 사용자 이메일
                    .price(totalPrice) // 계산된 총 가격
                    .amount(cartDto.getAmount()) // 수량
                    .itemName(itemEntity.getName()) // 아이템 이름
                    .popId(popUpEntity.getPopId()) // 팝업 스토어 정보
                    .imageUrl(firstImageUrl) // 첫번째 이미지 URL 정보
                    .build();

            cartRepository.save(cartEntity);
        }
    }

    // 이메일을 이용하여 장바구니 조회
    public List<CartDto> getCartItems(String token) {
        String email = jwtUtil.getEmail(token);
        List<CartEntity> cartEntities = cartRepository.findByEmail(email);

        // 엔티티 리스트를 DTO 리스트로 변환
        return cartEntities.stream()
                .map(cart -> CartDto.builder()
                        .cartId(cart.getCartId())
                        .itemId(cart.getItemId())
                        .itemName(cart.getItemName())
                        .amount(cart.getAmount())
                        .price(cart.getPrice())
                        .popId(cart.getPopId())
                        .email(cart.getEmail())
                        .imageUrl(cart.getImageUrl())
                        .build())
                .toList();
    }

    // 장바구니 아이템 빼기
    public void decreaseCartItem(String token, Long itemId, Long decreaseAmount) {
        String email = jwtUtil.getEmail(token);
        CartEntity cartItem = cartRepository.findByEmailAndItemId(email, itemId);

        // 현재 수량보다 많이 줄이려 하면 예외 처리
        if (cartItem.getAmount() < decreaseAmount) {
            throw new IllegalArgumentException("현재 수량보다 많은 수량을 뺄 수 없습니다.");
        }

        // 새로운 수량 계산
        long newAmount = cartItem.getAmount() - decreaseAmount;

        // 수량이 0이면 장바구니에서 삭제
        if (newAmount == 0) {
            cartRepository.delete(cartItem);
        } else {
            ItemEntity item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("해당 itemId에 해당하는 아이템을 찾을 수 없습니다."));

            long itemUnitPrice = item.getPrice(); // 아이템 단가
            long newTotalPrice = itemUnitPrice * newAmount; // 새 총 가격 계산

            // 총 가격 업데이트 후 저장
            cartItem.setAmount(newAmount);
            cartItem.setPrice(newTotalPrice);
            cartRepository.save(cartItem);
        }
    }

    public void deleteItem(Long itemId) {
        cartRepository.deleteById(itemId);
    }
}