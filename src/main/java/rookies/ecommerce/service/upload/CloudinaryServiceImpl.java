package rookies.ecommerce.service.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements UploadService {

  Cloudinary cloudinary;

  public String uploadImage(MultipartFile file) throws IOException {
    var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    return (String) uploadResult.get("url");
  }

  public void deleteImage(String imageUrl) {
    try {
      // Extract public ID from URL
      String publicId = extractPublicIdFromUrl(imageUrl);
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete image from Cloudinary", e);
    }
  }

  private String extractPublicIdFromUrl(String imageUrl) {
    // Extract the public ID from the Cloudinary URL
    // This is a simplified example - actual implementation depends on your URL format
    String[] parts = imageUrl.split("/");
    String fileNameWithExtension = parts[parts.length - 1];
    return fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
  }
}
