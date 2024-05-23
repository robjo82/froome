package org.froome.productservice.controller;

import org.froome.productservice.exception.ForbiddenException;
import org.froome.productservice.model.dto.ExceptionDto;
import org.froome.productservice.model.dto.PagedResponse;
import org.froome.productservice.model.dto.ProductDto;
import org.froome.productservice.service.AuthService;
import org.froome.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product", description = "Specific to products, manages the creation, update and deletion of products.")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "There is no need to be authenticated to get all products.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products found"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<PagedResponse<ProductDto>> getAll(
            @RequestParam(required = false, name = "page", defaultValue = "0") Integer page,
            @RequestParam(required = false, name = "size", defaultValue = "10") Integer size
    ) {
        PagedResponse<ProductDto> products = productService.getProducts(page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a product by id",
            description = "There is no need to be authenticated to get a product.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<ProductDto> get(
            @Parameter(description = "ID of the product to be retrieved", required = true)
            @PathVariable long id) {
        ProductDto product = productService.getProduct(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Operation(
            summary = "Create a new product",
            description = "Only an admin can create a product.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto productDto, Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to create a product.");
        } else {
            ProductDto createdProduct = productService.create(productDto);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a product by id",
            description = "Only an admin can update a product.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ProductDto> update(
            @Parameter(description = "ID of the product to be updated", required = true)
            @PathVariable long id,
            @Valid @RequestBody ProductDto productDto, Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to update a product.");
        } else {
            ProductDto updatedProduct = productService.updateProduct(id, productDto);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product by id",
            description = "Only an admin can delete a product.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted"),
                    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the product to be deleted", required = true)
            @PathVariable long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to delete a product.");
        } else {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
