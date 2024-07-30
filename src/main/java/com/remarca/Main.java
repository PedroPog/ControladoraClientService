package com.remarca;


import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Main {
    /*public static void main(String[] args) {
        FileReceiver fileReceiver = new FileReceiver(6000, "/home/desenvolvimento/pdvremarca/rx"); // Ajuste o caminho do diretório conforme necessário
        fileReceiver.startServer();
    }*/

    public static void main(String[] args) {
        String subnet = "192.168.10"; // Substitua pelo seu subnet
        int port = 6001; // Porta do seu servidor JAR
        List<String> activeIPs = NetworkScanner.scanNetwork(subnet);

        System.out.println("Verificando se o JAR está em execução:");
        for (String ip : activeIPs) {
            if (isJarRunning(ip, port)) {
                System.out.println("JAR rodando no IP: " + ip);
            } else {
                System.out.println("JAR não encontrado no IP: " + ip);
            }
        }

        FileReceiver fileReceiver = new FileReceiver(6000, "/home/desenvolvimento/pdvremarca/rx"); // Ajuste o caminho do diretório conforme necessário
        fileReceiver.startServer();
    }
    public static boolean isJarRunning(String ip, int port) {
        try (Socket socket = new Socket(ip, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}