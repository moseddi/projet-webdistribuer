package tn.esprit.stockservice.exception;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(String message) {
        super(message);
    }

    public StockNotFoundException(Long productId) {
        super("Stock not found for product with ID: " + productId);
    }
}