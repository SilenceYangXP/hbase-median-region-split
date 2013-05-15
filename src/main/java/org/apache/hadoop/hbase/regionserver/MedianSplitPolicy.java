package org.apache.hadoop.hbase.regionserver;

import org.apache.hadoop.hbase.util.Pair;
import org.apache.log4j.Logger;

import java.util.List;

public class MedianSplitPolicy extends IncreasingToUpperBoundRegionSplitPolicy {
    private static final Logger LOG = Logger.getLogger(MedianSplitPolicy.class);

    @Override
    protected byte[] getSplitPoint() {
        LOG.info("Finding split point of region " + region.getRegionNameAsString());

        // Sample the regions for median and weight
        List<Pair<byte[], Long>> samples = MedianSplitUtil.sampleRegion(region);

        // If there are no store files, revert to default split point
        if (samples.isEmpty()) {
            LOG.info("No store files found. Reverting to default split policy.");
            return super.getSplitPoint();
        }

        // Otherwise combine samples
        return MedianSplitUtil.combineSamples(samples);
    }
}