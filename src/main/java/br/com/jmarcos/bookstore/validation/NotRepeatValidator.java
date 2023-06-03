package br.com.jmarcos.bookstore.validation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

import br.com.jmarcos.bookstore.validation.constraints.NotRepeat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotRepeatValidator implements ConstraintValidator<NotRepeat, Object>{

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        List<Object> list = (ArrayList) value;

        Set<Object> setlist = new LinkedHashSet<>(list);
        if (setlist.size() != list.size()) {
            return false;
        }

        return true;
    }
    
}
