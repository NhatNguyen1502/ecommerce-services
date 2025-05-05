package rookies.ecommerce.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import rookies.ecommerce.dto.projection.product.IProductWithCategoryNameProjection;
import rookies.ecommerce.dto.request.product.CreateProductRequest;
import rookies.ecommerce.dto.request.product.UpdateProductRequest;
import rookies.ecommerce.dto.response.product.ProductDetailResponse;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.entity.Product;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.ProductRepository;
import rookies.ecommerce.service.category.ICategoryService;
import rookies.ecommerce.service.upload.IUploadService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock ProductRepository productRepository;

  @Mock IUploadService cloudinaryService;

  @Mock ICategoryService categoryService;

  @InjectMocks ProductService productService;

  UUID productId = UUID.randomUUID();
  UUID categoryId = UUID.randomUUID();

  @Test
  void getActiveProducts_shouldReturnPagedProducts() {
    // Given
    int page = 0;
    int size = 2;
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    IProductWithCategoryNameProjection product1 = mock(IProductWithCategoryNameProjection.class);
    IProductWithCategoryNameProjection product2 = mock(IProductWithCategoryNameProjection.class);

    List<IProductWithCategoryNameProjection> products = List.of(product1, product2);
    Page<IProductWithCategoryNameProjection> mockPage =
        new PageImpl<>(products, pageable, products.size());

    when(productRepository.findAllByIsDeletedFalse(pageable)).thenReturn(mockPage);

    // When
    Page<IProductWithCategoryNameProjection> result = productService.getActiveProducts(page, size);

    // Then
    assertEquals(2, result.getContent().size());
    verify(productRepository).findAllByIsDeletedFalse(pageable);
  }

  @Test
  void getProductById_existingProduct_shouldReturnProduct() {
    Product product = Product.builder().name("Test Product").build();
    product.setId(productId);
    product.setIsDeleted(false);

    when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));

    Product result = productService.getProductById(productId);

    assertNotNull(result);
    assertEquals(productId, result.getId());
    assertEquals("Test Product", result.getName());
    assertFalse(result.getIsDeleted());

    verify(productRepository, times(1)).findByIdAndIsDeletedFalse(productId);
  }

  @Test
  void getProductById_notFound_shouldThrowAppException() {
    when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(AppException.class, () -> productService.getProductById(productId));

    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }

  @Test
  void getProductDetailById_shouldReturnCorrectResponse() {
    Category category = Category.builder().name("Electronics").build();
    category.setId(categoryId);

    Product product =
        Product.builder()
            .name("Laptop")
            .category(category)
            .description("Gaming laptop")
            .imageUrl("http://image.url")
            .price(1000.0)
            .quantity(5)
            .isFeatured(true)
            .build();
    product.setId(productId);

    when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));

    ProductDetailResponse response = productService.getProductDetailById(productId);

    assertNotNull(response);
    assertEquals("Laptop", response.getName());
    assertEquals("Electronics", response.getCategory().getName());
  }

  @Test
  void createProduct_shouldUploadImageAndSaveProduct() throws IOException {
    CreateProductRequest request = new CreateProductRequest();
    request.setName("Phone");
    request.setCategoryId(categoryId);
    request.setPrice(500.0);
    request.setQuantity(10);
    request.setDescription("New phone");
    request.setFeatured(true);

    MultipartFile image = mock(MultipartFile.class);
    String uploadedImageUrl = "http://image.url";

    Category category = Category.builder().name("Phones").build();
    category.setId(categoryId);

    when(cloudinaryService.uploadImage(image)).thenReturn(uploadedImageUrl);
    when(categoryService.getCategoryById(categoryId)).thenReturn(category);

    productService.createProduct(request, image);

    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(productCaptor.capture());

    Product saved = productCaptor.getValue();
    assertEquals("Phone", saved.getName());
    assertEquals(uploadedImageUrl, saved.getImageUrl());
    assertEquals(category, saved.getCategory());
  }

  @Test
  void updateProduct_withImage_shouldUpdateProductAndReplaceImage() throws IOException {
    // Given
    Product existingProduct = new Product();
    existingProduct.setId(productId);
    existingProduct.setImageUrl("old-image-url");
    existingProduct.setIsDeleted(false);

    UpdateProductRequest updateRequest = new UpdateProductRequest();
    updateRequest.setName("Updated Name");
    updateRequest.setDescription("Updated Desc");
    updateRequest.setPrice(200.0);
    updateRequest.setQuantity(10);
    updateRequest.setFeatured(true);
    updateRequest.setCategoryId(categoryId);

    MultipartFile image = mock(MultipartFile.class);

    Category category = new Category();
    category.setId(categoryId);
    category.setName("Electronics");

    when(productRepository.findByIdAndIsDeletedFalse(productId))
        .thenReturn(Optional.of(existingProduct));
    when(categoryService.getCategoryById(categoryId)).thenReturn(category);
    when(image.isEmpty()).thenReturn(false);
    when(cloudinaryService.uploadImage(image)).thenReturn("new-image-url");

    // When
    productService.updateProduct(productId, updateRequest, image);

    // Then
    verify(cloudinaryService).deleteImage("old-image-url");
    verify(cloudinaryService).uploadImage(image);
    verify(productRepository).save(existingProduct);

    assertEquals("Updated Name", existingProduct.getName());
    assertEquals("Updated Desc", existingProduct.getDescription());
    assertEquals(200.0, existingProduct.getPrice());
    assertEquals(10, existingProduct.getQuantity());
    assertTrue(existingProduct.isFeatured());
    assertEquals(category, existingProduct.getCategory());
    assertEquals("new-image-url", existingProduct.getImageUrl());
  }

  @Test
  void deleteProduct_existingProduct_shouldMarkAsDeleted() {
    Product product = new Product();
    product.setId(productId);
    product.setIsDeleted(false);

    when(productRepository.findByIdAndIsDeletedFalse(productId)).thenReturn(Optional.of(product));

    productService.deleteProduct(productId);

    assertTrue(product.getIsDeleted());
    verify(productRepository).save(product);
  }

  @Test
  void getFeaturedProducts_shouldReturnPageOfFeaturedProducts() {
    int page = 0;
    int size = 5;
    Pageable pageable = PageRequest.of(page, size);

    Page<IProductWithCategoryNameProjection> mockPage = mock(Page.class);

    when(productRepository.findAllByIsDeletedFalseAndIsFeaturedTrue(pageable)).thenReturn(mockPage);

    Page<IProductWithCategoryNameProjection> result =
        productService.getFeaturedProducts(page, size);

    assertEquals(mockPage, result);
    verify(productRepository).findAllByIsDeletedFalseAndIsFeaturedTrue(pageable);
  }

  @Test
  void getProductsByCategory_shouldReturnPageOfProducts() {
    int page = 1;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Page<IProductWithCategoryNameProjection> mockPage = mock(Page.class);

    when(productRepository.findAllByIsDeletedFalseAndCategoryId(categoryId, pageable))
        .thenReturn(mockPage);

    Page<IProductWithCategoryNameProjection> result =
        productService.getProductsByCategory(page, size, categoryId);

    assertEquals(mockPage, result);
    verify(productRepository).findAllByIsDeletedFalseAndCategoryId(categoryId, pageable);
  }
}
