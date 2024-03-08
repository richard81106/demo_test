package com.example.demo.controller;

import com.example.demo.model.ExternalApiModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import  org.springframework.http.ResponseEntity;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProducrController {

    @Autowired
    private ProductService productService;

    @GetMapping("/callExternalApi")
    public ResponseEntity<Map<String, Object>> callExternalApi(@RequestParam String queryType,
                                                               @RequestParam String queryId,
                                                               @RequestParam String datatype,
                                                               @RequestParam String rangeStart,
                                                               @RequestParam String rangeEnd) {
        // 呼叫外部 API
        String apiUrl = "https://cathaybk.moneydj.com/w/djjson/FundETFDataJSON.djjson?queryType=" + queryType +
                                                                                        "&queryId=" + queryId +
                                                                                        "&datatype=" + datatype +
                                                                                        "&rangeStart=" + rangeStart +
                                                                                        "&rangeEnd=" + rangeEnd;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonResponse;
        try {
            jsonResponse = objectMapper.readValue(responseBody, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/findAll")
    public List<Product> getAllProducts(){
        return productService.getProducts();
    }

    @PostMapping("/insert")
    public Product insert(@RequestBody Product product){
        return productService.insertProduct(product);
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        try{
            productService.deleteProduct(id);
            return "刪除成功";
        }catch (Exception e){
            return "刪除失敗";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,@RequestBody Product product){
        try{
            productService.updateProduct(id, product);
            return "更新成功";
        }catch (Exception e){
            return "更新失敗";
        }
        
    }
}
