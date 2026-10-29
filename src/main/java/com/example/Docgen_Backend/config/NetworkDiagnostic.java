package com.example.Docgen_Backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

@Configuration
public class NetworkDiagnostic {

    @Bean
    CommandLineRunner testNetwork() {
        return args -> {
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(
                                 new URL("https://api.ipify.org").openStream()))) {

                System.out.println("RENDER_OUTBOUND_IP=" + reader.readLine());

            } catch (Exception e) {
                System.out.println("OUTBOUND_IP_CHECK_FAILED=" + e);
            }

            try (Socket socket = new Socket()) {
                socket.connect(
                        new InetSocketAddress("srv948.hstgr.io", 3306),
                        10000
                );

                System.out.println("MYSQL_TCP_TEST=SUCCESS");

            } catch (Exception e) {
                System.out.println("MYSQL_TCP_TEST=FAILED");
                e.printStackTrace();
            }
        };
    }
}