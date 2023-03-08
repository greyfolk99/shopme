package com.greyfolk99.shopme.domain.cart;

import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Table(name = "cart")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "cart_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL) @ToString.Exclude @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();
    public static Cart of(Member member) {
        return Cart.builder().member(member).build();
    }

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
    }
    public void deleteCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
    }
}
