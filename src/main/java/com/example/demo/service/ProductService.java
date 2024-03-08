package com.example.demo.service;

import com.example.demo.entity.Product;

import java.util.List;

public interface ProductService {
     Product insertProduct(Product product);

     Product updateProduct(Integer id, Product product);

     void deleteProduct(Integer id);

     List<Product> getProducts();
}
