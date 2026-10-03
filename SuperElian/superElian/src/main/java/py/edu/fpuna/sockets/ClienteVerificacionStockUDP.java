package py.edu.fpuna.sockets;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import com.google.gson.Gson;

import py.edu.fpuna.dto.MensajeServidor;
import py.edu.fpuna.dto.MensajeVerificarStock;

public class ClienteVerificacionStockUDP {
    private static final int SERVER_PORT = 9001; // Nuevo puerto exclusivo para verificar stock
    private static final String SERVER_IP = "localhost";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Ingrese el ID del producto para verificar si hay stock: ");
            int idProd = scanner.nextInt();

            try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            Gson gson = new Gson();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            // 1. Enviar el ID del producto
            MensajeVerificarStock consulta = new MensajeVerificarStock(idProd);
            byte[] bufferSalida = gson.toJson(consulta).getBytes();
            DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, serverAddress, SERVER_PORT);
            socket.send(paqueteSalida);

            // 2. Esperar respuesta de si hay o no hay
            byte[] bufferEntrada = new byte[1024];
            DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
            socket.receive(paqueteEntrada);

            String jsonEntrada = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
            MensajeServidor respuesta = gson.fromJson(jsonEntrada, MensajeServidor.class);

            System.out.println("\n--- RESPUESTA DE STOCK ---");
            System.out.println("Estado: " + respuesta.getTipo());
            System.out.println("Detalle: " + respuesta.getMensaje());

            }
        } catch (Exception e) {
            System.err.println("Error verificando stock: " + e.getMessage());
        }
    }
}