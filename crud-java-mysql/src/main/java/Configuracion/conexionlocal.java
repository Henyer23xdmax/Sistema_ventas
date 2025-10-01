/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Configuracion;

import Interfaces.Igestorconexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author esaldania
 */
public class conexionlocal implements Igestorconexion{
    private String url="jdbc:mysql://localhost:3306/crudjavabd1";
    private String usuario="root";
    private String clave="";
    
    //Instancia de la clase conexion del sistema
    private Connection conexion;

    public conexionlocal() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }
    
    
    
    @Override
    public void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion=DriverManager.getConnection(this.url, this.usuario, this.clave);
            System.out.println("Conectado a la base de datos local");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    @Override
    public void desconectar() {
        conectar();
        try {
           conexion.close();
            System.out.println("Desconectado de la base de datos");
        } catch (SQLException ex) {
             Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    @Override
    public boolean testearconexion() {
        try {
            if(conexion!=null && !conexion.isClosed()){
                System.out.println("Conexión abierta");
                return true;
            }else{
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null,ex);
            return false;
        }
    }
    
}
