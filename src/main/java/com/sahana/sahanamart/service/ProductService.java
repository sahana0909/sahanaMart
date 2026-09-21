package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.model.Category;
import com.sahana.sahanamart.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO createProduct(Long sellerId, String name, String description, BigDecimal price,
                             Integer stockQty, Long categoryId, String imageUrl);
    ProductDTO updateProduct(Long sellerId, Long productId, String name, String description,
                             BigDecimal price, Integer stockQty, Long categoryId, String imageUrl, Boolean active);
    boolean deleteProduct(Long sellerId, Long productId);
    Optional<ProductDTO> getProductById(Long id);
    List<ProductDTO> searchAndFilter(String keyword, Long categoryId, Boolean activeOnly, int page, int pageSize);
    int getTotalCount(String keyword, Long categoryId, Boolean activeOnly);
    List<ProductDTO> getSellerProducts(Long sellerId);
    boolean setListingStatus(Long productId, boolean active);
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
}
