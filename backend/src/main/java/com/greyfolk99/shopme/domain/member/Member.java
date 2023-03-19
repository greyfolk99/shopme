package com.greyfolk99.shopme.domain.member;

import com.greyfolk99.shopme.domain.cart.Cart;
import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.dto.form.MemberForm;
import com.greyfolk99.shopme.dto.form.MemberUpdateForm;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Table(name = "member")
@Entity @Getter @Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE member SET is_deleted = 1, deleted_at = NOW() WHERE member_uuid = ?")
public class Member extends BaseEntity implements UserDetails, OAuth2User {

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

    @Transient
    private Map<String, Object> oAuth2Attributes;

    @OneToMany(mappedBy = "member") @Builder.Default
    private List<Order> orders = new ArrayList<>();
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    public void update(MemberUpdateForm memberForm, PasswordEncoder passwordEncoder) {
        name = memberForm.getName();
        password = passwordEncoder.encode(memberForm.getPassword());
        address = memberForm.getAddress();
    }

    public void update(Address address) {
        if (this.address == null) {
            this.address = new Address();
            this.address.update(address);
        } else {
            this.address.update(address);
        }
    }

    public Member update(String name) {this.name = name; return this;}

    public static Member of(UUID uuid, String email, String name, Address address, String password, Role role){
        return Member.builder().uuid(uuid).email(email).name(name).address(address).password(password).role(role).build();
    }

    public static Member of(String username, String name, Address address, String password, Role role, String createdBy){
        Member member = Member.builder().email(username).name(name).address(address).password(password).role(role).build();
        member.setCreatedBy(createdBy);
        return member;
    }

    public static Member of(String username, String password, String name, Role role){
        return Member.builder().email(username).name(name).password(password).role(role).build();
    }

    public static Member of(String username, String password, String name, Role role, String createdBy){
        Member member = Member.builder().email(username).name(name).password(password).role(role).build();
        member.setCreatedBy(createdBy);
        return member;
    }

    public static Member from(MemberForm memberForm, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
            .name(memberForm.getName())
            .email(memberForm.getEmail())
            .address(memberForm.getAddress())
            .password(passwordEncoder.encode(memberForm.getPassword()))
            .role(Role.MEMBER).build();
        member.setCreatedBy(memberForm.getEmail());
        return member;
    }
    public void is(Role role) {
        this.role = role;
    }


    @Override public Map<String, Object> getAttributes() {return oAuth2Attributes;}
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(List.of(new RoleAdapter(role)));}
    @Override public String getUsername() {return email;}
    @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return !isDeleted;}


    @Builder
    public Member(String createdBy, UUID uuid, String email, String name, Address address, String password, Role role) {
        setCreatedBy(createdBy);
        this.uuid = uuid;
        this.email = email;
        this.name = name;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member that)) return false;
        return this.getUuid() != null && this.getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}


