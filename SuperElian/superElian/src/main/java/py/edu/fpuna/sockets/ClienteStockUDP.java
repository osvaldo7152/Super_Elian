package py.edu.fpuna.sockets;

import com.google.gson.Gson;
import py.edu.fpuna.dto.MensajeClienteStock;
import py.edu.fpuna.dto.MensajeServidor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteStockUDP {
    private static final int SERVER_PORT = 9000;
    private static final String SERVER_IP = "localhost"; // Cambiar por IP si Osvamu está en otra red

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            Gson gson = new Gson();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            // 1. Preparar la consulta (Ejemplo: página 0)
            int paginaConsultar = 0;
            MensajeClienteStock consulta = new MensajeClienteStock(paginaConsultar);
            String jsonSalida = gson.toJson(consulta);
            byte[] bufferSalida = jsonSalida.getBytes();

            // 2. Enviar el datagrama
            DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, serverAddress, SERVER_PORT);
            socket.send(paqueteSalida);
            System.out.println("Consulta UDP enviada solicitando stock de la página " + paginaConsultar + "...");

            // 3. Recibir el datagrama de respuesta
            byte[] bufferEntrada = new byte[8192]; // Buffer amplio para la lista de productos
            DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
            socket.receive(paqueteEntrada);

            // 4. Procesar la respuesta JSON
            String jsonEntrada = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength());
            MensajeServidor respuesta = gson.fromJson(jsonEntrada, MensajeServidor.class);

            System.out.println("\n--- RESPUESTA DE OSVAMU MARKET ---");
            System.out.println("Estado: " + respuesta.getTipo());
            System.out.println("Mensaje: " + respuesta.getMensaje());
            
            if (respuesta.getProductos() != null && !respuesta.getProductos().isEmpty()) {
                System.out.println("Productos:");
                // Imprimimos la lista en formato JSON para visualización rápida
                System.out.println(gson.toJson(respuesta.getProductos())); 
            }

        } catch (Exception e) {
            System.err.println("Error en Cliente UDP: " + e.getMessage());
        }
    }
}