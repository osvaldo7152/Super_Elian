package py.edu.fpuna.sockets;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import com.google.gson.Gson;

import py.edu.fpuna.dto.MensajeRastreoLogistico;
import py.edu.fpuna.dto.MensajeServidor;

public class ClienteRastreoLogisticoUDP {
    private static final int SERVER_PORT = 9002;
    private static final String SERVER_IP = "localhost";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Ingrese el codigo de confirmacion de la orden: ");
            int codigo = scanner.nextInt();

            try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            Gson gson = new Gson();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            MensajeRastreoLogistico consulta = new MensajeRastreoLogistico(codigo);
            byte[] bufferSalida = gson.toJson(consulta).getBytes();
            DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, serverAddress, SERVER_PORT);
            socket.send(paqueteSalida);

            byte[] bufferEntrada = new byte[1024];
            DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
            socket.receive(paqueteEntrada);

            String jsonEntrada = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
            MensajeServidor respuesta = gson.fromJson(jsonEntrada, MensajeServidor.class);

            System.out.println("\n--- RESPUESTA DE RASTREO LOGISTICO ---");
            System.out.println("Estado: " + respuesta.getTipo());
            System.out.println("Detalle: " + respuesta.getMensaje());

            }
        } catch (Exception e) {
            System.err.println("Error consultando rastreo: " + e.getMessage());
        }
    }
}
