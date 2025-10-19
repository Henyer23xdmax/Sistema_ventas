package Vistas;

import Configuracion.conexionlocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Registrar_usuarios extends javax.swing.JFrame {
    
    public enum EstadoApp {
        INICIAL,
        EDITANDO,
        SELECCIONADO
    }

     private EstadoApp estadoActual = EstadoApp.INICIAL;
     private int idUsuarioSeleccionado = -1;
     private conexionlocal cn = new conexionlocal();

     
    public Registrar_usuarios() {
        initComponents();
        cargarUsuarios();
        cargarTiposDeRol();
        this.setLocationRelativeTo(null);
        actualizarEstado(EstadoApp.INICIAL);
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarVentana();
            }
        });
        
        tabla_usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tabla_usuarios.getSelectedRow();
                if (fila >= 0) {
                    cargarDatosEnFormulario(fila); // 👈 Aquí cargas los datos al formulario
                    actualizarEstado(EstadoApp.SELECCIONADO); // 👈 Luego cambias el estado
                }
            }
        });


        
        
    }

    private void cerrarVentana() {
        this.dispose();

    }
    
    private void cargarDatosEnFormulario(int fila) {
        idUsuarioSeleccionado = Integer.parseInt(tabla_usuarios.getValueAt(fila, 0).toString());
        txt_nombre.setText(tabla_usuarios.getValueAt(fila, 1).toString());
        txt_apellido.setText(tabla_usuarios.getValueAt(fila, 2).toString());
        txt_nombre_usuario.setText(tabla_usuarios.getValueAt(fila, 3).toString());
        txt_correo.setText(tabla_usuarios.getValueAt(fila, 4).toString());

        // Seleccionar el rol en el combo
        String rolSeleccionado = tabla_usuarios.getValueAt(fila, 5).toString();
        combo_rol.setSelectedItem(rolSeleccionado);

        // Limpiar el campo de contraseña (por seguridad no se muestra)
        txt_password.setText("");
    }

    private void actualizarEstado(EstadoApp nuevoEstado) {
        this.estadoActual = nuevoEstado;

        switch (estadoActual) {
            case INICIAL:
                limpiarCampos();
                bloquearCampos();
                btn_nuevo.setEnabled(true);
                btn_guardar.setEnabled(false);
                btn_cancelar.setEnabled(false);
                btn_cambiar_estado.setEnabled(false);
                btn_salir.setEnabled(true);
                break;

            case EDITANDO:
                desbloquearCampos();
                btn_nuevo.setEnabled(false);
                btn_guardar.setEnabled(true);
                btn_cancelar.setEnabled(true);
                btn_cambiar_estado.setEnabled(false);
                btn_salir.setEnabled(true);
                break;

            case SELECCIONADO:
                desbloquearCampos();
                btn_nuevo.setEnabled(false);
                btn_guardar.setEnabled(true);
                btn_cancelar.setEnabled(true);
                btn_cambiar_estado.setEnabled(true);
                btn_salir.setEnabled(true);
                break;
        }
    }

    
    private void cargarTiposDeRol() {
        conexionlocal con = new conexionlocal();
        con.conectar();

        Connection conn = con.getConexion();

        if (conn != null) {
            try {
                String sql = "SELECT nombre FROM rol";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    combo_rol.addItem(rs.getString("nombre"));
                }

                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar tipos de rol: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
        }
    }

    private void cargarUsuarios() {
        conexionlocal conLocal = new conexionlocal();
        conLocal.conectar();
        Connection con = conLocal.getConexion();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Apellido");
        model.addColumn("Usuario");
        model.addColumn("Correo");
        model.addColumn("Rol");
        model.addColumn("Estado");

        try {
            String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.nombre_usuario, u.correo, r.nombre AS rol, u.estado "
                    + "FROM usuario u "
                    + "INNER JOIN rol r ON u.id_rol = r.id_rol";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("nombre_usuario"),
                    rs.getString("correo"),
                    rs.getString("rol"),
                    rs.getString("estado")
                });
            }

            tabla_usuarios.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        } finally {
            conLocal.desconectar(); // Cierra la conexión al final
        }
    }

    private void limpiarCampos() {
        txt_nombre.setText("");
        txt_apellido.setText("");
        txt_nombre_usuario.setText("");
        txt_password.setText("");
        txt_correo.setText("");
        combo_rol.setSelectedIndex(0);
    }
    
    private void bloquearCampos() {
        txt_nombre.setEnabled(false);
        txt_apellido.setEnabled(false);
        txt_nombre_usuario.setEnabled(false);
        txt_password.setEnabled(false);
        txt_correo.setEnabled(false);
        combo_rol.setEnabled(false);
        btn_guardar.setEnabled(false);
        btn_cancelar.setEnabled(false);
    }
    
    private void desbloquearCampos() {
        txt_nombre.setEnabled(true);
        txt_apellido.setEnabled(true);
        txt_nombre_usuario.setEnabled(true);
        txt_password.setEnabled(true);
        txt_correo.setEnabled(true);
        combo_rol.setEnabled(true);
        btn_guardar.setEnabled(true);
        btn_cancelar.setEnabled(true);
    }
    
    private boolean correoExiste(String correo, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ? AND id_usuario != ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, correo);
        ps.setInt(2, idUsuarioSeleccionado == -1 ? -1 : idUsuarioSeleccionado);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        ps.close();
        return count > 0;
    }
    
    private boolean usuarioExiste(String nombreUsuario, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ? AND id_usuario != ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, nombreUsuario);
        ps.setInt(2, idUsuarioSeleccionado == -1 ? -1 : idUsuarioSeleccionado);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        ps.close();
        return count > 0;
    }
    
    private boolean validarRol() {
        if (combo_rol.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
            return false;
        }
        return true;
    }
    
    private boolean validarCorreo(String correo) {
        // No vacío
        if (correo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no puede estar vacío.");
            return false;
        }

        // Longitud máxima
        if (correo.trim().length() > 100) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no puede exceder 100 caracteres.");
            return false;
        }

        // Formato válido
        if (!correo.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no tiene un formato válido.");
            return false;
        }

        return true;
    }

    private boolean validarPassword(String password) {
        // No vacía
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contraseña no puede estar vacía.");
            return false;
        }

        // Longitud mínima
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 8 caracteres.");
            return false;
        }

        // Al menos una mayúscula
        if (!password.matches(".*[A-Z].*")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos una letra mayúscula.");
            return false;
        }

        // Al menos una minúscula
        if (!password.matches(".*[a-z].*")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos una letra minúscula.");
            return false;
        }

        // Al menos un número
        if (!password.matches(".*[0-9].*")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos un número.");
            return false;
        }

        // Opcional: al menos un carácter especial
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe contener al menos un carácter especial (!@#$%^&*...).");
            return false;
        }

        return true;
    }
    
       private boolean validarNombreUsuario(String usuario) {
        // No vacío
        if (usuario.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.");
            return false;
        }  

        // Longitud mínima
        if (usuario.trim().length() < 4) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario debe tener al menos 4 caracteres.");
            return false;
        }

        // Longitud máxima
        if (usuario.trim().length() > 50) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede exceder 50 caracteres.");
            return false;
        }

        // Solo letras, números y guión bajo, sin espacios
        if (!usuario.matches("^[a-zA-Z0-9_]+$")) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario solo puede contener letras, números y guión bajo (_).");
            return false;
        }

        return true;
    }
       
    private boolean validarNombreApellido(String texto, String campo) {
        // No vacío
        if (texto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo " + campo + " no puede estar vacío.");
            return false;
        }

        // Longitud mínima
        if (texto.trim().length() < 2) {
            JOptionPane.showMessageDialog(this, "El campo " + campo + " debe tener al menos 2 caracteres.");
            return false;
        }

        // Longitud máxima
        if (texto.trim().length() > 50) {
            JOptionPane.showMessageDialog(this, "El campo " + campo + " no puede exceder 50 caracteres.");
            return false;
        }

        // Solo letras, espacios y tildes
        if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(this, "El campo " + campo + " solo debe contener letras y espacios.");
            return false;
        }

        return true;
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_nombre = new javax.swing.JTextField();
        txt_nombre_usuario = new javax.swing.JTextField();
        txt_apellido = new javax.swing.JTextField();
        txt_password = new javax.swing.JPasswordField();
        txt_correo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_usuarios = new javax.swing.JTable();
        btn_nuevo = new javax.swing.JButton();
        btn_guardar = new javax.swing.JButton();
        btn_cancelar = new javax.swing.JButton();
        btn_cambiar_estado = new javax.swing.JButton();
        btn_salir = new javax.swing.JButton();
        combo_rol = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("NewsGoth BT", 3, 18)); // NOI18N
        jLabel1.setText("USUARIOS");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel2.setText("NOMBRES:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel3.setText("APELLIDOS:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel4.setText("NOMBRE DE USUARIO:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel5.setText("CONTRASEÑA:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel6.setText("ROL:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel7.setText("CORREO:");

        txt_nombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_nombre_usuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_apellido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_password.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_correo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        tabla_usuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabla_usuarios);

        btn_nuevo.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btn_nuevo.setText("NUEVO");
        btn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevoActionPerformed(evt);
            }
        });

        btn_guardar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btn_guardar.setText("GUARDAR");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        btn_cancelar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btn_cancelar.setText("CANCELAR");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });

        btn_cambiar_estado.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btn_cambiar_estado.setText("CAMBIAR ESTADO");
        btn_cambiar_estado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cambiar_estadoActionPerformed(evt);
            }
        });

        btn_salir.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btn_salir.setText("SALIR");
        btn_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salirActionPerformed(evt);
            }
        });

        combo_rol.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combo_rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione..." }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(356, 356, 356)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(txt_nombre_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(92, 92, 92)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(117, 117, 117)
                                            .addComponent(txt_correo))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(65, 65, 65))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(142, 142, 142)))
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(combo_rol, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btn_nuevo)
                                .addGap(34, 34, 34)
                                .addComponent(btn_guardar)
                                .addGap(54, 54, 54)
                                .addComponent(btn_cancelar)
                                .addGap(37, 37, 37)
                                .addComponent(btn_cambiar_estado)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(btn_salir)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1)
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txt_nombre_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(combo_rol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txt_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_nuevo)
                    .addComponent(btn_guardar)
                    .addComponent(btn_cancelar)
                    .addComponent(btn_cambiar_estado)
                    .addComponent(btn_salir))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevoActionPerformed
        limpiarCampos();
        desbloquearCampos();
        actualizarEstado(EstadoApp.EDITANDO);
        txt_nombre_usuario.requestFocus();
    }//GEN-LAST:event_btn_nuevoActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        limpiarCampos();
        bloquearCampos();
        actualizarEstado(EstadoApp.INICIAL);
    }//GEN-LAST:event_btn_cancelarActionPerformed

    private void btn_cambiar_estadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cambiar_estadoActionPerformed
        int fila = tabla_usuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para cambiar estado");
            return;
        }

        int id = Integer.parseInt(tabla_usuarios.getValueAt(fila, 0).toString());
        String estadoActual = tabla_usuarios.getValueAt(fila, 6).toString();
        int nuevoEstado = estadoActual.equals("Activo") ? 0 : 1;

        try (Connection con = new conexionlocal().getConexion()) {
            PreparedStatement ps = con.prepareStatement("UPDATE usuario SET estado=? WHERE id_usuario=?");
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente");
            cargarUsuarios();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_cambiar_estadoActionPerformed

    private void btn_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salirActionPerformed
        this.dispose();
    }//GEN-LAST:event_btn_salirActionPerformed

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        conexionlocal conLocal = new conexionlocal();
        conLocal.conectar();

        try {
            // ========================================
            // VALIDACIONES COMPLETAS
            // ========================================

            String nombre = txt_nombre.getText().trim();
            String apellido = txt_apellido.getText().trim();
            String usuario = txt_nombre_usuario.getText().trim().toLowerCase();
            String password = String.valueOf(txt_password.getPassword()).trim();
            String correo = txt_correo.getText().trim();

            // 1. Validar nombre
            if (!validarNombreApellido(nombre, "Nombres")) {
                return;
            }

            // 2. Validar apellido
            if (!validarNombreApellido(apellido, "Apellidos")) {
                return;
            }

            // 3. Validar nombre de usuario
            if (!validarNombreUsuario(usuario)) {
                return;
            }

            // 4. Validar contraseña
            if (!validarPassword(password)) {
                return;
            }

            // 5. Validar correo
            if (!validarCorreo(correo)) {
                return;
            }

            // 6. Validar rol
            if (!validarRol()) {
                return;
            }

            String rol = combo_rol.getSelectedItem().toString();
            Connection con = conLocal.getConexion();

            // 7. Verificar si el usuario ya existe
            if (usuarioExiste(usuario, con)) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe. Por favor, elija otro.");
                return;
            }

            // 8. Verificar si el correo ya existe
            if (correoExiste(correo, con)) {
                JOptionPane.showMessageDialog(this, "El correo electrónico ya está registrado. Por favor, use otro.");
                return;
            }

            // Buscar ID del rol
            int idRol = 0;
            PreparedStatement psRol = con.prepareStatement("SELECT id_rol FROM rol WHERE nombre = ?");
            psRol.setString(1, rol);
            ResultSet rsRol = psRol.executeQuery();
            if (rsRol.next()) {
                idRol = rsRol.getInt("id_rol");
            }
            rsRol.close();
            psRol.close();

            // Si es NUEVO (insertar)
            if (idUsuarioSeleccionado == -1) {
                // Generar un documento único
                String documentoTemporal = "USR" + System.currentTimeMillis();

                // Crear cliente asociado
                PreparedStatement psCliente = con.prepareStatement(
                        "INSERT INTO cliente(nombre, apellidos, nro_documento, direccion, id_tipo_cliente, id_tipo_documuento, estado) VALUES(?, ?, ?, 'N/A', 1, 1, 1)",
                        Statement.RETURN_GENERATED_KEYS);
                psCliente.setString(1, nombre);
                psCliente.setString(2, apellido);
                psCliente.setString(3, documentoTemporal);
                psCliente.executeUpdate();

                ResultSet rsCliente = psCliente.getGeneratedKeys();
                rsCliente.next();
                int idCliente = rsCliente.getInt(1);
                rsCliente.close();
                psCliente.close();

                // Insertar usuario con id_cliente
                String insertSql = "INSERT INTO usuario(id_cliente, nombre, apellido, nombre_usuario, password, correo, id_rol, estado) VALUES(?, ?, ?, ?, SHA2(?,256), ?, ?, 1)";
                PreparedStatement ps = con.prepareStatement(insertSql);
                ps.setInt(1, idCliente);
                ps.setString(2, nombre);
                ps.setString(3, apellido);
                ps.setString(4, usuario);
                ps.setString(5, password);
                ps.setString(6, correo);
                ps.setInt(7, idRol);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "✓ Usuario registrado con éxito.");

            } else {
                // Si es EDICIÓN (actualizar)
                String updateSql = "UPDATE usuario SET nombre=?, apellido=?, nombre_usuario=?, password=SHA2(?,256), correo=?, id_rol=? WHERE id_usuario=?";
                PreparedStatement ps = con.prepareStatement(updateSql);
                ps.setString(1, nombre);
                ps.setString(2, apellido);
                ps.setString(3, usuario);
                ps.setString(4, password);
                ps.setString(5, correo);
                ps.setInt(6, idRol);
                ps.setInt(7, idUsuarioSeleccionado);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "✓ Usuario actualizado con éxito.");
            }

            // Refrescar tabla y estado
            cargarUsuarios();
            limpiarCampos();
            bloquearCampos();
            actualizarEstado(EstadoApp.INICIAL);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al guardar usuario: " + e.getMessage());
        } finally {
            conLocal.desconectar();
        }

    }//GEN-LAST:event_btn_guardarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Registrar_usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registrar_usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registrar_usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registrar_usuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registrar_usuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cambiar_estado;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JButton btn_nuevo;
    private javax.swing.JButton btn_salir;
    private javax.swing.JComboBox<String> combo_rol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla_usuarios;
    private javax.swing.JTextField txt_apellido;
    private javax.swing.JTextField txt_correo;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_nombre_usuario;
    private javax.swing.JPasswordField txt_password;
    // End of variables declaration//GEN-END:variables
}
