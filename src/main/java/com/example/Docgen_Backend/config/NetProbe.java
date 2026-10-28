package com.example.Docgen_Backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

final class NetProbe {

    private static final Logger log = LoggerFactory.getLogger(NetProbe.class);

    private NetProbe() {}

    static void probe(String jdbcUrl) {
        try {
            String hostPort = jdbcUrl.replaceFirst("^jdbc:mysql://", "").split("[/?]")[0];
            String host = hostPort.split(":")[0];
            int port = hostPort.contains(":") ? Integer.parseInt(hostPort.split(":")[1]) : 3306;

            log.info("PROBE configured host={} port={}", host, port);
            for (InetAddress a : InetAddress.getAllByName(host)) {
                log.info("PROBE {} resolves to {}", host, a.getHostAddress());
            }
            tcp(host, port);
            tcp("82.25.121.177", 3306);   // Hostinger's IP from hPanel
        } catch (Exception e) {
            log.warn("PROBE error: {}", e.toString());
        }
    }

    private static void tcp(String host, int port) {
        long start = System.currentTimeMillis();
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 8000);
            log.info("PROBE TCP {}:{} OK in {} ms", host, port, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("PROBE TCP {}:{} FAILED after {} ms: {}", host, port,
                    System.currentTimeMillis() - start, e.toString());
        }
    }
}