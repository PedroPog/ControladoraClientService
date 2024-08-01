package com.remarca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PingClient {
    public static void ping(){
        String serverAddress = "192.168.10.16";
        int serverPort = 10060;

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String ipToCheck = "192.168.1.1";
            int portToCheck = 8080;

            out.println(ipToCheck);
            out.println(portToCheck);

            String response = in.readLine();
            System.out.println("Resposta do servidor: " + response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
