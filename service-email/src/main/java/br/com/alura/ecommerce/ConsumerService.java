package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.lang.String;

public interface ConsumerService<String> {
    void parse(ConsumerRecord<String, Message< String>> record);
     java.lang.String getTopic();

     java.lang.String getConsumerGroup();

}
