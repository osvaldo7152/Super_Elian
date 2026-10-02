package py.edu.fpuna.dao;

import py.edu.fpuna.entities.Factura;

public class FacturaDAO {

    public boolean registrarFactura(Factura factura) {
        try {
            // TODO: Aquí iría tu código JDBC (INSERT INTO facturas ...) para guardar en PostgreSQL
            
            System.out.println(">> [Sistema Contable Local] Registrando factura en base de datos...");
            System.out.println("   ID Compra: " + factura.getIdCompra());
            System.out.println("   Monto Total: $" + factura.getMontoTotal());
            System.out.println("   Fecha: " + factura.getFecha());
            
            return true; // Simula que se guardó con éxito en la BD de superElian
        } catch (Exception e) {
            System.err.println("Error al guardar la factura en la BD local: " + e.getMessage());
            return false;
        }
    }
}