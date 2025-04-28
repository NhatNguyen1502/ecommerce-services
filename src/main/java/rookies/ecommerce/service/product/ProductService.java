package rookies.ecommerce.service.product;

import java.io.IOException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {

  ProductRepository productRepository;
  IUploadService cloudinaryService;
  ICategoryService categoryService;

  /**
   * Retrieves a page of active products sorted by creation time in descending order.
   *
   * @param page the page number
   * @param size the page size
   * @return a page of active products
   */
  @Override
  public Page<IProductWithCategoryNameProjection> getActiveProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return productRepository.findAllByIsDeletedFalse(pageable);
  }

  /**
   * Gets a product by its ID.
   *
   * @param id the unique identifier of the product to be retrieved
   * @return the product with the given ID
   * @throws AppException if the product with the given ID does not exist
   */
  @Override
  public Product getProductById(UUID id) {
    return productRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves a product by its ID, including its category name and other details.
   *
   * @param id the unique identifier of the product to be retrieved
   * @return a {@link ProductDetailResponse} containing the product's details
   * @throws AppException if the product with the given ID does not exist
   */
  @Override
  public ProductDetailResponse getProductDetailById(UUID id) {
    Product product = getProductById(id);
    Category category = product.getCategory();
    return ProductDetailResponse.builder()
        .id(product.getId())
        .category(
            ProductDetailResponse.Category.builder()
                .id(category.getId())
                .name(category.getName())
                .build())
        .name(product.getName())
        .price(product.getPrice())
        .quantity(product.getQuantity())
        .description(product.getDescription())
        .imageUrl(product.getImageUrl())
        .isFeatured(product.isFeatured())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }

  /**
   * Creates a new product with the specified details and uploads its image.
   *
   * @param productDTO the data transfer object containing new product details
   * @param image the image file to be uploaded for the product
   * @throws IOException if an error occurs during image upload
   */
  @Override
  @Transactional
  public void createProduct(CreateProductRequest productDTO, MultipartFile image)
      throws IOException {
    String imageUrl = cloudinaryService.uploadImage(image);

    Category category = categoryService.getCategoryById(productDTO.getCategoryId());

    Product product =
        Product.builder()
            .category(category)
            .name(productDTO.getName())
            .description(productDTO.getDescription())
            .imageUrl(imageUrl)
            .price(productDTO.getPrice())
            .quantity(productDTO.getQuantity())
            .isFeatured(productDTO.isFeatured())
            .build();

    productRepository.save(product);
  }

  /**
   * Updates an existing product with the provided details and optional new image.
   *
   * @param id the unique identifier of the product to be updated
   * @param productDTO the data transfer object containing the updated product details
   * @param image the new image file to be uploaded for the product (optional)
   * @throws IOException if an error occurs during image upload
   * @throws AppException if the product with the given ID does not exist
   */
  @Override
  @Transactional
  public void updateProduct(UUID id, UpdateProductRequest productDTO, MultipartFile image)
      throws IOException {
    Product product =
        productRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

    Category category = categoryService.getCategoryById(productDTO.getCategoryId());

    String imageUrl = product.getImageUrl();
    if (image != null && !image.isEmpty()) {
      // Delete old image if it exists
      if (imageUrl != null && !imageUrl.isEmpty()) {
        cloudinaryService.deleteImage(imageUrl);
      }
      imageUrl = cloudinaryService.uploadImage(image);
    }

    product.setCategory(category);
    product.setName(productDTO.getName());
    product.setDescription(productDTO.getDescription());
    product.setImageUrl(imageUrl);
    product.setPrice(productDTO.getPrice());
    product.setQuantity(productDTO.getQuantity());
    product.setFeatured(productDTO.isFeatured());

    productRepository.save(product);
  }

  /**
   * Marks a product as deleted by setting its isDeleted flag to true.
   *
   * <p>This method is a "soft delete" which means it only marks the product as deleted and does not
   * physically delete the product from the database.
   *
   * @param id the unique identifier of the product to be deleted
   * @throws AppException if the product with the given ID does not exist
   */
  @Override
  @Transactional
  public void deleteProduct(UUID id) {
    Product product =
        productRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

    product.setIsDeleted(true);

    productRepository.save(product);
  }

  /**
   * Retrieves a page of products marked as featured sorted by creation time in descending order.
   *
   * @param page the page number
   * @param size the page size
   * @return a page of featured products
   */
  public Page<IProductWithCategoryNameProjection> getFeaturedProducts(int page, int size) {
    return productRepository.findAllByIsDeletedFalseAndIsFeaturedTrue(PageRequest.of(page, size));
  }

  public Page<IProductWithCategoryNameProjection> getProductsByCategory(
      int page, int size, UUID categoryId) {
    return productRepository.findAllByIsDeletedFalseAndCategoryId(
        categoryId, PageRequest.of(page, size));
  }
}
