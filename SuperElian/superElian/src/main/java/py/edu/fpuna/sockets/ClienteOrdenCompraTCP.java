package py.edu.fpuna.sockets;

import com.google.gson.Gson;
import py.edu.fpuna.dto.MensajeClienteOrdenDeCompra;
import py.edu.fpuna.dto.MensajeServidor;
import py.edu.fpuna.enums.Opciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteOrdenCompraTCP {
    private static final int SERVER_PORT = 8000;
    private static final String SERVER_IP = "localhost";

    public static void main(String[] args) {
        Gson gson = new Gson();

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conexión TCP establecida con OsvamuMarket (Puerto 8000).");

            // OsvamuMarket envía inmediatamente la primera página de productos al conectar
            String jsonInicial = in.readLine();
            MensajeServidor msjInicial = gson.fromJson(jsonInicial, MensajeServidor.class);
            System.out.println("\n--- CATÁLOGO INICIAL ---");
            imprimirRespuesta(msjInicial, gson);

            boolean conectado = true;
            while (conectado) {
                System.out.println("\n--- MENÚ DE ORDEN DE COMPRA ---");
                System.out.println("1. Siguiente página de catálogo");
                System.out.println("2. Anterior página de catálogo");
                System.out.println("3. Agregar producto al carrito");
                System.out.println("4. Eliminar producto del carrito");
                System.out.println("5. Ver estado del carrito");
                System.out.println("6. Vaciar carrito");
                System.out.println("7. CONFIRMAR COMPRA");
                System.out.println("8. CANCELAR Y SALIR");
                System.out.print("Elige una opción: ");
                
                int eleccion = scanner.nextInt();
                Opciones opcionSeleccionada = null;
                int idProducto = 0;
                int cantidad = 0;

                switch (eleccion) {
                    case 1: opcionSeleccionada = Opciones.SIGUIENTE; break;
                    case 2: opcionSeleccionada = Opciones.ANTERIOR; break;
                    case 3:
                        opcionSeleccionada = Opciones.AGREGAR;
                        System.out.print("Ingrese ID del producto: ");
                        idProducto = scanner.nextInt();
                        System.out.print("Ingrese cantidad de cajas: ");
                        cantidad = scanner.nextInt();
                        break;
                    case 4:
                        opcionSeleccionada = Opciones.ELIMINAR;
                        System.out.print("Ingrese ID del producto a remover: ");
                        idProducto = scanner.nextInt();
                        break;
                    case 5: opcionSeleccionada = Opciones.CARRITO; break;
                    case 6: opcionSeleccionada = Opciones.BORRAR_TODO; break;
                    case 7: opcionSeleccionada = Opciones.COMPRAR; break;
                    case 8: opcionSeleccionada = Opciones.CANCELAR; break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                        continue;
                }

                // Empaquetar la petición en el DTO
                MensajeClienteOrdenDeCompra peticion = new MensajeClienteOrdenDeCompra(opcionSeleccionada, idProducto, cantidad);
                String jsonSalida = gson.toJson(peticion);
                
                // Enviar al servidor
                out.println(jsonSalida);

                // Esperar la respuesta del servidor
                String jsonEntrada = in.readLine();
                if (jsonEntrada == null) {
                    System.out.println("El servidor cerró la conexión.");
                    break;
                }

                MensajeServidor respuesta = gson.fromJson(jsonEntrada, MensajeServidor.class);
                imprimirRespuesta(respuesta, gson);

                // Según el código de OsvamuMarket, si COMPRAR o CANCELAR, el servidor cierra el socket.
                if (opcionSeleccionada == Opciones.COMPRAR || opcionSeleccionada == Opciones.CANCELAR) {
                    conectado = false;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en Cliente TCP: " + e.getMessage());
        }
    }

    private static void imprimirRespuesta(MensajeServidor respuesta, Gson gson) {
        System.out.println("\n[OsvamuMarket]: " + respuesta.getTipo() + " -> " + respuesta.getMensaje());
        if (respuesta.getProductos() != null && !respuesta.getProductos().isEmpty()) {
            System.out.println(gson.toJson(respuesta.getProductos()));
        }
    }
}