package com.remarca;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReceiver {
    private final int port;
    private final String outputDirectory;

    public FileReceiver(int port, String outputDirectory) {
        this.port = port;
        this.outputDirectory = outputDirectory;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexões na porta " + port);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     InputStream is = socket.getInputStream()) {

                    // Receber o nome do arquivo do cliente
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String fileName = reader.readLine();

                    // Cria um caminho completo para o arquivo
                    Path outputPath = Paths.get(outputDirectory, fileName);

                    // Certifique-se de que o diretório de saída existe
                    if (!Files.exists(outputPath.getParent())) {
                        Files.createDirectories(outputPath.getParent());
                    }

                    try (FileOutputStream fos = new FileOutputStream(outputPath.toString());
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                        bos.flush();
                        System.out.println("Arquivo recebido com sucesso!");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
