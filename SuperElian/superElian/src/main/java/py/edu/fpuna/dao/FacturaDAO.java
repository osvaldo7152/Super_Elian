package py.edu.fpuna.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import py.edu.fpuna.entities.Factura;

public class FacturaDAO {
    // Cambia "postgres" y "admin" por tu usuario y contraseña real de PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/super_elian_db";
    private static final String USER = "admin"; 
    private static final String PASSWORD = "123";

    public boolean registrarFactura(Factura factura) {
        String sql = "INSERT INTO facturas (id_compra, monto_total, fecha) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, factura.getIdCompra());
            pstmt.setInt(2, factura.getMontoTotal());
            pstmt.setString(3, factura.getFecha());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println(">> [DB Super Elian] Factura de la compra #" + factura.getIdCompra() + " guardada en PostgreSQL exitosamente.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar en PostgreSQL (Super Elian): " + e.getMessage());
        }
        return false;
    }
}