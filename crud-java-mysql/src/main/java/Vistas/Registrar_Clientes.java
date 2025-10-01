package Vistas;

import Configuracion.FiltroAlfanumerico;
import Configuracion.FiltroSoloLetras;
import Configuracion.FiltroSoloNumeros;
import java.sql.Connection;
import Configuracion.conexionlocal; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import Configuracion.conexionlocal;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

enum EstadoApp {
    INICIAL,
    EDITANDO,
    SELECCIONADO
}
public class Registrar_Clientes extends javax.swing.JFrame {
    
    DefaultTableModel modelo = new DefaultTableModel();
    conexionlocal cn = new conexionlocal();
    private int idClienteSeleccionado = -1;
    private EstadoApp estadoActual = EstadoApp.INICIAL;


    
    public Registrar_Clientes() {
        initComponents();
        cargarTiposDeCliente();
        cargarTiposDeDocumentos();
        cargarClientes();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        actualizarEstado(EstadoApp.INICIAL);
    
        
        ((AbstractDocument) txt_nombre.getDocument()).setDocumentFilter(new FiltroSoloLetras());
        ((AbstractDocument) txt_apellido.getDocument()).setDocumentFilter(new FiltroSoloLetras());
        
        
        combo_tipodo_documento.addActionListener(evt -> {
            int tipoDoc = combo_tipodo_documento.getSelectedIndex();

            if (tipoDoc == 1) { // DNI
                ((AbstractDocument) txt_nro_documento.getDocument()).setDocumentFilter(new FiltroSoloNumeros(8));
            } else if (tipoDoc == 2) { // Carné de Extranjería
                ((AbstractDocument) txt_nro_documento.getDocument()).setDocumentFilter(new FiltroAlfanumerico(12));
            } else if (tipoDoc == 3) { // RUC
                ((AbstractDocument) txt_nro_documento.getDocument()).setDocumentFilter(new FiltroSoloNumeros(11));
            } else {
                // Si no selecciona nada, se quita el filtro
                ((AbstractDocument) txt_nro_documento.getDocument()).setDocumentFilter(null);
            }

            txt_nro_documento.setText(""); // limpiar al cambiar tipo de doc
        });
        
        
        tabla_cliente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tabla_cliente.getSelectedRow();
                if (fila != -1) {
                    idClienteSeleccionado = Integer.parseInt(tabla_cliente.getValueAt(fila, 0).toString());

                    txt_nombre.setText(tabla_cliente.getValueAt(fila, 1).toString());
                    txt_apellido.setText(tabla_cliente.getValueAt(fila, 2).toString());
                    txt_nro_documento.setText(tabla_cliente.getValueAt(fila, 3).toString());
                    txt_direccion.setText(tabla_cliente.getValueAt(fila, 4).toString());

                    combo_tipo_cliente.setSelectedItem(tabla_cliente.getValueAt(fila, 5).toString());
                    combo_tipodo_documento.setSelectedItem(tabla_cliente.getValueAt(fila, 6).toString());

                    actualizarEstado(EstadoApp.SELECCIONADO); // <- MUY IMPORTANTE
                }
            }
        });



      
    }
    
    private void actualizarEstado(EstadoApp nuevoEstado) {
        this.estadoActual = nuevoEstado;

        switch (estadoActual) {
            case INICIAL:
                btn_nuevo.setEnabled(true);
                btn_guardar.setEnabled(false);
                btn_cancelar.setEnabled(false);
                btn_cambiar_estado.setEnabled(false);
                btn_salir.setEnabled(true);
                break;
            case EDITANDO:
                btn_nuevo.setEnabled(false);
                btn_guardar.setEnabled(true);
                btn_cancelar.setEnabled(true);
                btn_cambiar_estado.setEnabled(false);
                btn_salir.setEnabled(true);
                break;
            case SELECCIONADO:
                btn_nuevo.setEnabled(true);
                btn_guardar.setEnabled(false);
                btn_cancelar.setEnabled(false);
                btn_cambiar_estado.setEnabled(true);
                btn_salir.setEnabled(true);
                break;
        }
    }

    private void limpiarCampos() {
        txt_nro_documento.setText("");
        txt_nombre.setText("");
        txt_apellido.setText("");
        txt_direccion.setText("");
        combo_tipo_cliente.setSelectedIndex(0);
        combo_tipodo_documento.setSelectedIndex(0);
        idClienteSeleccionado = -1;
    }

    
    private void cargarTiposDeDocumentos() {
        conexionlocal con = new conexionlocal();
        con.conectar();

        Connection conn = con.getConexion();

        if (conn != null) {
            try {
                String sql = "SELECT nombre FROM tipo_documento";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    combo_tipodo_documento.addItem(rs.getString("nombre"));
                }

                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar tipos de cliente: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
        }
    }

    private void cargarTiposDeCliente() {
        conexionlocal con = new conexionlocal();
        con.conectar();

        Connection conn = con.getConexion();

        if (conn != null) {
            try {
                String sql = "SELECT nombre FROM tipo_cliente";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    combo_tipo_cliente.addItem(rs.getString("nombre"));
                }

                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar tipos de cliente: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
        }
    }
    
    public void cargarClientes() {
        String[] columnas = {"ID", "Nombre", "Apellidos", "Documento", "Dirección", "Tipo Cliente", "Tipo Documento", "Estado"};
        modelo.setColumnIdentifiers(columnas);
        tabla_cliente.setModel(modelo);
        modelo.setRowCount(0);

        String sql = """
        SELECT c.id_cliente, c.nombre, c.apellidos, c.nro_documento, c.direccion,
               tc.nombre AS tipo_cliente, td.nombre AS tipo_documento, c.estado
        FROM cliente c
        INNER JOIN tipo_cliente tc ON c.id_tipo_cliente = tc.id_tipo_cliente
        INNER JOIN tipo_documento td ON c.id_tipo_documuento = td.id_tipo_documento
        ORDER BY c.id_cliente ASC
    """;

        try {
            cn.conectar();
            Connection conn = cn.getConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                String documento = rs.getString("nro_documento");
                String direccion = rs.getString("direccion");
                String tipoCliente = rs.getString("tipo_cliente");
                String tipoDocumento = rs.getString("tipo_documento");
                String estado = rs.getBoolean("estado") ? "Activo" : "Inactivo";

                Object[] fila = {id, nombre, apellidos, documento, direccion, tipoCliente, tipoDocumento, estado};
                modelo.addRow(fila);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ERROR al cargar clientes: " + e.getMessage());
        }
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
        combo_tipo_cliente = new javax.swing.JComboBox<>();
        txt_nro_documento = new javax.swing.JTextField();
        txt_nombre = new javax.swing.JTextField();
        txt_direccion = new javax.swing.JTextField();
        txt_apellido = new javax.swing.JTextField();
        btn_nuevo = new javax.swing.JButton();
        btn_guardar = new javax.swing.JButton();
        btn_cancelar = new javax.swing.JButton();
        btn_cambiar_estado = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_cliente = new javax.swing.JTable();
        btn_salir = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        combo_tipodo_documento = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setFont(new java.awt.Font("Candara", 3, 24)); // NOI18N
        jLabel1.setText("CLIENTES");

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel2.setText("N° DOCUMENTO:");

        jLabel3.setBackground(new java.awt.Color(204, 204, 204));
        jLabel3.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel3.setText("NOMBRE:");

        jLabel4.setBackground(new java.awt.Color(204, 204, 204));
        jLabel4.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel4.setText("DIRECCIÓN:");

        jLabel5.setBackground(new java.awt.Color(204, 204, 204));
        jLabel5.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel5.setText("APELLIDOS:");

        jLabel6.setBackground(new java.awt.Color(204, 204, 204));
        jLabel6.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel6.setText("TIPO CLIENTE:");

        combo_tipo_cliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ">>SELECCIONE..." }));
        combo_tipo_cliente.setToolTipText("");
        combo_tipo_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_tipo_clienteActionPerformed(evt);
            }
        });

        txt_nro_documento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nro_documentoActionPerformed(evt);
            }
        });

        txt_nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nombreActionPerformed(evt);
            }
        });

        txt_direccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_direccionActionPerformed(evt);
            }
        });

        btn_nuevo.setText("NUEVO");
        btn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevoActionPerformed(evt);
            }
        });

        btn_guardar.setText("GUARDAR");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        btn_cancelar.setText("CANCELAR");
        btn_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelarActionPerformed(evt);
            }
        });

        btn_cambiar_estado.setText("CAMBIAR ESTADO");
        btn_cambiar_estado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cambiar_estadoActionPerformed(evt);
            }
        });

        tabla_cliente.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabla_cliente);

        btn_salir.setText("SALIR");
        btn_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salirActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        jLabel7.setText("DOCUMENTO:");

        combo_tipodo_documento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ">>SELECCIONE..." }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(344, 344, 344)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(btn_nuevo)
                                .addGap(47, 47, 47)
                                .addComponent(btn_guardar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_tipodo_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(26, 26, 26)
                                .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_tipo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn_cancelar)
                                .addGap(52, 52, 52)
                                .addComponent(btn_cambiar_estado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                                .addComponent(btn_salir))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel2))
                                .addGap(44, 44, 44)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_apellido, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                                    .addComponent(txt_nro_documento))
                                .addGap(56, 56, 56)))))
                .addGap(26, 26, 26))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txt_nro_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(combo_tipodo_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txt_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(combo_tipo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_nuevo)
                    .addComponent(btn_guardar)
                    .addComponent(btn_cancelar)
                    .addComponent(btn_cambiar_estado)
                    .addComponent(btn_salir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_nro_documentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nro_documentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nro_documentoActionPerformed

    private void txt_nombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nombreActionPerformed

    private void txt_direccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_direccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_direccionActionPerformed

    private void combo_tipo_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_tipo_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_tipo_clienteActionPerformed

    private void btn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevoActionPerformed
        
        limpiarCampos();
        txt_nro_documento.requestFocus();
        JOptionPane.showMessageDialog(this, "Nuevo registro iniciado.");
        actualizarEstado(EstadoApp.EDITANDO);

    }//GEN-LAST:event_btn_nuevoActionPerformed

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed
        try {
            // 1. Validar campos
            if (txt_nombre.getText().trim().isEmpty()
                    || txt_apellido.getText().trim().isEmpty()
                    || txt_nro_documento.getText().trim().isEmpty()
                    || txt_direccion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }
            if (combo_tipo_cliente.getSelectedIndex() == 0 || combo_tipodo_documento.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar tipo de cliente y tipo de documento.");
                return;
            }

            // 2. Validar documento
            String nroDocumento = txt_nro_documento.getText().trim();
            int tipoDoc = combo_tipodo_documento.getSelectedIndex();
            if (tipoDoc == 1 && !nroDocumento.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "El DNI debe tener 8 dígitos.");
                return;
            }
            if (tipoDoc == 2 && !nroDocumento.matches("[A-Z0-9]{5,12}")) {
                JOptionPane.showMessageDialog(this, "Carné debe tener 5-12 caracteres alfanuméricos.");
                return;
            }
            if (tipoDoc == 3) {
                if (!nroDocumento.matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(this, "El RUC debe tener 11 dígitos.");
                    return;
                }
                String prefix = nroDocumento.substring(0, 2);
                if (!(prefix.equals("10") || prefix.equals("15") || prefix.equals("16") || prefix.equals("17") || prefix.equals("20"))) {
                    JOptionPane.showMessageDialog(this, "El RUC debe comenzar con 10, 15, 16, 17 o 20.");
                    return;
                }
            }

            cn.conectar();
            Connection conn = cn.getConexion();

            if (idClienteSeleccionado == -1) {
                // INSERTAR
                String checkSql = "SELECT COUNT(*) FROM cliente WHERE nro_documento = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, nroDocumento);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "El número de documento ya está registrado.");
                    rs.close();
                    checkPs.close();
                    conn.close();
                    return;
                }
                rs.close();
                checkPs.close();

                String sql = "INSERT INTO cliente (nombre, apellidos, nro_documento, direccion, id_tipo_cliente, id_tipo_documuento, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, 1)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txt_nombre.getText().trim());
                ps.setString(2, txt_apellido.getText().trim());
                ps.setString(3, nroDocumento);
                ps.setString(4, txt_direccion.getText().trim());
                ps.setInt(5, combo_tipo_cliente.getSelectedIndex());
                ps.setInt(6, combo_tipodo_documento.getSelectedIndex());
                ps.executeUpdate();
                ps.close();
                JOptionPane.showMessageDialog(this, "Cliente guardado con éxito.");
            } else {
                // ACTUALIZAR
                String sql = "UPDATE cliente SET nombre=?, apellidos=?, nro_documento=?, direccion=?, id_tipo_cliente=?, id_tipo_documuento=? "
                        + "WHERE id_cliente=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txt_nombre.getText().trim());
                ps.setString(2, txt_apellido.getText().trim());
                ps.setString(3, nroDocumento);
                ps.setString(4, txt_direccion.getText().trim());
                ps.setInt(5, combo_tipo_cliente.getSelectedIndex());
                ps.setInt(6, combo_tipodo_documento.getSelectedIndex());
                ps.setInt(7, idClienteSeleccionado);
                ps.executeUpdate();
                ps.close();
                JOptionPane.showMessageDialog(this, "Cliente actualizado con éxito.");
            }

            conn.close();
            cargarClientes();
            limpiarCampos();
            actualizarEstado(EstadoApp.SELECCIONADO);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void btn_cambiar_estadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cambiar_estadoActionPerformed
        if (idClienteSeleccionado != -1) {
            try {
                cn.conectar();
                Connection conn = cn.getConexion();
                String sql = "UPDATE cliente SET estado = NOT estado WHERE id_cliente=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idClienteSeleccionado);
                ps.executeUpdate();
                ps.close();
                conn.close();
                JOptionPane.showMessageDialog(this, "Estado cambiado correctamente.");
                cargarClientes();
                limpiarCampos();
                actualizarEstado(EstadoApp.INICIAL);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_cambiar_estadoActionPerformed

    private void btn_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salirActionPerformed
        dispose();
    }//GEN-LAST:event_btn_salirActionPerformed

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelarActionPerformed
        limpiarCampos();
        JOptionPane.showMessageDialog(this, "Operación cancelada.");
        actualizarEstado(EstadoApp.INICIAL);
    }//GEN-LAST:event_btn_cancelarActionPerformed

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
            java.util.logging.Logger.getLogger(Registrar_Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registrar_Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registrar_Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registrar_Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registrar_Clientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cambiar_estado;
    private javax.swing.JButton btn_cancelar;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JButton btn_nuevo;
    private javax.swing.JButton btn_salir;
    private javax.swing.JComboBox<String> combo_tipo_cliente;
    private javax.swing.JComboBox<String> combo_tipodo_documento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabla_cliente;
    private javax.swing.JTextField txt_apellido;
    private javax.swing.JTextField txt_direccion;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_nro_documento;
    // End of variables declaration//GEN-END:variables
}
