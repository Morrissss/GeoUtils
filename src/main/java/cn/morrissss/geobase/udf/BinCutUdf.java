package cn.morrissss.geobase.udf;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.ArrayList;
import java.util.List;

public class BinCutUdf extends UDF {

    public Integer evaluate(Double value, String binIntervals) {
        if (StringUtils.isEmpty(binIntervals)) {
            throw new IllegalArgumentException("Empty binIntervals");
        }
        if (value == null) {
            return null;
        }
        String[] binStrs = StringUtils.split(binIntervals, ',');
        List<Double> bins = new ArrayList<Double>(binStrs.length+1);
        for (String binStr : binStrs) {
            bins.add(Double.parseDouble(binStr));
        }
        bins.add(Double.POSITIVE_INFINITY);
        int result = 0;
        while (value > bins.get(result)) {
            result++;
        }
        return result;
    }
    
}
