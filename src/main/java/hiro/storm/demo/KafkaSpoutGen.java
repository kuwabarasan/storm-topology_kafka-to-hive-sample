package hiro.storm.demo;


import org.apache.log4j.Logger;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;

import java.util.Properties;

public class KafkaSpoutGen {

    private static final String KAFKA_ZKCONNECTSTRING = "kafka.zk.connect.string";
    private static final String KAFKA_TOPIC           = "kafka.topic.name";
    private static final String KAFKA_ZKROOT          = "kafka.zk.root";
    private static final String KAFKA_SPOUTID         = "kafka.spout.id";
    private static final String KAFKA_PROTOCOL        = "kafka.security.protocol";

    private static final Logger LOG = Logger.getLogger(KafkaSpoutGen.class);


    public static KafkaSpout createKafkaSpout(Properties props){

        BrokerHosts hosts = new ZkHosts(props.getProperty(KAFKA_ZKCONNECTSTRING));
        SpoutConfig spoutConfig = new SpoutConfig(hosts, props.getProperty(KAFKA_TOPIC), props.getProperty(KAFKA_ZKROOT), props.getProperty(KAFKA_SPOUTID));
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.securityProtocol=props.getProperty(KAFKA_PROTOCOL);

        return new KafkaSpout(spoutConfig);
    }

}