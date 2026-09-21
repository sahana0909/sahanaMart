package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Category;
import com.sahana.sahanamart.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Optional<Product> findById(Long id);
    Product create(Product product);
    boolean update(Product product);
    boolean delete(Long id);
    boolean updateStock(Long productId, int quantityDelta);
    boolean setActiveStatus(Long productId, boolean active);

    List<Product> findAllActive(int limit, int offset);
    List<Product> searchAndFilter(String keyword, Long categoryId, Boolean activeOnly, int limit, int offset);
    int countSearchAndFilter(String keyword, Long categoryId, Boolean activeOnly);

    List<Product> findBySellerId(Long sellerId);
    List<Category> findAllCategories();
    Optional<Category> findCategoryById(Long id);
}
