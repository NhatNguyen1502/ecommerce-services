package rookies.ecommerce.service.upload;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/** Interface for cloud storage operations */
public interface UploadService {

  /**
   * Upload an image to cloud storage
   *
   * @param file the image file to upload
   * @return the URL of the uploaded image
   * @throws IOException if an error occurs during upload
   */
  String uploadImage(MultipartFile file) throws IOException;

  /**
   * Delete an image from cloud storage
   *
   * @param imageUrl the URL of the image to delete
   */
  void deleteImage(String imageUrl);
}
