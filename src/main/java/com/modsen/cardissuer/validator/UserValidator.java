package com.modsen.cardissuer.validator;

import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
        if (user.getName().length() < 8 || user.getName().length() > 32) {
            errors.rejectValue("name", "size.userForm.name");
        }

        if (userService.findByName(user.getName()) != null) {
            errors.rejectValue("name", "duplicate.userForm.name");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required");
        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "size.userForm.password");
        }
        if (user.getConfirmPassword().equals(user.getPassword())) {
            errors.rejectValue("confirmPassword", "different.userForm.password");
        }
    }
}
