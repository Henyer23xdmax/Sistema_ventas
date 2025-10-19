/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Configuracion;

import com.mysql.cj.conf.PropertyKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esaldania
 */
public class miconexion {
    private Connection cnn;
    private String cadenaconexion,usuariodb,clavedb;

    public miconexion(String cadenaconexion, String usuariodb, String clavedb) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.cadenaconexion = cadenaconexion;
            this.usuariodb = usuariodb;
            this.clavedb = clavedb;
            cnn=DriverManager.getConnection(this.cadenaconexion, this.usuariodb, this.clavedb);
            System.out.println("Estas conectado");
        } catch (ClassNotFoundException | SQLException ex) {
        
        }
        
        
        
    }
    public boolean testearConexion(){
        try {
            return !cnn.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null,ex);
            return false;
        }
    }
    
    public void abrirconexion(){
        try {
            cnn=DriverManager.getConnection(cadenaconexion, "root","");
            cnn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null,ex);
                System.out.println("Conexión abierta");
                
        }
        
    }
    public void cerrarconexion(){
        try {
            cnn.close();
            System.out.println("Desconectado");
        } catch (Exception e) {
        }
    }
}
