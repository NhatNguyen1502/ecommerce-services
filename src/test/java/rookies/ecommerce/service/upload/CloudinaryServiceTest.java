package rookies.ecommerce.service.upload;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

class CloudinaryServiceTest {

  @Mock private Cloudinary cloudinary;
  @Mock private Uploader uploader;
  @Mock private MultipartFile file;

  @InjectMocks private CloudinaryService cloudinaryService;

  @BeforeEach
  void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    when(cloudinary.uploader()).thenReturn(uploader);
  }

  @Test
  void uploadImage_validFile_shouldReturnImageUrl() throws IOException {
    byte[] fileBytes = new byte[] {1, 2, 3};
    String expectedUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg";

    when(file.getBytes()).thenReturn(fileBytes);
    when(uploader.upload(eq(fileBytes), anyMap())).thenReturn(Map.of("url", expectedUrl));

    String result = cloudinaryService.uploadImage(file);

    assertEquals(expectedUrl, result);
    verify(file, times(1)).getBytes();
    verify(uploader, times(1)).upload(any(), anyMap());
  }

  @Test
  void deleteImage_validUrl_shouldCallDestroy() throws IOException {
    String imageUrl = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg";
    String publicId = "sample";

    cloudinaryService.deleteImage(imageUrl);

    verify(uploader, times(1)).destroy(eq(publicId), anyMap());
  }

  @Test
  void deleteImage_exceptionThrown_shouldWrapInRuntimeException() throws IOException {
    String imageUrl = "https://res.cloudinary.com/demo/image/upload/v123456/sample.jpg";

    doThrow(new IOException("Delete failed")).when(uploader).destroy(anyString(), anyMap());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> cloudinaryService.deleteImage(imageUrl));

    assertEquals("Failed to delete image from Cloudinary", exception.getMessage());
    assertInstanceOf(IOException.class, exception.getCause());
  }
}
