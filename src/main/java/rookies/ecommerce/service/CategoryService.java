package rookies.ecommerce.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import rookies.ecommerce.dto.request.category.CreateCategoryReq;
import rookies.ecommerce.dto.request.category.UpdateCategoryReq;
import rookies.ecommerce.dto.response.category.CategorySummaryRes;
import rookies.ecommerce.entity.Category;

public interface CategoryService {
  void createCategory(CreateCategoryReq req);

  CategorySummaryRes getCategoryById(UUID id);

  Page<Category> getCategories(Integer page, Integer size);

  void updateCategory(UUID id, UpdateCategoryReq req);

  void deleteCategory(UUID id);
}
