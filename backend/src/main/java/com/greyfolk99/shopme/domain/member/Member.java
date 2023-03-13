package com.greyfolk99.shopme.domain.member;

import com.greyfolk99.shopme.domain.cart.Cart;
import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.dto.form.MemberForm;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Table(name = "member")
@Entity @Getter @Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE member SET is_deleted = 1, deleted_at = NOW() WHERE member_uuid = ?")
public class Member extends BaseEntity implements UserDetails{

    @Id @GeneratedValue(generator = "uuid2") @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)", name = "member_uuid")
    private UUID uuid;
    @Column(unique = true)
    private String email;
    @Column
    private String name;
    @Embedded
    private Address address;
    @Column
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(columnDefinition = "boolean default 0") @Builder.Default
    private Boolean isDeleted = false;
    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member") @Builder.Default
    private List<Order> orders = new ArrayList<>();
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @Transient
    private Map<String, Object> oAuth2Attributes;

    public void update(MemberForm memberForm, PasswordEncoder passwordEncoder) {
        name = memberForm.getName();
        password = passwordEncoder.encode(memberForm.getPassword());
        address = memberForm.getAddress();
    }

    public static Member of(UUID uuid, String email, String name, Address address, String password, Role role){
        return Member.builder().uuid(uuid).email(email).name(name).address(address).password(password).role(role).build();
    }

    public static Member ofOAuth(String email, String password, String name, Role role){
        return Member.builder().email(email).name(name).password(password).role(role).build();
    }

    public static Member from(MemberForm memberForm, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(memberForm.getName())
                .email(memberForm.getEmail())
                .address(memberForm.getAddress())
                .password(passwordEncoder.encode(memberForm.getPassword()))
                .role(Role.MEMBER).build();
    }
    public void is(Role role) {
        this.role = role;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(List.of(new RoleAdapter(role)));}
    @Override public String getUsername() {return email;}
    @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return !isDeleted;}
}


