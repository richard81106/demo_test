package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.service.PriceService;
import com.example.demo.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private ProductRepo productRepo;

    /*@
        先取得商品名稱分組，在計算 漲跌[後收-前收] 和 漲跌幅[(後收-前收)/前收]
     */
    @Override
    public Map<String, Object> getProductGroup(String startDate, String endDate){
        List<Product> products = new ArrayList<Product>();
        Map<String, Object> response = new HashMap<>();
        try{
            // 檢查日期格式是否為 yyyy-MM
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                formatter.parse(startDate);
                formatter.parse(endDate);
            } catch (Exception e) {
                response.put("code", HttpStatus.BAD_REQUEST.value());
                response.put("message", "日期格式應為 yyyy-MM-dd");
                return response;
            }

            //驗證 結束日 不能小於等於 開始日
            if (startDate.compareTo(endDate) >= 0) {
                response.put("code", HttpStatus.BAD_REQUEST.value());
                response.put("message", "結束日期不能小於等於開始日期");
                return response;
            }

            //個別查詢 開始，結束日期 商品資料
            products.addAll(productRepo.findByDate(startDate));
            products.addAll(productRepo.findByDate(endDate));

            //商品名稱進行分組，若商品有2筆資料，就要取出來
            Map<String, List<Product>> pricesByProductName = products.stream()
                    .collect(Collectors.groupingBy(Product::getName))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue().size() >= 2)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));;

            //取出 list 計算 漲跌[後收-前收] 和 漲跌幅[(後收-前收)/前收]
            List<Map<String, Object>> priceChanges = new ArrayList<Map<String, Object>>();
            pricesByProductName.forEach((name, productList)->{
                //先按照日期排序
                Collections.sort(productList, Comparator.comparing(Product::getDate));

                Map<String, Object> price = new HashMap<String, Object>();
                price.put("name", name);
                price.put("price_rises_and_falls", calculatePriceChange(productList));
                price.put("price_increase_or_decrease", calculatePriceChangePercentage(productList));
                priceChanges.add(price);
            });

            response.put("code", HttpStatus.OK.value());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("price_change", priceChanges);

        }catch(Exception e){
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "內部服務器錯誤");
            return response;
        }

        return response;
    }

    public double calculatePriceChange(List<Product> productList) {
        //取得 開始日商品的價格
        double firstPrice = productList.get(0).getPrice();
        //取得 結束日商品的價格
        double lastPrice = productList.get(productList.size() - 1).getPrice();

        return lastPrice - firstPrice;
    }

    public double calculatePriceChangePercentage(List<Product> productList) {
        //取得 開始日商品的價格
        double firstPrice = productList.get(0).getPrice();
        //取得 結束日商品的價格
        double lastPrice = productList.get(productList.size() - 1).getPrice();

        return (lastPrice - firstPrice) / firstPrice;
    }
}
