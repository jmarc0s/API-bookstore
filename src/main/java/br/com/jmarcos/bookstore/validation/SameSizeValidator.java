package br.com.jmarcos.bookstore.validation;

import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.jmarcos.bookstore.validation.constraints.SameSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SameSizeValidator implements ConstraintValidator<SameSize, Object> {
    private String firstListFieldName;
    private String secondListFieldName;

    @Override
    public void initialize(SameSize constraintAnnotation) {
        firstListFieldName = constraintAnnotation.firstList();
        secondListFieldName = constraintAnnotation.secondList();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        List<?> firstList = (List<?>) beanWrapper.getPropertyValue(firstListFieldName);
        List<?> secondList = (List<?>) beanWrapper.getPropertyValue(secondListFieldName);

        return firstList.size() == secondList.size();
    }
}

