package rookies.ecommerce.service.product;

import java.io.IOException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import rookies.ecommerce.dto.projection.product.ProductWithCategoryNameProjection;
import rookies.ecommerce.dto.request.product.CreateProductRequest;
import rookies.ecommerce.dto.request.product.UpdateProductRequest;
import rookies.ecommerce.dto.response.product.ProductDetailResponse;
import rookies.ecommerce.entity.Product;

/** Interface for product management operations */
public interface ProductService {

  /**
   * Get all products with pagination
   *
   * @param page the page number
   * @param size the page size
   * @return paginated list of products
   */
  Page<ProductWithCategoryNameProjection> getActiveProducts(int page, int size);

  /**
   * Get a product by ID
   *
   * @param id the product ID
   * @return the product with the specified ID
   */
  Product getProductById(UUID id);

  ProductDetailResponse getProductDetailById(UUID id);

  /**
   * Create a new product
   *
   * @param productDTO the product data
   * @param image the product image
   * @throws IOException if an error occurs during image upload
   */
  void createProduct(CreateProductRequest productDTO, MultipartFile image) throws IOException;

  /**
   * Update an existing product
   *
   * @param id the ID of the product to update
   * @param productDTO the updated product data
   * @param image the updated product image (optional)
   * @throws IOException if an error occurs during image upload
   */
  void updateProduct(UUID id, UpdateProductRequest productDTO, MultipartFile image)
      throws IOException;

  /**
   * Delete a product
   *
   * @param id the ID of the product to delete
   */
  void deleteProduct(UUID id);
}
