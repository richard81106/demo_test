package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PriceService PriceService;

    @Autowired
    private PriceService priceRepo;

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
    public List<Product> getAllProducts(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String date){
        return productService.getProducts(name, date);
    }

    @GetMapping("/getPriceChange")
    public ResponseEntity<Map<String, Object>> getPriceChange(@RequestParam(required = true) String startDate,
                                                              @RequestParam(required = true) String endDate){
        Map<String, Object> response = PriceService.getProductGroup(startDate, endDate);
        if(response.get("code").toString().equals("200")){
            return ResponseEntity.ok().body(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Product product) {
        Map<String, Object> response = productService.insertProduct(product);
        if(response.get("code").toString().equals("200")){
            return ResponseEntity.ok().body(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id){
        Map<String, Object> response = productService.deleteProduct(id);;
        if(response.get("code").toString().equals("200")){
            return ResponseEntity.ok().body(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id,@RequestBody Product product){
        Map<String, Object> response = productService.updateProduct(id, product);
        if(response.get("code").toString().equals("200")){
            return ResponseEntity.ok().body(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
