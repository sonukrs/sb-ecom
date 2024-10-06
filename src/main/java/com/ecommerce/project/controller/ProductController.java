package com.ecommerce.project.controller;


import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.CategoryService;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO savedProductDTO=productService.addProduct(productDTO,categoryId);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){
        List<ProductDTO> productDTOS=productService.getAllProducts();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId){
        ProductResponse productResponse=productService.getProductByCategory(categoryId);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword){
        ProductResponse productResponse=productService.getProductByKeyword(keyword);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId){
        ProductDTO updatedProductDTO=productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(updatedProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO productDTO=productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }

    @PutMapping("/admin/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProductDTO=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedProductDTO,HttpStatus.OK);
    }
}
