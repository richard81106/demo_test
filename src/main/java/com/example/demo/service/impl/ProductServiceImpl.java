package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepo;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public Product insertProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Integer id, Product product) {
        Product exitProduct = productRepo.findById(id).get();
        exitProduct.setName(product.getName());
        exitProduct.setPrice(product.getPrice());
        exitProduct.setQty(product.getQty());
        return productRepo.save(exitProduct);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepo.deleteById(id);
    }

    @Override
    public List<Product> getProducts() {
        return productRepo.findAll();
    }
}
