package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    //Definir algumas propriedades: conexão com servidor e serializadores
    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    //Criando um produtor e enviando a msg "132123,123456,09876512" no tópico "ECOMMERCE_NEW_ORDER"
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       var producer = new KafkaProducer<String, String>(properties());//Criando o produtor e definindo propriedades
       var value = "132123,123456,1234";//Mensagem que será enviada no tópico
       var record = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", value, value);//Configurando o que será gravado no kafka

       producer.send(record, (data, ex) -> {
           if(ex != null){
               ex.printStackTrace();
               return;
           }
           System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() +
                   "/ offfset " + data.offset() + "/ " + data.timestamp());
       }).get();

    }

}
