package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Category;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public ProductDTO createProduct(Long sellerId, String name, String description, BigDecimal price,
                                    Integer stockQty, Long categoryId, String imageUrl) {
        if (sellerId == null || sellerId <= 0) {
            throw new UnauthorizedException("Valid seller ID required");
        }
        ValidationUtil.validateProduct(name, description, price, stockQty, categoryId);

        if (productDAO.findCategoryById(categoryId).isEmpty()) {
            throw new ValidationException("categoryId", "Selected category does not exist");
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setCategoryId(categoryId);
        product.setName(name.trim());
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80");
        product.setActive(true);

        Product created = productDAO.create(product);
        return getProductById(created.getId()).orElse(ProductDTO.fromProduct(created));
    }

    @Override
    public ProductDTO updateProduct(Long sellerId, Long productId, String name, String description,
                                    BigDecimal price, Integer stockQty, Long categoryId, String imageUrl, Boolean active) {
        ValidationUtil.validateProduct(name, description, price, stockQty, categoryId);

        Product existing = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You are not authorized to update another seller's listing");
        }

        if (productDAO.findCategoryById(categoryId).isEmpty()) {
            throw new ValidationException("categoryId", "Selected category does not exist");
        }

        existing.setName(name.trim());
        existing.setDescription(description != null ? description.trim() : "");
        existing.setPrice(price);
        existing.setStockQty(stockQty);
        existing.setCategoryId(categoryId);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            existing.setImageUrl(imageUrl.trim());
        }
        if (active != null) {
            existing.setActive(active);
        }

        productDAO.update(existing);
        return getProductById(productId).orElse(ProductDTO.fromProduct(existing));
    }

    @Override
    public boolean deleteProduct(Long sellerId, Long productId) {
        Product existing = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You are not authorized to delete another seller's listing");
        }
        return productDAO.delete(productId);
    }

    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return productDAO.findById(id).map(ProductDTO::fromProduct);
    }

    @Override
    public List<ProductDTO> searchAndFilter(String keyword, Long categoryId, Boolean activeOnly, int page, int pageSize) {
        int limit = pageSize > 0 ? pageSize : 12;
        int offset = Math.max(0, (page - 1) * limit);
        return productDAO.searchAndFilter(keyword, categoryId, activeOnly, limit, offset).stream()
            .map(ProductDTO::fromProduct)
            .collect(Collectors.toList());
    }

    @Override
    public int getTotalCount(String keyword, Long categoryId, Boolean activeOnly) {
        return productDAO.countSearchAndFilter(keyword, categoryId, activeOnly);
    }

    @Override
    public List<ProductDTO> getSellerProducts(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return List.of();
        }
        return productDAO.findBySellerId(sellerId).stream()
            .map(ProductDTO::fromProduct)
            .collect(Collectors.toList());
    }

    @Override
    public boolean setListingStatus(Long productId, boolean active) {
        return productDAO.setActiveStatus(productId, active);
    }

    @Override
    public List<Category> getAllCategories() {
        return productDAO.findAllCategories();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return productDAO.findCategoryById(id);
    }
}
