package hiro.storm.demo;

import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class KafkaToHiveTopology {

    // Topology properties
    private static final String NUM_WORKERS = "topology.num.workers";
    private static final String SET_DEBUG = "topology.set.debug";

    // Parallelism
    private static final String KAFKASPOUT_PARALLELISM = "kafka.spout.parallelism";
    private static final String HIVEBOLT_PARALLELISM = "hive.bolt.parallelism";

    private static final Logger LOG = Logger.getLogger(KafkaToHiveTopology.class);


    public static void main(String[] args) throws Exception {

        // Main arguments
        // @param: submission instance name
        // @param: properties file path as argument

        // Load properties
        Properties props;
        try {
            props = loadProperties(args[1]);
        } catch (IOException e) {
            return;
        }

        // Initialize storm Config
        Config config = new Config();

        config.setNumWorkers(Integer.parseInt(props.getProperty(NUM_WORKERS)));
        config.setDebug(Boolean.parseBoolean(props.getProperty(SET_DEBUG)));

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("messages", KafkaSpoutGen.createKafkaSpout(props), Integer.parseInt(props.getProperty(KAFKASPOUT_PARALLELISM)));
        builder.setBolt("toHive", HiveBoltGen.createHiveBolt(props), Integer.parseInt(props.getProperty(HIVEBOLT_PARALLELISM)))
            .shuffleGrouping("messages");

        try {
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } catch (Exception e) {
            LOG.error("Error submitting topology to cluster", e);
        }
    }

    private static Properties loadProperties(String path) throws IOException {
        Properties props = new Properties();

        LOG.info("Loading properties file: " + path);
        try (FileReader reader = new FileReader(path)) {
            props.load(reader);
        } catch (IOException e) {
            LOG.error("Error encountered loading properties file: " + e);
            throw new IOException("Cannot load properties file.");
        }
        return props;
    }

}