package app.project.jwt.common.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegNoValidator.class)
public @interface RegNoValid {
    String message() default "올바른 주민등록번호를 입력하여 주시기 바랍니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
