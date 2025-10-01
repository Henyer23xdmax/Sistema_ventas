/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

/**
 *
 * @author esald
 * @param <T>
 */
public interface Igestiondatos<T> {
    void creacion(T objeto);
    T lectura(int id);
    void actualizar(T objeto,int id);
    void eliminar(int id);
}
