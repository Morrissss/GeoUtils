package cn.morrissss.geobase.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.commons.math3.special.Gamma.digamma;

public class BayesCtrSmoother {

    private static volatile List<Integer> clickNums;
    private static volatile List<Integer> displayNums;

    private static volatile float a;
    private static volatile float b;

    private static volatile List<Float> aNumerator;
    private static volatile List<Float> bNumerator;
    private static volatile List<Float> denominator;

    private static class DigammaCalculator implements Runnable {
        DigammaCalculator(CountDownLatch latch, int beg, int end) {
            this.latch = latch;
            this.beg = beg;
            this.end = end;
        }
        private CountDownLatch latch;
        private int beg;
        private int end;    // excluded
        @Override
        public void run() {
            double digammaA = digamma(a);
            double digammaB = digamma(b);
            double digammaAB = digamma(a+b);
            for (int i = beg; i < end; i++) {
                aNumerator.set(i, (float) (digamma(clickNums.get(i)+a) - digammaA));
                bNumerator.set(i, (float) (digamma(displayNums.get(i)-clickNums.get(i)+b) - digammaB));
                denominator.set(i, (float) (digamma(displayNums.get(i)+a+b) - digammaAB));
            }
            latch.countDown();
        }
    }

    private static boolean update() {
        float sumANumerator = 0.0f;
        for (float f : aNumerator) {
            sumANumerator += f;
        }
        float sumBNumerator = 0.0f;
        for (float f : bNumerator) {
            sumBNumerator += f;
        }
        float sumDenominator = 0.0f;
        for (float f : denominator) {
            sumDenominator += f;
        }

        float nextA = a * sumANumerator / sumDenominator;
        float nextB = b * sumBNumerator / sumDenominator;
        boolean stop = false;
        if (Math.abs(nextA - a) < 1e-4 && Math.abs(nextB - b) < 1e-4) {
            stop = true;
        }
        a = nextA;
        b = nextB;
        return stop;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        int clickNumIdx = Integer.parseInt(args[2]);
        int displayNumIdx = Integer.parseInt(args[3]);
        int threadNum = Integer.parseInt(args[4]);
        a = Float.parseFloat(args[5]);
        b = Float.parseFloat(args[6]);

        clickNums = new ArrayList<Integer>(1<<21);
        displayNums = new ArrayList<Integer>(1<<21);
        aNumerator = new ArrayList<Float>(1<<21);
        bNumerator = new ArrayList<Float>(1<<21);
        denominator = new ArrayList<Float>(1<<21);

        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            clickNums.add(Integer.parseInt(parts[clickNumIdx]));
            displayNums.add(Integer.parseInt(parts[displayNumIdx]));
            aNumerator.add(0.0f);
            bNumerator.add(0.0f);
            denominator.add(0.0f);
        }
        br.close();

        for (int n = 0; n < 2000; n++) {
            CountDownLatch latch = new CountDownLatch(threadNum);
            ExecutorService exec = Executors.newFixedThreadPool(threadNum);
            int batchSize = clickNums.size() / threadNum + 1;
            for (int i = 0; i < threadNum; i++) {
                exec.execute(new DigammaCalculator(latch, i * batchSize,
                                                   Math.min((i + 1) * batchSize,
                                                            clickNums.size())));
            }
            latch.await();
            exec.shutdown();
            if (update()) {
                break;
            }
            if (n % 50 == 0) {
                System.out.println("a=" + a + ", b=" + b);
            }
        }
        System.out.println("a=" + a + ", b=" + b);

        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
        bw.write("smoothed_m\tsmoothed_n\tsmoothed_ctr\n");
        for (int i = 0; i < clickNums.size(); i++) {
            float smoothedClick = clickNums.get(i)+a;
            float smoothedDisplay = displayNums.get(i)+b;
            bw.write(String.format("%.4f\t%.4f\t%.6f\n",
                                   smoothedClick, smoothedDisplay, smoothedClick / smoothedDisplay));
        }
        bw.close();
    }
}
