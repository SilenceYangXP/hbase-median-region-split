# HBase Median Region Split

An HBase [split policy](https://hbase.apache.org/book/regions.arch.html) which is designed to split a region at the median rowkey, so that each resulting child region is as close to the same size on disk as possible.  The policy works on HBase 0.94+, and can be defined table-by-table or globally.  If running on HBase 0.92, a coprocessor is provided which will hijack any split requests and substitute in the median key.  If running with the coprocessor, all tables will use the median split key.

## Installation

Checkout and build the project:

```bash	
git clone https://github.com/nearinfinity/hbase-median-region-split.git
cd hbase-median-region-split
mvn package
```

include the resulting jar, `target/median-region-split-VERSION.jar`, on the classpath of all regionservers.

## Enable median region split policy

You can enable the new split policy on a table-by-table basis, at table creation time:


```Java
HTableDescriptor myHtd = ...;
myHtd.setValue(HTableDescriptor.SPLIT_POLICY, MedianSplitPolicy.class.getName());
```

or, you can configure HBase to use it as the default by adding the following to `hbase-site.xml` on all region servers:

```XML
<property>
  <name>hbase.regionserver.region.split.policy</name>
  <value>org.apache.hadoop.hbase.regionserver.MedianSplitPolicy</value>
</property>
```

## Enable median region split observer coprocessor


Add the following to `hbase-site.xml` on all region servers:

```XML
<property>
  <name>hbase.coprocessor.region.classes</name>
  <value>org.apache.hadoop.hbase.regionserver.MedianSplitObserver</value>
</property>
```

## FAQ

#### Splitting? What are you talking about?

See [this](http://hortonworks.com/blog/apache-hbase-region-splitting-and-merging/) excellent Horton Works blog post on the specifics of HBase region splitting.

#### Does Median Region Split change *when* regions are split?

Short answer: No.  When using the coprocessor the answer is always no.  When using the policy, the regions will be split according to when the [IncreasingToUpperBoundSplitPolicy](https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/regionserver/IncreasingToUpperBoundRegionSplitPolicy.html) would have split the region.  The `IncreasingToUpperBoundSplitPolicy` is the default split policy for HBase 0.94+, so, unless you were running a split policy other than the default, the answer is still no.

#### Does Median Region Split change *where* regions are split?

Yes.  Median Region Split attempts to split regions in half (in terms of the resulting child-regions' size in bytes) as closely as possible.

#### Do I need both the coprocessor and the policy?

No.  Use one or the other, they provide the same functionality.  I recommend running the policy on HBase 0.94+, and the observer on HBase 0.92.

## License

HBase Median Region Split is released under the [Apache License](https://www.apache.org/licenses/LICENSE-2.0.html).