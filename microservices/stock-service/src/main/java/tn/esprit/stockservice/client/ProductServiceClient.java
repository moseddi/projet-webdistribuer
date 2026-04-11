package tn.esprit.stockservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/products/sku/{sku}")
    ProductDto getProductBySku(@PathVariable("sku") String sku);

    @PutMapping("/api/v1/products/{id}/stock")
    void updateProductStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}