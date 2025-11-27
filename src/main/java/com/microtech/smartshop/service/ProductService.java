package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.ProductCreateRequest;
import com.microtech.smartshop.dto.request.ProductUpdateRequest;
import com.microtech.smartshop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest req);
    ProductResponse updateProduct(Long id, ProductUpdateRequest req);
    ResponseEntity<String> deleteProduct(Long id);
    Page<ProductResponse> listProducts(String nomFilter, int page, int size, String sortBy, String direction);
    ProductResponse getProduct(Long id);
}
