# Storm topology:  kafka -> hive sample
##Introduction
A simple sample topology ingesting messages from kafka and streaming into a hive table using HiveBolt, for Hivebolt/streaming demonstration purposes.

This sample topology is based on a company user registrar.  In this hypothetical scenario, entries are of the following format:
```
<id INT> <name STRING> <role STRING> <year STRING>
```
where id is a user-defined ID, name is the user name, role is the user's role in the company, and year represents year marking start of employment.

To conform to the format above, it is expected that kafka send messages in this format.  E.g.  1000 HIRO ENGINEER 2017

##Getting Started
- **Hive to-do**

  Make sure ACID transactions are turned on for Hive.  Specifically, this pertains to the following settings:
  ```
  hive.support.concurrency -> true
  hive.txn.manager -> org.apache.hadoop.hive.ql.lockmgr.DbTxnManager
  hive.exec.dynamic.partition.mode -> nonstrict
  hive.enforce.bucketing -> true
  hive.compactor.worker.threads -> <not-zero>
  hive.compactor.initiator.on -> true
  ```
  **NOTE**:  If you have Ambari, you can just enable the 'Acid Transactions' switch :)
  
  Once we are sure ACID transactions are enabled for Hive, access Hive (i.e. via beeline -> !connect jdbc:hive2://...) and create a database for your use:
  ```
  create database <db_name>;
  ```
  Next, create a table as follows:
  ```
  use <db_name>;
  CREATE TABLE <table_name> ( id INT, name STRING, role STRING) PARTITIONED BY (year STRING) CLUSTERED BY (role) into 5 buckets STORED AS ORC TBLPROPERTIES ("orc.compress"="NONE","transactional"="true");
  ```
- **Kafka to-do**

  Create your topic for kafka:
  ```
  ./kafka-topics.sh --create --topic <topic_name> --partitions 3 --replication-factor 3 --zookeeper test1:2181
  ```
  **NOTE**:  If your kafka is kerberized, make sure to add authorization.  For example, if using the buit-in SimpleACL class:
  ```
  /usr/hdp/current/kafka-broker/bin/kafka-acls.sh --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=<zk_string> --add --allow-principal User:storm-principal@REALM --operation All --topic <topic_name>
  ```
  
### Installing
In your directory of choice, run git clone:
```
git clone https://github.com/kuwabarasan/storm-topology_kafka-to-hive-sample
```
If maven is not installed, please install maven.  Afterwards, add maven bin to $PATH.  Once you have maven, run the following in the root directory of the cloned directory:
```
mvn clean package
```
The above will produce a uber jar called 'kafkatohive-0.0.1-SNAPSHOT.jar' in a new 'target' directory.  Copy this over to the root directory, so it is in the same path as 'deploy.sh' and 'topology.properties':
```
cp target/kafkatohive-0.0.1-SNAPSHOT.jar ..
```
Make sure to fill in all property values in topology.properties.

Also, make sure to insert hive-site.xml, hdfs-site.xml, and core-site.xml from your Hive cluster.
```
E.g.
jar uvf kafkatohive-0.0.1-SNAPSHOT.jar -C /etc/hive/conf hive-site.xml
jar uvf kafkatohive-0.0.1-SNAPSHOT.jar -C /etc/hadoop/conf core-site.xml
jar uvf kafkatohive-0.0.1-SNAPSHOT.jar -C /etc/hadoop/conf hdfs-site.xml
```

### Deploying
Deploying is easy.  Simply run the deploy script:
```
./deploy.sh <instance_name>
```
The topology will be submitted as specified instance_name.

You can test out the topology by running a producer (built-in console producer will suffice) and sending messages in the format discussed above:  
```
<id INT> <name STRING> <role STRING> <year STRING>
```
