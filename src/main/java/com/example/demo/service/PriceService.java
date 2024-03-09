package com.example.demo.service;

import com.example.demo.entity.Product;

import java.util.List;
import java.util.Map;

public interface PriceService {
    Map<String, Object> getProductGroup(String startDate, String endDate);
}
