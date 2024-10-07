package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        List<Product> productList=category.getProducts();
        boolean isProductNotPresent=true;
        for (Product product : productList) {
            if(product.getProductName().equals(productDTO.getProductName())){
                isProductNotPresent=false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            Double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setCategory(category);
            productRepository.save(product);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else {
            throw new APIException("Product already present");
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        if(productList.isEmpty()) {
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOS = productList.stream().map((products)->modelMapper.map(products,ProductDTO.class)).toList();
        return productDTOS;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        List<Product> productList=productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = productList.stream().map((products)->modelMapper.map(products,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword) {
        List<Product> productList=productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%");
        if(productList.isEmpty()) {
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOS = productList.stream().map((products)->modelMapper.map(products,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product currentProduct=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        Product product=modelMapper.map(productDTO, Product.class);
        currentProduct.setProductName(product.getProductName());
        currentProduct.setDescription(product.getDescription());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setDiscount(product.getDiscount());
        currentProduct.setQuantity(product.getQuantity());
        currentProduct.setSpecialPrice(product.getSpecialPrice());
        Product updatedProduct=productRepository.save(currentProduct);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product existingProduct=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        ProductDTO deleteProductDTO=modelMapper.map(existingProduct,ProductDTO.class);
        productRepository.delete(existingProduct);
        return deleteProductDTO;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDB=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        //Upload image to server
        //get file name of uploaded image
        String fileName=fileService.uploadImage(path,image);
        productFromDB.setImage(fileName);
        Product updatedProduct=productRepository.save(productFromDB);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }
}
