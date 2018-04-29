/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.ui.dialogs;

import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.ui.simulator.JDesignPanel;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class JLERWindow extends javax.swing.JDialog {

    public JLERWindow(TTopology t, JDesignPanel pad, TImageBroker di, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        ventanaPadre = parent;
        dispensadorDeImagenes = di;
        pd = pad;
        topo = t;
        initComponents();
        initComponents2();
    }

    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        panelPestanias = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        iconoLER = new javax.swing.JLabel();
        etiquetaNombre = new javax.swing.JLabel();
        nombreNodo = new javax.swing.JTextField();
        panelPosicion = new javax.swing.JPanel();
        coordenadaX = new javax.swing.JLabel();
        coordenadaY = new javax.swing.JLabel();
        panelCoordenadas = new com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel();
        verNombre = new javax.swing.JCheckBox();
        panelRapido = new javax.swing.JPanel();
        selectorDeGenerarEstadisticasSencillo = new javax.swing.JCheckBox();
        iconoEnlace1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        selectorSencilloCaracteristicas = new javax.swing.JComboBox();
        panelAvanzado = new javax.swing.JPanel();
        selectorDeGenerarEstadisticasAvanzado = new javax.swing.JCheckBox();
        iconoEnlace2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        selectorDePotenciaDeConmutacion = new javax.swing.JSlider();
        etiquetaPotencia = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        selectorDeTamanioBuffer = new javax.swing.JSlider();
        etiquetaMemoriaBuffer = new javax.swing.JLabel();
        panelBotones = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations"); // NOI18N
        setTitle(bundle.getString("VentanaLER.titulo"));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPestanias.setFont(new java.awt.Font("Dialog", 0, 12));

        panelGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoLER.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.LER));
        iconoLER.setText(bundle.getString("VentanaLER.descripcion"));
        panelGeneral.add(iconoLER, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        etiquetaNombre.setFont(new java.awt.Font("Dialog", 0, 12));
        etiquetaNombre.setText(bundle.getString("VentanaLER.etiquetaNombre"));
        panelGeneral.add(etiquetaNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 80, 120, -1));
        panelGeneral.add(nombreNodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 105, 125, -1));

        panelPosicion.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("VentanaLER.etiquetaGrupo")));
        panelPosicion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        coordenadaX.setFont(new java.awt.Font("Dialog", 0, 12));
        coordenadaX.setText(bundle.getString("VentanaLER.X="));
        panelPosicion.add(coordenadaX, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));

        coordenadaY.setFont(new java.awt.Font("Dialog", 0, 12));
        coordenadaY.setText(bundle.getString("VentanaLER.Y="));
        panelPosicion.add(coordenadaY, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        panelCoordenadas.setBackground(new java.awt.Color(255, 255, 255));
        panelCoordenadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clicEnPanelCoordenadas(evt);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnPanelCoordenadas(evt);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDePanelCoordenadas(evt);
            }
        });
        panelPosicion.add(panelCoordenadas, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 130, 70));

        panelGeneral.add(panelPosicion, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 75, 180, 125));

        verNombre.setFont(new java.awt.Font("Dialog", 0, 12));
        verNombre.setSelected(true);
        verNombre.setText(bundle.getString("VentanaLER.verNombre"));
        panelGeneral.add(verNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 135, -1, -1));

        panelPestanias.addTab(bundle.getString("VentanaLER.tabs.General"), panelGeneral);

        panelRapido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        selectorDeGenerarEstadisticasSencillo.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticasSencillo.setText(bundle.getString("VentanaLER.GenerarEstadisticas"));
        selectorDeGenerarEstadisticasSencillo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGenerarEstadisticasSencillo(evt);
            }
        });
        panelRapido.add(selectorDeGenerarEstadisticasSencillo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));

        iconoEnlace1.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ASISTENTE));
        iconoEnlace1.setText(bundle.getString("VentanaLER.ConfiguracionRapida"));
        panelRapido.add(iconoEnlace1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("VentanaLER.CaracteristicasDelLER"));
        panelRapido.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 160, -1));

        selectorSencilloCaracteristicas.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorSencilloCaracteristicas.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Personalized", "Very low cost LER", "Low cost LER", "Medium cost LER", "Expensive LER", "Very expensive LER"}));
        selectorSencilloCaracteristicas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cliEnSelectorSencilloCaracteristicas(evt);
            }
        });
        panelRapido.add(selectorSencilloCaracteristicas, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 110, -1, -1));

        panelPestanias.addTab(bundle.getString("VentanaLER.tabs.Fast"), panelRapido);

        panelAvanzado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        selectorDeGenerarEstadisticasAvanzado.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticasAvanzado.setText(bundle.getString("VentanaLER.GenerarEstadisticas"));
        selectorDeGenerarEstadisticasAvanzado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGenerarEstadisticasAvanzada(evt);
            }
        });
        panelAvanzado.add(selectorDeGenerarEstadisticasAvanzado, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));

        iconoEnlace2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.AVANZADA));
        iconoEnlace2.setText(bundle.getString("VentanaLER.ConfiguracionAvanzada"));
        panelAvanzado.add(iconoEnlace2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("VentanaLER.PotenciaDeConmutacion"));
        panelAvanzado.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 140, -1));

        selectorDePotenciaDeConmutacion.setMajorTickSpacing(1000);
        selectorDePotenciaDeConmutacion.setMaximum(10240);
        selectorDePotenciaDeConmutacion.setMinimum(1);
        selectorDePotenciaDeConmutacion.setMinorTickSpacing(100);
        selectorDePotenciaDeConmutacion.setValue(1);
        selectorDePotenciaDeConmutacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                selectorDePotenciadeConmutacionCambiado(evt);
            }
        });
        panelAvanzado.add(selectorDePotenciaDeConmutacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 90, 130, 20));

        etiquetaPotencia.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaPotencia.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaPotencia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        etiquetaPotencia.setText(bundle.getString("VentanaLER.1_Mbps"));
        panelAvanzado.add(etiquetaPotencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, 70, 20));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("VentanaLER.TamanioDelBufferDeEntrada"));
        panelAvanzado.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, -1));

        selectorDeTamanioBuffer.setMajorTickSpacing(50);
        selectorDeTamanioBuffer.setMaximum(1024);
        selectorDeTamanioBuffer.setMinimum(1);
        selectorDeTamanioBuffer.setMinorTickSpacing(100);
        selectorDeTamanioBuffer.setValue(1);
        selectorDeTamanioBuffer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                selectorDeTamanioBufferCambiado(evt);
            }
        });
        panelAvanzado.add(selectorDeTamanioBuffer, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 100, 20));

        etiquetaMemoriaBuffer.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaMemoriaBuffer.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaMemoriaBuffer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        etiquetaMemoriaBuffer.setText(bundle.getString("VentanaLER.1_MB"));
        panelAvanzado.add(etiquetaMemoriaBuffer, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 60, 20));

        panelPestanias.addTab(bundle.getString("VentanaLER.tabs.Advanced"), panelAvanzado);

        panelPrincipal.add(panelPestanias, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 370, 240));

        panelBotones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ACEPTAR));
        jButton2.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.botones.mne.Aceptar").charAt(0));
        jButton2.setText(bundle.getString("VentanaLER.boton.Ok"));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });
        panelBotones.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 115, -1));

        jButton3.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton3.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.CANCELAR));
        jButton3.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.botones.mne.Cancelar").charAt(0));
        jButton3.setText(bundle.getString("VentanaLER.boton.Cancel"));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });
        panelBotones.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 15, 115, -1));

        panelPrincipal.add(panelBotones, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 255, 400, 55));

        getContentPane().add(panelPrincipal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 310));

        pack();
    }

    private void initComponents2() {
        panelCoordenadas.setDesignPanel(pd);
        java.awt.Dimension tamFrame = this.getSize();
        java.awt.Dimension tamPadre = ventanaPadre.getSize();
        setLocation((tamPadre.width - tamFrame.width) / 2, (tamPadre.height - tamFrame.height) / 2);
        configLER = null;
        coordenadaX.setText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.X=") + panelCoordenadas.getRealX());
        coordenadaY.setText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Y=") + panelCoordenadas.getRealY());
        BKUPMostrarNombre = true;
        BKUPNombre = "";
        BKUPPotencia = 0;
        BKUPTamBuffer = 0;
        reconfigurando = false;
        BKUPGenerarEstadisticas = false;
        this.selectorSencilloCaracteristicas.removeAllItems();
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Personalized_LER"));
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Very_low_range_LER"));
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Low_range_LER"));
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Medium_range_LER"));
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("High_range_LER"));
        this.selectorSencilloCaracteristicas.addItem(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Very_high_range_LER"));
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
    }

    private void cliEnSelectorSencilloCaracteristicas(java.awt.event.ActionEvent evt) {
        int opcionSeleccionada = this.selectorSencilloCaracteristicas.getSelectedIndex();
        if (opcionSeleccionada == 0) {
            // No se hace nada
            this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        } else if (opcionSeleccionada == 1) {
            this.selectorDePotenciaDeConmutacion.setValue(1);
            this.selectorDeTamanioBuffer.setValue(1);
            this.selectorSencilloCaracteristicas.setSelectedIndex(1);
        } else if (opcionSeleccionada == 2) {
            this.selectorDePotenciaDeConmutacion.setValue(2560);
            this.selectorDeTamanioBuffer.setValue(256);
            this.selectorSencilloCaracteristicas.setSelectedIndex(2);
        } else if (opcionSeleccionada == 3) {
            this.selectorDePotenciaDeConmutacion.setValue(5120);
            this.selectorDeTamanioBuffer.setValue(512);
            this.selectorSencilloCaracteristicas.setSelectedIndex(3);
        } else if (opcionSeleccionada == 4) {
            this.selectorDePotenciaDeConmutacion.setValue(7680);
            this.selectorDeTamanioBuffer.setValue(768);
            this.selectorSencilloCaracteristicas.setSelectedIndex(4);
        } else if (opcionSeleccionada == 5) {
            this.selectorDePotenciaDeConmutacion.setValue(10240);
            this.selectorDeTamanioBuffer.setValue(1024);
            this.selectorSencilloCaracteristicas.setSelectedIndex(5);
        }
    }

    private void selectorDeTamanioBufferCambiado(javax.swing.event.ChangeEvent evt) {
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        this.etiquetaMemoriaBuffer.setText(this.selectorDeTamanioBuffer.getValue() + " " + java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.MB"));
    }

    private void selectorDePotenciadeConmutacionCambiado(javax.swing.event.ChangeEvent evt) {
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        this.etiquetaPotencia.setText(this.selectorDePotenciaDeConmutacion.getValue() + " " + java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.Mbps."));
    }

    private void clicEnGenerarEstadisticasAvanzada(java.awt.event.ActionEvent evt) {
        this.selectorDeGenerarEstadisticasSencillo.setSelected(this.selectorDeGenerarEstadisticasAvanzado.isSelected());
    }

    private void clicEnGenerarEstadisticasSencillo(java.awt.event.ActionEvent evt) {
        this.selectorDeGenerarEstadisticasAvanzado.setSelected(this.selectorDeGenerarEstadisticasSencillo.isSelected());
    }

    private void clicEnCancelar(java.awt.event.ActionEvent evt) {
        if (reconfigurando) {
            configLER.setShowName(BKUPMostrarNombre);
            configLER.setName(BKUPNombre);
            configLER.setWellConfigured(true);
            configLER.setBufferSizeInMBytes(BKUPTamBuffer);
            configLER.setRoutingPowerInMbps(BKUPPotencia);
            configLER.setGenerateStats(BKUPGenerarEstadisticas);
            reconfigurando = false;
        } else {
            configLER.setWellConfigured(false);
        }
        this.setVisible(false);
        this.dispose();
    }

    private void clicEnAceptar(java.awt.event.ActionEvent evt) {
        configLER.setWellConfigured(true);
        if (!this.reconfigurando) {
            configLER.setScreenPosition(new Point(panelCoordenadas.getRealX(), panelCoordenadas.getRealY()));
        }
        configLER.setBufferSizeInMBytes(this.selectorDeTamanioBuffer.getValue());
        configLER.setRoutingPowerInMbps(this.selectorDePotenciaDeConmutacion.getValue());
        configLER.setGenerateStats(this.selectorDeGenerarEstadisticasSencillo.isSelected());
        configLER.setName(nombreNodo.getText());
        configLER.setShowName(verNombre.isSelected());
        configLER.setGenerateStats(this.selectorDeGenerarEstadisticasSencillo.isSelected());
        int error = configLER.validateConfig(topo, this.reconfigurando);
        if (error != TLERNode.OK) {
            JWarningWindow va = new JWarningWindow(ventanaPadre, true, dispensadorDeImagenes);
            va.setWarningMessage(configLER.getErrorMessage(error));
            va.show();
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void clicEnPanelCoordenadas(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            panelCoordenadas.setCoordinates(evt.getPoint());
            coordenadaX.setText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaconfigLER.X=_") + panelCoordenadas.getRealX());
            coordenadaY.setText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaconfigLER.Y=_") + panelCoordenadas.getRealY());
            panelCoordenadas.repaint();
        }
    }

    private void ratonSaleDePanelCoordenadas(java.awt.event.MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void ratonEntraEnPanelCoordenadas(java.awt.event.MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        configLER.setWellConfigured(false);
        dispose();
    }

    public void ponerConfiguracion(TLERNode tnler, boolean recfg) {
        configLER = tnler;
        reconfigurando = recfg;
        if (reconfigurando) {
            this.panelCoordenadas.setEnabled(false);
            this.panelCoordenadas.setToolTipText(null);

            BKUPGenerarEstadisticas = tnler.isGeneratingStats();
            BKUPMostrarNombre = tnler.nameMustBeDisplayed();
            BKUPNombre = tnler.getName();
            BKUPPotencia = tnler.getRoutingPowerInMbps();
            BKUPTamBuffer = tnler.getBufferSizeInMBytes();

            this.selectorDeGenerarEstadisticasAvanzado.setSelected(BKUPGenerarEstadisticas);
            this.selectorDeGenerarEstadisticasSencillo.setSelected(BKUPGenerarEstadisticas);
            this.selectorDePotenciaDeConmutacion.setValue(BKUPPotencia);
            this.selectorDeTamanioBuffer.setValue(BKUPTamBuffer);
            this.nombreNodo.setText(BKUPNombre);
            this.verNombre.setSelected(BKUPMostrarNombre);
        }
    }

    private TImageBroker dispensadorDeImagenes;
    private Frame ventanaPadre;
    private JDesignPanel pd;
    private TLERNode configLER;
    private TTopology topo;
    private boolean BKUPMostrarNombre;
    private String BKUPNombre;
    private int BKUPPotencia;
    private int BKUPTamBuffer;
    private boolean BKUPGenerarEstadisticas;
    private boolean reconfigurando;
    private javax.swing.JLabel coordenadaX;
    private javax.swing.JLabel coordenadaY;
    private javax.swing.JLabel etiquetaMemoriaBuffer;
    private javax.swing.JLabel etiquetaNombre;
    private javax.swing.JLabel etiquetaPotencia;
    private javax.swing.JLabel iconoEnlace1;
    private javax.swing.JLabel iconoEnlace2;
    private javax.swing.JLabel iconoLER;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nombreNodo;
    private javax.swing.JPanel panelAvanzado;
    private javax.swing.JPanel panelBotones;
    private com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel panelCoordenadas;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JTabbedPane panelPestanias;
    private javax.swing.JPanel panelPosicion;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelRapido;
    private javax.swing.JCheckBox selectorDeGenerarEstadisticasAvanzado;
    private javax.swing.JCheckBox selectorDeGenerarEstadisticasSencillo;
    private javax.swing.JSlider selectorDePotenciaDeConmutacion;
    private javax.swing.JSlider selectorDeTamanioBuffer;
    private javax.swing.JComboBox selectorSencilloCaracteristicas;
    private javax.swing.JCheckBox verNombre;
}
