package financeTracker.models.repository;

import financeTracker.models.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByOwnerId(int ownerId);
    List<Category> findAllByOwnerIsNull();
    Category findByIdAndOwnerId(int categoryId, int ownerId);
    Category findByIdAndOwnerIsNull(int categoryId);
}
