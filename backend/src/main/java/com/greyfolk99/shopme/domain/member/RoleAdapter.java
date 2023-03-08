package com.greyfolk99.shopme.domain.member;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class RoleAdapter implements GrantedAuthority {
    private Role role;
    @Override
    public String getAuthority() {
        String roleName = role.name();
        return roleName.startsWith("ROLE_") ?
                roleName :
                "ROLE_" + roleName;
    }
}
