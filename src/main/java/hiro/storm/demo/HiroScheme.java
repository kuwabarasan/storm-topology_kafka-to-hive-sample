package hiro.storm.demo;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class HiroScheme implements Scheme {
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private static final Logger LOG = Logger.getLogger(HiroScheme.class);

    // id INT, name STRING, role STRING, year STRING
    List<String> colFields = Arrays.asList("id", "name", "role", "year");

    public List<Object> deserialize(ByteBuffer bytes) {

        List<String> deserializedAndSplit = new ArrayList<String>();

        try {
            String deserialized = deserializeString(bytes);
            deserializedAndSplit = Arrays.asList(deserialized.split(" "));
        } catch (Exception e) {
            LOG.error("Failed to deserialize message due to" + e, e);
        }
        return new Values(deserializedAndSplit.get(0), deserializedAndSplit.get(1), deserializedAndSplit.get(2), deserializedAndSplit.get(3));
    }

    public static String deserializeString(ByteBuffer string) {
        if (string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    public Fields getOutputFields() {
        return new Fields(colFields);
    }
}