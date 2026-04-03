package kg.rubicon.my_app.util.annatation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kg.rubicon.my_app.util.validator.ImageFileValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
@Documented
public @interface ImageFile {

    String message() default "File must be a valid image (png, jpg, jpeg, gif)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
