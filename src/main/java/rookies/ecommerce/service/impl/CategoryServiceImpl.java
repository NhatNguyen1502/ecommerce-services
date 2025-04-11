package rookies.ecommerce.service.impl;

import java.util.Optional;
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
import rookies.ecommerce.dto.request.category.CreateCategoryReq;
import rookies.ecommerce.dto.request.category.UpdateCategoryReq;
import rookies.ecommerce.dto.response.category.CategorySummaryRes;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CategoryRepository;
import rookies.ecommerce.service.CategoryService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
  CategoryRepository categoryRepository;

  @Override
  public void createCategory(CreateCategoryReq req) {
    if (categoryRepository.existsByNameAndIsDeletedFalse(req.getName())) {
      throw new AppException(ErrorCode.CATEGORY_EXISTS, HttpStatus.BAD_REQUEST);
    }

    Category category = Category.builder().name(req.getName()).build();
    categoryRepository.save(category);
  }

  @Override
  public CategorySummaryRes getCategoryById(UUID id) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    Category category = currentCategory.orElseThrow();

    return CategorySummaryRes.builder().id(category.getId()).name(category.getName()).build();
  }

  @Override
  public Page<Category> getCategories(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    return categoryRepository.findAllByIsDeletedFalse(pageable);
  }

  @Override
  public void updateCategory(UUID id, UpdateCategoryReq req) {
    Optional<Category> currentCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
    Category category =
        currentCategory.orElseThrow(
            () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

    category.setName(req.getName());

    categoryRepository.save(category);
  }

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
