package com.example.tickets.dto;

import com.example.tickets.validator.annotation.RoleExist;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Data
public class GrantRoleDto {

    @NotNull
    @RoleExist
    private String roleName;
}
