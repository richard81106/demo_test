package com.example.demo.service;

import com.example.demo.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
     Map<String, Object> insertProduct(Product product);

     Map<String, Object> updateProduct(Integer id, Product product);

     Map<String, Object> deleteProduct(Integer id);

     List<Product> getProducts(String name, String date);
}
