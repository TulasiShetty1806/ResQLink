package com.resqlink.repository;

import com.resqlink.model.Organization;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.enums.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceItemRepository extends JpaRepository<ResourceItem, Long> {
    Optional<ResourceItem> findByResourceCode(String resourceCode);
    List<ResourceItem> findByCategory(ResourceCategory category);
    List<ResourceItem> findByOrganization(Organization organization);
    List<ResourceItem> findByAvailableQuantityGreaterThan(Integer minQuantity);
    List<ResourceItem> findByCategoryAndAvailableQuantityGreaterThan(ResourceCategory category, Integer minQuantity);

    @Query("SELECT r FROM ResourceItem r WHERE r.category = :category AND r.availableQuantity > 0 ORDER BY r.availableQuantity DESC")
    List<ResourceItem> findAvailableForCategory(ResourceCategory category);

    @Query("SELECT SUM(r.availableQuantity) FROM ResourceItem r")
    Long sumTotalAvailableSupplies();
}
