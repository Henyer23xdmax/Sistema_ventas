/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controladores;

import Configuracion.conexionlocal;
import Interfaces.Igestiondatos;
import Modelos.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esaldania
 */
public class PersonaController implements Igestiondatos<Persona>{

       private Connection cnn;
       private final conexionlocal connnewadmin=new conexionlocal();
       
    @Override
    public void creacion(Persona objeto) {
       
           try {
                connnewadmin.conectar();
        String sql="INSERT INTO persona(nombre,apellidos,correo,fechanacimiento,pais,profesion,rol_id)"+"VALUES(?,?,?,?,?,?,?)";
               PreparedStatement st=connnewadmin.getConexion().prepareStatement(sql);
               st.setString(1, objeto.getNombre());
               st.setString(2, objeto.getApellidos());
               st.setString(3, objeto.getCorreo());
               java.sql.Date fechanacimiento=new java.sql.Date(objeto.getFechanacimiento().getTime());
               st.setDate(4, fechanacimiento);
               st.setString(5, objeto.getPais());
               st.setString(6, objeto.getProfesion());
               st.setInt(7, objeto.getRol_id());
               
           } catch (SQLException ex) {
               Logger.getLogger(PersonaController.class.getName()).log(Level.SEVERE, null, ex);
           }
        
    }

    @Override
    public Persona lectura(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void actualizar(Persona objeto, int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
