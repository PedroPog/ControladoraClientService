package com.remarca;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetworkScanner {
    public static List<String> scanNetwork(String subnet) {
        List<String> activeIPs = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            executor.submit(() -> {
                try {
                    InetAddress inet = InetAddress.getByName(host);
                    if (inet.isReachable(1000)) {
                        synchronized (activeIPs) {
                            activeIPs.add(host);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return activeIPs;
    }


}
