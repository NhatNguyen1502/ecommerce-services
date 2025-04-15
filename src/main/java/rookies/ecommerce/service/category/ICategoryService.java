package rookies.ecommerce.service.category;

import java.util.UUID;
import org.springframework.data.domain.Page;
import rookies.ecommerce.dto.request.category.CreateCategoryRequest;
import rookies.ecommerce.dto.request.category.UpdateCategoryRequest;
import rookies.ecommerce.dto.response.category.CategorySummaryResponse;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.exception.AppException;

/** Service interface for category management operations. */
public interface ICategoryService {
  /**
   * Creates a new category based on the given request.
   *
   * @param request details of the new category
   */
  void createCategory(CreateCategoryRequest request);

  /**
   * Gets an active category by the given ID.
   *
   * @param id the id of the category to be retrieved
   * @return the category details
   * @throws AppException if the category is not found
   */
  CategorySummaryResponse getActiveCategoryById(UUID id);

  Category getCategoryById(UUID id);

  Page<CategorySummaryResponse> getActiveCategories(Integer page, Integer size);

  /**
   * Updates an existing category with the provided details.
   *
   * @param id the unique identifier of the category to be updated
   * @param request the request containing the updated category details
   * @throws AppException if the category is not found
   */
  void updateCategory(UUID id, UpdateCategoryRequest request);

  /**
   * Deletes a category by the given ID.
   *
   * @param id the id of the category to be deleted
   * @throws AppException if the category does not exist
   */
  void deleteCategory(UUID id);
}
