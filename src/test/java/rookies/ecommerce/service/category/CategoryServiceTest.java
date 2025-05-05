package rookies.ecommerce.service.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rookies.ecommerce.dto.request.category.CreateCategoryRequest;
import rookies.ecommerce.dto.request.category.UpdateCategoryRequest;
import rookies.ecommerce.dto.response.category.CategorySummaryResponse;
import rookies.ecommerce.entity.BaseEntityAudit;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @InjectMocks private CategoryService categoryService;

  @Mock private CategoryRepository categoryRepository;

  private Category category;
  private final UUID categoryId = UUID.randomUUID();
  private final String categoryName = "Electronics";

  @BeforeEach
  void setUp() {
    // Set up a sample Category entity
    category = Category.builder().name(categoryName).build();
    category.setId(categoryId);
    category.setIsDeleted(false);
  }

  @Test
  void createCategory_newCategory_success() {
    // Arrange
    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setName(categoryName);
    when(categoryRepository.existsByNameAndIsDeletedFalse(categoryName)).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // Act
    assertDoesNotThrow(() -> categoryService.createCategory(request));

    // Assert
    verify(categoryRepository, times(1)).existsByNameAndIsDeletedFalse(categoryName);
    verify(categoryRepository, times(1))
        .save(argThat(cat -> cat.getName().equals(categoryName) && !cat.getIsDeleted()));
  }

  @Test
  void createCategory_categoryExists_throwsAppException() {
    // Arrange
    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setName(categoryName);
    when(categoryRepository.existsByNameAndIsDeletedFalse(categoryName)).thenReturn(true);

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> categoryService.createCategory(request));
    assertEquals(ErrorCode.CATEGORY_EXISTS, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(categoryRepository, times(1)).existsByNameAndIsDeletedFalse(categoryName);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void getActiveCategoryById_categoryFound_success() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId))
        .thenReturn(Optional.of(category));

    // Act
    CategorySummaryResponse response = categoryService.getActiveCategoryById(categoryId);

    // Assert
    assertNotNull(response);
    assertEquals(categoryId, response.getId());
    assertEquals(categoryName, response.getName());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
  }

  @Test
  void getActiveCategoryById_categoryNotFound_throwsAppException() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> categoryService.getActiveCategoryById(categoryId));
    assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
  }

  @Test
  void getCategoryById_categoryFound_success() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId))
        .thenReturn(Optional.of(category));

    // Act
    Category result = categoryService.getCategoryById(categoryId);

    // Assert
    assertNotNull(result);
    assertEquals(categoryId, result.getId());
    assertEquals(categoryName, result.getName());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
  }

  @Test
  void getCategoryById_categoryNotFound_throwsAppException() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> categoryService.getCategoryById(categoryId));
    assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
  }

  @Test
  void getActiveCategories_categoriesFound_success() {
    // Arrange
    List<CategorySummaryResponse> categories =
        Collections.singletonList(
            CategorySummaryResponse.builder().id(categoryId).name(categoryName).build());
    when(categoryRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()).thenReturn(categories);

    // Act
    List<CategorySummaryResponse> result = categoryService.getActiveCategories();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(categoryId, result.get(0).getId());
    assertEquals(categoryName, result.get(0).getName());
    verify(categoryRepository, times(1)).findAllByIsDeletedFalseOrderByCreatedAtDesc();
  }

  @Test
  void getActiveCategories_noCategories_returnsEmptyList() {
    // Arrange
    when(categoryRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc())
        .thenReturn(Collections.emptyList());

    // Act
    List<CategorySummaryResponse> result = categoryService.getActiveCategories();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(categoryRepository, times(1)).findAllByIsDeletedFalseOrderByCreatedAtDesc();
  }

  @Test
  void updateCategory_categoryFound_success() {
    // Arrange
    String newName = "Updated Electronics";
    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName(newName);
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId))
        .thenReturn(Optional.of(category));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // Act
    assertDoesNotThrow(() -> categoryService.updateCategory(categoryId, request));

    // Assert
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
    verify(categoryRepository, times(1)).save(argThat(cat -> cat.getName().equals(newName)));
  }

  @Test
  void updateCategory_categoryNotFound_throwsAppException() {
    // Arrange
    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Updated Electronics");
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> categoryService.updateCategory(categoryId, request));
    assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void deleteCategory_categoryFound_success() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId))
        .thenReturn(Optional.of(category));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // Act
    assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

    // Assert
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
    verify(categoryRepository, times(1)).save(argThat(BaseEntityAudit::getIsDeleted));
  }

  @Test
  void deleteCategory_categoryNotFound_throwsAppException() {
    // Arrange
    when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> categoryService.deleteCategory(categoryId));
    assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(categoryRepository, times(1)).findByIdAndIsDeletedFalse(categoryId);
    verify(categoryRepository, never()).save(any());
  }
}
