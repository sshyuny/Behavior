package snapshot.behavior.category.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import snapshot.behavior.category.dto.CategoryDTO;
import snapshot.behavior.category.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public List<CategoryDTO> getCategoryList(Long userId) {

        List<CategoryDTO> categoryDTOList = categoryRepository.selectCategoryDTOList(userId);

        List<CategoryDTO> sortedCategoryDTOList = CategoryUnit.sortCategoryDTOListOrderByTime(categoryDTOList);

        return sortedCategoryDTOList;
    }
}
