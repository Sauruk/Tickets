package com.example.tickets.validator;

import com.example.tickets.dao.RoleDao;
import com.example.tickets.validator.annotation.RoleExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleExistValidator implements ConstraintValidator<RoleExist, String> {

    private RoleDao roleDao;

    @Override
    public boolean isValid(String roleName, ConstraintValidatorContext validatorContext) {
        if (!roleDao.isExist(roleName)) {
            validatorContext.disableDefaultConstraintViolation();
            validatorContext.buildConstraintViolationWithTemplate("No such role").addConstraintViolation();
            return false;
        }
        return true;
    }

    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}
