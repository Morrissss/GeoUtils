package cn.morrissss.geobase.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

public class WilsonScoreInterval extends UDF {

	public Double evaluate(final Long m, final Long n, final String prob) {
		if (n == 0) {
			return 0.0;
		}
		double p = m * 1.0 / n;
		double z = 0;
		if (prob.equals("0.95")) {
			z = 1.96;
		} else if (prob.equals("0.9")) {
			z = 1.64;
		} else if (prob.equals("0.8")) {
			z = 1.28;
		} else if (prob.equals("0.6")) {
			z = 0.84;
		}
		double tmp = z * Math.sqrt(p * (1 - p) / n + z * z / (4.0 * n * n));
		return (p + z * z / (2 * n) - tmp) / (1 + z * z / n);
	}
}
