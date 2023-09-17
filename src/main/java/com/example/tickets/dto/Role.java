package com.example.tickets.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {

    public Role(String name) {
        this.name = name;
    }

    private String name;
    private Integer id;

    @Override
    public String getAuthority() {
        return name;
    }
}
