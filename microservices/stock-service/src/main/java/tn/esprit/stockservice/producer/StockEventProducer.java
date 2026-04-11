package tn.esprit.stockservice.producer;

import tn.esprit.stockservice.event.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockEvent(StockEvent event) {
        kafkaTemplate.send("stock-events", event);
        log.info("Stock event sent: {}", event);
    }
}
