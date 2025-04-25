package rookies.ecommerce.service.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rookies.ecommerce.dto.request.category.CreateCategoryRequest;
import rookies.ecommerce.dto.request.category.UpdateCategoryRequest;
import rookies.ecommerce.dto.response.category.CategorySummaryResponse;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
  CategoryRepository categoryRepository;

  /**
   * Creates a new category based on the given request.
   *
   * @param request details of the new category
   * @throws AppException if the category with the same name already exists
   */
  @Override
  public void createCategory(CreateCategoryRequest request) {
    if (categoryRepository.existsByNameAndIsDeletedFalse(request.getName())) {
      throw new AppException(ErrorCode.CATEGORY_EXISTS, HttpStatus.BAD_REQUEST);
    }

    Category category = Category.builder().name(request.getName()).build();
    categoryRepository.save(category);
  }

  /**
   * Gets a category by its ID.
   *
   * @param id the unique identifier of the category to be retrieved
   * @return the category with the given ID
   * @throws AppException if the category with the given ID does not exist
   */
  @Override
  public CategorySummaryResponse getActiveCategoryById(UUID id) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    Category category =
        currentCategory.orElseThrow(
            () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

    return CategorySummaryResponse.builder().id(category.getId()).name(category.getName()).build();
  }

  @Override
  public Category getCategoryById(UUID id) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    return currentCategory.orElseThrow(
        () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves active categories sorted by creation time in descending order.
   *
   * @return a page of active categories
   */
  @Override
  public List<CategorySummaryResponse> getActiveCategories() {
    return categoryRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
  }

  /**
   * Updates an existing category with the provided details.
   *
   * @param id the unique identifier of the category to be updated
   * @param request the request containing the updated category details
   * @throws AppException if the category with the given ID does not exist
   */
  @Override
  public void updateCategory(UUID id, UpdateCategoryRequest request) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    Category category =
        currentCategory.orElseThrow(
            () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

    category.setName(request.getName());

    categoryRepository.save(category);
  }

  /**
   * Deletes a category by its ID.
   *
   * <p>This method is a "soft delete" which means it only marks the category as deleted and does
   * not physically delete the category from the database.
   *
   * @param id the unique identifier of the category to be deleted
   * @throws AppException if the category with the given ID does not exist
   */
  @Override
  public void deleteCategory(UUID id) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    Category category =
        currentCategory.orElseThrow(
            () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

    category.setIsDeleted(true);

    categoryRepository.save(category);
  }
}
