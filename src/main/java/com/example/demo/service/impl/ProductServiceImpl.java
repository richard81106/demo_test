package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepo;
import com.example.demo.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Override
    public Map<String, Object> insertProduct(Product product) {
        Map<String, Object> response = new HashMap<>();
        try{
            // 檢查日期格式是否為 yyyy-MM
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                formatter.parse(product.getDate());
            } catch (Exception e) {
                response.put("code", HttpStatus.BAD_REQUEST.value());
                response.put("message", "日期格式應為 yyyy-MM-dd");
                return response;
            }

            // 檢查 name 是否存在相同的日期
            boolean existsByName = productRepo.existsByName(product.getName());
            if (existsByName) {
                // 檢查是否存在相同的 name 且不同的 date
                boolean existsDifferentDate = productRepo.existsByNameAndDate(product.getName(), product.getDate());
                if ((existsDifferentDate)) {
                    response.put("code", HttpStatus.BAD_REQUEST.value());
                    response.put("message", "同一名稱不允許擁有2筆一樣日期，新增失敗");
                    return response;
                }
            }

            //新增產品
            Product insertedProduct = productRepo.save(product);
            response.put("code", HttpStatus.OK.value());
            response.put("message", "新增成功");
            response.put("product", insertedProduct);
            return response;

        }catch(Exception e){
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "內部服務器錯誤");
            return response;
        }
    }

    @Override
    public Map<String, Object> updateProduct(Integer id, Product product) {
        Map<String, Object> response = new HashMap<>();
        try{
            //檢查是否存在ID
            if (!productRepo.existsById(id)) {
                response.put("code", HttpStatus.BAD_REQUEST.value());
                response.put("message", "ID不存在，修改失敗");
                return response;
            }

            Product exitProduct = productRepo.findById(id).get();
            exitProduct.setPrice(product.getPrice());

            //執行修改
            exitProduct = productRepo.save(exitProduct);
            response.put("code", HttpStatus.OK.value());
            response.put("message", "修改成功");
            response.put("product", exitProduct);
            return response;

        }catch(Exception e){
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "內部服務器錯誤");
            return response;
        }
    }

    @Override
    public Map<String, Object> deleteProduct(Integer id) {
        Map<String, Object> response = new HashMap<>();
        try{
            //檢查是否存在ID
            if (!productRepo.existsById(id)) {
                response.put("code", HttpStatus.BAD_REQUEST.value());
                response.put("message", "ID不存在，刪除失敗");
                return response;
            }

            //執行刪除
            productRepo.deleteById(id);
            response.put("code", HttpStatus.OK.value());
            response.put("message", "刪除成功");
            return response;

        }catch(Exception e){
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "內部服務器錯誤");
            return response;
        }
    }

    @Override
    public List<Product> getProducts(String name, String date) {
        // 如果 name 和 date 都是空白，則查詢所有產品
        if (StringUtils.isBlank(name) && StringUtils.isBlank(date)) {
            return productRepo.findAll();
        } else {
            // 否則，根據條件篩選產品
            if (StringUtils.isBlank(name)) {
                // 如果 name 是空白，只根據日期篩選
                return productRepo.findByDate(date);
            } else if (StringUtils.isBlank(date)) {
                // 如果 date 是空白，只根據名稱篩選
                return productRepo.findByName(name);
            } else {
                // 如果 name 和 date 都有值，則根據兩個條件篩選
                return productRepo.findByNameAndDate(name, date);
            }
        }
    }
}
