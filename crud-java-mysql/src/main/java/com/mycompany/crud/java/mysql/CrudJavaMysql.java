/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.crud.java.mysql;

import Configuracion.conexionlocal;

/**
 *
 * @author esald
 */
public class CrudJavaMysql {

    public static void main(String[] args) {
        conexionlocal nconexion=new conexionlocal();
        nconexion.conectar();
        System.out.println("Conectado"+nconexion.getConexion());
        nconexion.desconectar();
        System.out.println("Testear");
        nconexion.testearconexion();
        
    }
}
