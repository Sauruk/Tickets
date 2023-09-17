package com.example.tickets.validator;

import com.example.tickets.dao.UserDao;
import com.example.tickets.validator.annotation.NoLoginDuplicates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginValidator implements ConstraintValidator<NoLoginDuplicates, String> {

    private UserDao userDao;

    @Override
    public void initialize(NoLoginDuplicates constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext validatorContext) {
        if (userDao.isExist(login)) {
            validatorContext.disableDefaultConstraintViolation();
            validatorContext.buildConstraintViolationWithTemplate("Login already occupied").addConstraintViolation();
            return false;
        }
        return true;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
