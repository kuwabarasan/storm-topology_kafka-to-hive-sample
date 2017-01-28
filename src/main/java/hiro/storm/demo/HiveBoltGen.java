package hiro.storm.demo;


import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.storm.hive.bolt.HiveBolt;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.tuple.Fields;

import java.util.Properties;

public class HiveBoltGen {

    private static final String HIVE_METASTOREURI = "hive.metastore.uri";
    private static final String HIVE_DB_NAME = "hive.db.name";
    private static final String HIVE_TABLE_NAME = "hive.table.name";
    private static final String HIVE_TICK_INTERVAL = "hive.tick.interval";

    private static final String HIVE_PRINCIPAL = "hive.kerberos.principal";
    private static final String HIVE_KEYTAB = "hive.principal.keytab";

    private static final Logger LOG = Logger.getLogger(HiveBoltGen.class);

    public static HiveBolt createHiveBolt(Properties props) {

        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
                .withColumnFields(new Fields("id", "name", "role"))
                .withPartitionFields(new Fields("year"));

        HiveOptions hiveOptions = new HiveOptions(props.getProperty(HIVE_METASTOREURI), props.getProperty(HIVE_DB_NAME),
                props.getProperty(HIVE_TABLE_NAME), mapper)
                .withTickTupleInterval(Integer.parseInt(props.getProperty(HIVE_TICK_INTERVAL)));

        if (isSecure())
            hiveOptions.withKerberosPrincipal(props.getProperty(HIVE_PRINCIPAL))
                    .withKerberosKeytab(HIVE_KEYTAB);

        return new HiveBolt(hiveOptions);
    }

    public static boolean isSecure() {
        Configuration configuration = new Configuration();
        configuration.addResource("hive-site.xml");
        if (configuration.get("hive.metastore.sasl.enabled").equalsIgnoreCase("true"))
            return true;
        else return false;
    }
}
