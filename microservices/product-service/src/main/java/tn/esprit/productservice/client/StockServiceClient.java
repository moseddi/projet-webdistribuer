package tn.esprit.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("/api/v1/stocks/product/{productId}/availability")
    Boolean checkStockAvailability(@PathVariable("productId") Long productId,
                                   @RequestParam("quantity") Integer quantity);

    @GetMapping("/api/v1/stocks/product/{productId}/available")
    Integer getAvailableQuantity(@PathVariable("productId") Long productId);
}