package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.ProductCreateRequest;
import com.microtech.smartshop.dto.request.ProductUpdateRequest;
import com.microtech.smartshop.dto.response.ProductResponse;
import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.mapper.ProductMapper;
import com.microtech.smartshop.repository.OrderItemRepository;
import com.microtech.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ProductResponse createProduct(ProductCreateRequest req) {
        Product p = productMapper.toEntity(req);
        p.setDeleted(false);
        return productMapper.toResponse(productRepository.save(p));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest req) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        if(p.isDeleted()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Cannot update a deleted product");
        }
        productMapper.updateProductFromDto(req, p);
        return productMapper.toResponse(productRepository.save(p));
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        boolean used = orderItemRepository.existsByProductId(id);
        if(p.isDeleted()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Product is already deleted");
        }
        if (used) {
            p.setDeleted(true);
            productRepository.save(p);
            return ResponseEntity.ok("Product marked as deleted (soft delete)");
        } else {
            productRepository.delete(p);
            return ResponseEntity.ok("Product deleted successfully (hard delete)");
        }
    }

    @Override
    public Page<ProductResponse> listProducts(String nomFilter, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> pageResult;
        if (nomFilter == null || nomFilter.isBlank()) {
            pageResult = productRepository.findByDeletedFalse(pageable);
        } else {
            pageResult = productRepository.findByDeletedFalseAndNomContainingIgnoreCase(nomFilter, pageable);
        }
        return pageResult.map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        if (p.isDeleted()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product is deleted");
        return productMapper.toResponse(p);
    }
}
