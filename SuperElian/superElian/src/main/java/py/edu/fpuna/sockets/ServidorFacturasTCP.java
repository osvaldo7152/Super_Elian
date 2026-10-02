package py.edu.fpuna.sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorFacturasTCP extends Thread {
    private static final int PORT = 8080;
    private Socket socketCliente;

    public ServidorFacturasTCP(Socket socket) {
        this.socketCliente = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
             PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true)) {

            String jsonFactura = in.readLine();
            System.out.println("Factura recibida de OsvamuMarket: " + jsonFactura);

            // Aquí iría el código para usar FacturaDAO y guardarla en PostgreSQL

            out.println("{\"estado\":\"OK\", \"mensaje\":\"Factura registrada en Super Elian\"}");

        } catch (Exception e) {
            System.err.println("Error recibiendo factura: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Super Elian esperando facturas en el puerto " + PORT + "...");
            while (true) {
                Socket socket = serverSocket.accept();
                new ServidorFacturasTCP(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}