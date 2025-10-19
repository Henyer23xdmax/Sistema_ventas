/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import javax.swing.JOptionPane;

/**
 *
 * @author Alumno
 */
public class Principal extends javax.swing.JFrame {

    
    public Principal() {
        initComponents();

        this.setLocationRelativeTo(null);

        // 🔹 Cambiar comportamiento de la “X”
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        // 🔹 Agregar listener para detectar el cierre de la ventana
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarVentana(); // Llamamos a un método propio
            }
        });
    }

    public void habilitarMenuAdmin() {
        // 🔹 Ejemplo: activar todo
        menu_usuarios.setVisible(true);
        
    }

    public void habilitarMenuVendedor() {
        menu_usuarios.setVisible(false);
        //menuUsuarios.setVisible(false);
        
    }

    public void habilitarMenuLimitado() {
        // 🔹 Para otros roles no definidos
        menu_usuarios.setVisible(false);
        
    }

    private void cerrarVentana() {
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Desea cerrar la sesión y volver al login?",
        "Cerrar sesión",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        this.dispose(); // 🔹 Cierra la ventana actual
        Login lg = new Login(); // 🔹 Abre el login
        lg.setVisible(true);
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        sms_clientes = new javax.swing.JMenuItem();
        menu_usuarios = new javax.swing.JMenuItem();
        menu_salir = new javax.swing.JMenu();
        sm_salir = new javax.swing.JMenuItem();

        jMenu1.setText("jMenu1");

        jMenu2.setText("jMenu2");

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu3.setText("Mantenimiento");

        sms_clientes.setText("Clientes");
        sms_clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sms_clientesActionPerformed(evt);
            }
        });
        jMenu3.add(sms_clientes);

        menu_usuarios.setText("Usuarios");
        menu_usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_usuariosActionPerformed(evt);
            }
        });
        jMenu3.add(menu_usuarios);

        jMenuBar1.add(jMenu3);

        menu_salir.setText("Salir");
        menu_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_salirActionPerformed(evt);
            }
        });

        sm_salir.setText("Salir");
        sm_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sm_salirActionPerformed(evt);
            }
        });
        menu_salir.add(sm_salir);

        jMenuBar1.add(menu_salir);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 723, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sms_clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sms_clientesActionPerformed
        Registrar_Clientes frmu = new Registrar_Clientes();
        frmu.setVisible(true);
    }//GEN-LAST:event_sms_clientesActionPerformed

    private void menu_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_salirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menu_salirActionPerformed

    private void sm_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sm_salirActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión actual?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // 🔹 Cierra la ventana actual (Principal)
            Login lg = new Login(); // 🔹 Crea una nueva instancia del login
            lg.setVisible(true); // 🔹 Muestra el formulario de login
        }
    }//GEN-LAST:event_sm_salirActionPerformed

    private void menu_usuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_usuariosActionPerformed
    Registrar_usuarios r_u = new Registrar_usuarios();
    r_u.setVisible(true);
    }//GEN-LAST:event_menu_usuariosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenu menu_salir;
    private javax.swing.JMenuItem menu_usuarios;
    private javax.swing.JMenuItem sm_salir;
    private javax.swing.JMenuItem sms_clientes;
    // End of variables declaration//GEN-END:variables
}
