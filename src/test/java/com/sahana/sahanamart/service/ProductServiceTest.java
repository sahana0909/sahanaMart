package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Category;
import com.sahana.sahanamart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productDAO);
    }

    @Test
    void createProduct_Success() {
        when(productDAO.findCategoryById(1L)).thenReturn(Optional.of(new Category(1L, "Tech", "Desc", null)));
        when(productDAO.create(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(50L);
            return p;
        });
        when(productDAO.findById(50L)).thenAnswer(inv -> {
            Product p = new Product(50L, 2L, 1L, "Laptop", "Desc", new BigDecimal("50000.00"), 10, null, true, null);
            return Optional.of(p);
        });

        ProductDTO created = productService.createProduct(2L, "Laptop", "Desc", new BigDecimal("50000.00"), 10, 1L, null);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(50L);
        assertThat(created.getName()).isEqualTo("Laptop");
    }

    @Test
    void createProduct_InvalidPrice_ThrowsValidationException() {
        assertThatThrownBy(() -> productService.createProduct(2L, "Laptop", "Desc", new BigDecimal("-10.00"), 10, 1L, null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Price must be greater than 0");
    }

    @Test
    void updateProduct_WrongSeller_ThrowsUnauthorizedException() {
        Product existing = new Product(10L, 99L, 1L, "Item", "Desc", new BigDecimal("100.00"), 5, null, true, null);
        when(productDAO.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productService.updateProduct(2L, 10L, "New Item", "Desc", new BigDecimal("120.00"), 5, 1L, null, true))
            .isInstanceOf(UnauthorizedException.class);
    }
}
