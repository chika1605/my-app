package kg.rubicon.my_app.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kg.rubicon.my_app.util.annatation.ImageFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        try {
            if (javax.imageio.ImageIO.read(file.getInputStream()) == null) {
                throw new IllegalArgumentException("Uploaded file must be a valid image (png, jpg, jpeg, gif)");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Uploaded file must be a valid image (png, jpg, jpeg, gif)", e);
        }

        return true;
    }
}
