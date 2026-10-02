package py.edu.fpuna.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

import py.edu.fpuna.dao.FacturaDAO;
import py.edu.fpuna.dto.MensajeFactura;
import py.edu.fpuna.dto.MensajeServidor;
import py.edu.fpuna.enums.TipoDeMensaje;

public class ServidorFacturasTCP extends Thread {

    // Puerto en el que Super Elian esperará las facturas. 
    // Asegúrate de que no sea el 8000, para que no choque con OsvamuMarket. Usaremos el 8001.
    private static final int PORT = 8001; 
    
    private Socket socketCliente;
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final Gson gson = new Gson();

    public ServidorFacturasTCP(Socket socket) {
        this.socketCliente = socket;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true)
        ) {
            System.out.println("Conexión TCP establecida con OsvamuMarket para recepción de factura.");
            
            // 1. Leer el JSON enviado por OsvamuMarket
            String jsonIn = in.readLine();
            
            if (jsonIn != null) {
                // 2. Convertir el JSON al objeto MensajeFactura
                MensajeFactura mensajeFactura = gson.fromJson(jsonIn, MensajeFactura.class);
                
                // 3. Registrar en el sistema contable (Base de datos local)
                boolean exito = facturaDAO.registrarFactura(mensajeFactura.getFactura());
                
                // 4. Armar la respuesta para OsvamuMarket
                MensajeServidor respuesta;
                if (exito) {
                    respuesta = new MensajeServidor(TipoDeMensaje.OK, "Factura procesada y registrada en el sistema contable de Super Elian exitosamente.", null);
                } else {
                    respuesta = new MensajeServidor(TipoDeMensaje.ERROR, "Ocurrió un error al intentar registrar la factura en Super Elian.", null);
                }
                
                // 5. Enviar la respuesta de confirmación de vuelta
                out.println(gson.toJson(respuesta));
            }
            
        } catch (Exception e) {
            System.err.println("Error en la comunicación TCP de la factura: " + e.getMessage());
        } finally {
            try {
                System.out.println("Fin de la conexión TCP para la factura.");
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Facturas (Super Elian) iniciado, escuchando en el puerto " + PORT + "...");

            while (true) {
                // Se queda bloqueado esperando que OsvamuMarket se conecte y envíe una factura
                Socket socketProveedor = serverSocket.accept(); 
                new ServidorFacturasTCP(socketProveedor).start();
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor de facturas: " + e.getMessage());
        }
    }
}