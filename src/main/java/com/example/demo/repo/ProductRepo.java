package com.example.demo.repo;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndDate(String name, String date);

    List<Product> findByDate(String date);
    List<Product> findByName(String name);
    List<Product> findByNameAndDate(String name, String date);
}
