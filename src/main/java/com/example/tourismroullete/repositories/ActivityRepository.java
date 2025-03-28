
package com.example.tourismroullete.repositories;

import com.example.tourismroullete.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Find activities by category id
    @Query("SELECT a FROM Activity a JOIN a.categories c WHERE c.id = :categoryId")
    List<Activity> findByCategoryId(@Param("categoryId") Long categoryId);

    // Find activities that have all the specified categories
    @Query("SELECT a FROM Activity a JOIN a.categories c WHERE c.id IN :categoryIds GROUP BY a HAVING COUNT(DISTINCT c.id) = :categoryCount")
    List<Activity> findByAllCategoryIds(@Param("categoryIds") Set<Long> categoryIds, @Param("categoryCount") long categoryCount);

    // Find activities that have any of the specified categories
    @Query("SELECT DISTINCT a FROM Activity a JOIN a.categories c WHERE c.id IN :categoryIds")
    List<Activity> findByAnyCategoryIds(@Param("categoryIds") Set<Long> categoryIds);

    // Search activities by name or description and filter by categories
    @Query("SELECT DISTINCT a FROM Activity a JOIN a.categories c WHERE " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "c.id IN :categoryIds")
    List<Activity> searchByTermAndCategories(@Param("searchTerm") String searchTerm, @Param("categoryIds") Set<Long> categoryIds);
}
