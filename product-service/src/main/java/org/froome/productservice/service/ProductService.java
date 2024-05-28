package org.froome.productservice.service;

import org.froome.productservice.exception.NotFoundException;
import org.froome.productservice.model.Product;
import org.froome.productservice.model.dto.PagedResponse;
import org.froome.productservice.model.dto.ProductDto;
import org.froome.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductDto create(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    public PagedResponse<ProductDto> getProducts(int page, int size) {
        List<Product> allProductsInStock = productRepository.findByStockGreaterThan(0);

        int totalElements = allProductsInStock.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Product> paginatedProducts = allProductsInStock.subList(fromIndex, toIndex);

        List<ProductDto> products = paginatedProducts.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                products,
                page,
                size,
                totalElements,
                totalPages
        );
    }


    public ProductDto getProduct(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return modelMapper.map(product, ProductDto.class);
    }

    public ProductDto updateProduct(long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setDescription(productDto.getDescription());
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }
}
