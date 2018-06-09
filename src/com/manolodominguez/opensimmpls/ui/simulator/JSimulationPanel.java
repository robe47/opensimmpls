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
package com.manolodominguez.opensimmpls.ui.simulator;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventNodeCongested;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketDiscarded;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketGenerated;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketOnFly;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketRouted;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSent;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSwitched;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEvent;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TOpenSimMPLSEvent;
import com.manolodominguez.opensimmpls.utils.TLock;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javax.swing.JPanel;

/**
 * Esta clase implementa un panel que recibir� eventos de simulaci�n y los
 * representar� en pantalla dando la sensaci�n de continuidad a una simulaci�n
 * visual.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JSimulationPanel extends JPanel {

    /**
     * Crea una nueva instancia de JPanelSimulacion
     * @since 2.0
     */
    public JSimulationPanel() {
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelSimulacion
     * @since 2.0
     * @param di Dispensador de im�genes de donde el panel tomar� las im�genes que necesite
     * mostrar en pantalla.
     */    
    public JSimulationPanel(TImageBroker di) {
        dispensadorDeImagenes = di;
        initComponents();
    }

    /**
     * @since 2.0
     */    
    private void initComponents () {
        this.translations = ResourceBundle.getBundle(AvailableBundles.SIMULATION_PANEL.getPath());
        tamPantalla=Toolkit.getDefaultToolkit().getScreenSize();
	imagenbuf = null;
        g2Dbuf = null;
        topologia=null;
        maxX = 10;
        maxY = 10;
        bufferEventos = new TreeSet();
        bufferParaSimular = new TreeSet();
        ticActual = 0;
        mlsPorTic = 0;
        mostrarLeyenda = false;
        cerrojo = new TLock();
        ficheroTraza = null;
        streamFicheroTraza = null;
        streamTraza = null;
    }

    /**
     * Reincia todos los atributos de la clase a su valor de creaci�n.
     * @since 2.0
     */    
    public void reset() {
        cerrojo.lock();
        Iterator it = null;
        it = bufferEventos.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        it = bufferParaSimular.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        mostrarLeyenda = false;
        cerrojo.unLock();
        ticActual = 0;
        ficheroTraza = null;;
        streamFicheroTraza = null;
        streamTraza = null;
    }
    
    /**
     * Este m�todo env�a un evento al fichero de traza, para que quede registrado.
     * @param es Evento de simulaci�n que se desea tracear.
     * @since 2.0
     */    
    public void enviarATraza(TSimulationEvent es) {
        String texto = "";
        texto += this.ticActual + ": ";
        if (this.streamTraza != null) {
            if (es.getType() == TOpenSimMPLSEvent.SIMULATION) {
                texto += es.toString();
                this.streamTraza.println(texto);
            }
        }
    }
    
    /**
     * Este m�todo establece el fichero de traza.
     * @param ft Fichero de traza.
     * @since 2.0
     */    
    public void ponerFicheroTraza(File ft) {
        if (this.ficheroTraza != null) {
            if (this.streamFicheroTraza != null) {
                try {
                    this.streamFicheroTraza.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.streamTraza != null) {
                this.streamTraza.close();
            }
        }
        this.ficheroTraza = ft;
        if (this.ficheroTraza != null) {
            if (this.ficheroTraza.exists()) {
                this.ficheroTraza.delete();
            }
            try {
                this.streamFicheroTraza = new FileOutputStream(ficheroTraza);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.streamTraza = new PrintStream(streamFicheroTraza, true);
        } else {
            this.streamFicheroTraza = null;
            this.streamTraza = null;
            this.ficheroTraza = null;
        }
    }
    
    /**
     * Este m�todo asocia al panel de simulaci�n una topology concreta.
     * @since 2.0
     * @param t Topolog�a que se debe representar en el panel de simulaci�n.
     */    
    public void setTopology(TTopology t) {
        topologia = t;
    }

    /**
     * Este m�todo asigna un dispensador de im�genes al panel de forma que de ah�
     * tomar� todas las im�genes que deba mostrar en la pantalla.
     * @since 2.0
     * @param di El dispensador de im�genes.
     */    
    public void setImageBroker(TImageBroker di) {
        dispensadorDeImagenes = di;
    }

    /**
     * Este m�todo determina que grosor en p�xeles debe tener un enlace de la topolog�a
     * al ser mostrado, segun su delay.
     * @since 2.0
     * @param delay Retardo del enlace.
     * @return Grosor en p�xeles que se debe usar para mostrar el enlace.
     */    
    public double obtenerGrosorEnlace(double delay) {
        return (16/Math.log(delay+100));
    }
    
    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void prepararImagen(Graphics2D g2Dbuf) {
        g2Dbuf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Dbuf.setColor(Color.WHITE);
        g2Dbuf.fillRect(0,0, tamPantalla.width, tamPantalla.height);
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarDominio(Graphics2D g2Dbuf) {
        Iterator itd = topologia.getNodesIterator();
        TNode nd;
        Polygon pol = new Polygon();
        int vertices = 0;
        while (itd.hasNext()) {
            nd = (TNode) itd.next();
            if ((nd.getNodeType() == TNode.LER) ||
               (nd.getNodeType() == TNode.ACTIVE_LER)) {
                   pol.addPoint(nd.getScreenPosition().x+24, nd.getScreenPosition().y+24);
                   vertices ++;
               }
        };
        if (vertices > 2) {
            g2Dbuf.setColor(this.DOMAIN_BACKGROUND_COLOR);
            g2Dbuf.fillPolygon(pol);
            g2Dbuf.setColor(this.DOMAIN_BORDER_COLOR);
            g2Dbuf.drawPolygon(pol);
        } else if (vertices == 2) {
            int x1 = Math.min(pol.xpoints[0], pol.xpoints[1]);
            int y1 = Math.min(pol.ypoints[0], pol.ypoints[1]);
            int x2 = Math.max(pol.xpoints[0], pol.xpoints[1]);
            int y2 = Math.max(pol.ypoints[0], pol.ypoints[1]);
            int ancho = x2-x1;
            int alto = y2-y1;
            g2Dbuf.setColor(this.DOMAIN_BACKGROUND_COLOR);
            g2Dbuf.fillRect(x1, y1, ancho, alto);
            g2Dbuf.setColor(this.DOMAIN_BORDER_COLOR);
            g2Dbuf.drawRect(x1, y1, ancho, alto);
        } else if (vertices == 1) {
            g2Dbuf.setColor(this.DOMAIN_BACKGROUND_COLOR);
            g2Dbuf.fillOval(pol.xpoints[0]-50, pol.ypoints[0]-40, 100, 80);
            g2Dbuf.setColor(this.DOMAIN_BORDER_COLOR);
            g2Dbuf.drawOval(pol.xpoints[0]-50, pol.ypoints[0]-40, 100, 80);
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarEnlaces(Graphics2D g2Dbuf) {
        Iterator ite = topologia.getLinksIterator();
        while (ite.hasNext()) {
            TLink enlace = (TLink) ite.next();
            Point inicio = enlace.getHeadEndNode().getScreenPosition();
            Point fin = enlace.getTailEndNode().getScreenPosition();
            int del = enlace.getDelay();
            g2Dbuf.setStroke(new BasicStroke((float) obtenerGrosorEnlace(del)));
            if (enlace.getLinkType() == TLink.EXTERNAL_LINK) {
                g2Dbuf.setColor(Color.GRAY);
            } else {
                g2Dbuf.setColor(Color.BLUE);
            }
            if (enlace.isBroken()) {
                    float dash1[] = {5.0f};
                    BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                    g2Dbuf.setColor(Color.RED);
                    g2Dbuf.setStroke(dashed);
            }
            g2Dbuf.drawLine(inicio.x+24, inicio.y+24, fin.x+24, fin.y+24);
            g2Dbuf.setStroke(new BasicStroke((float) 1));

            if (!enlace.isBroken()) {
                if (enlace.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink ei = (TInternalLink) enlace;
                    if (ei.isBeingUsedByAnyLSP()) {
                        float dash1[] = {5.0f};
                        BasicStroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                        g2Dbuf.setColor(this.LSP_COLOR);
                        g2Dbuf.setStroke(dashed);
                        if (inicio.x == fin.x) {
                            g2Dbuf.drawLine(inicio.x+20, inicio.y+24, fin.x+20, fin.y+24);
                        }
                        else if (inicio.y == fin.y) {
                            g2Dbuf.drawLine(inicio.x+24, inicio.y+20, fin.x+24, fin.y+20);
                        }
                        else if (((inicio.x < fin.x) && (inicio.y > fin.y)) || ((inicio.x > fin.x) && (inicio.y < fin.y))) {
                            g2Dbuf.drawLine(inicio.x+20, inicio.y+20, fin.x+20, fin.y+20);
                        }
                        else if (((inicio.x < fin.x) && (inicio.y < fin.y)) || ((inicio.x > fin.x) && (inicio.y > fin.y))) {
                            g2Dbuf.drawLine(inicio.x+28, inicio.y+20, fin.x+28, fin.y+20);
                        }
                        g2Dbuf.setStroke(new BasicStroke(1));
                    }
                }
            }


            if (!enlace.isBroken()) {
                if (enlace.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink ei = (TInternalLink) enlace;
                    if (ei.isBeingUsedByAnyBackupLSP()) {
                        float dash1[] = {10.0f, 5.0f, 0.2f, 5.0f};
                        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                        g2Dbuf.setColor(Color.BLACK);
                        g2Dbuf.setStroke(dashed);
                        if (inicio.x == fin.x) {
                            g2Dbuf.drawLine(inicio.x+28, inicio.y+24, fin.x+28, fin.y+24);
                        }
                        else if (inicio.y == fin.y) {
                            g2Dbuf.drawLine(inicio.x+24, inicio.y+28, fin.x+24, fin.y+28);
                        }
                        else if (((inicio.x < fin.x) && (inicio.y > fin.y)) || ((inicio.x > fin.x) && (inicio.y < fin.y))) {
                            g2Dbuf.drawLine(inicio.x+28, inicio.y+28, fin.x+28, fin.y+28);
                        }
                        else if (((inicio.x < fin.x) && (inicio.y < fin.y)) || ((inicio.x > fin.x) && (inicio.y > fin.y))) {
                            g2Dbuf.drawLine(inicio.x+20, inicio.y+28, fin.x+20, fin.y+28);
                        }
                        g2Dbuf.setStroke(new BasicStroke(1));
                    }
                }
            }
//
            if (enlace.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(enlace.getName().toCharArray(), 0, enlace.getName().length());
                int posX1 = enlace.getHeadEndNode().getScreenPosition().x+24;
                int posY1 = enlace.getHeadEndNode().getScreenPosition().y+24;
                int posX2 = enlace.getTailEndNode().getScreenPosition().x+24;
                int posY2 = enlace.getTailEndNode().getScreenPosition().y+24;
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (anchoTexto / 2);
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                g2Dbuf.setColor(this.LINK_NAME_COLOR);
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(enlace.getName(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarNodos(Graphics2D g2Dbuf) {
        maxX = 10;
        maxY = 10;
        Iterator ite = topologia.getNodesIterator();
        while (ite.hasNext()) {
            TNode nodo = (TNode) ite.next();
            Point posicion = nodo.getScreenPosition();

            if ((posicion.x+48) > maxX)
                maxX = posicion.x+48;
            if ((posicion.y+48) > maxY)
                maxY = posicion.y+48;
            this.setPreferredSize(new Dimension(maxX, maxY));
            this.revalidate();
            
            int tipo = nodo.getNodeType();
            switch (tipo) {
                case TNode.TRAFFIC_GENERATOR: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.EMISOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.EMISOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.TRAFFIC_SINK: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.RECEPTOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.RECEPTOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LER: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LER), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LER_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.ACTIVE_LER: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LERA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LERA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LSR: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LSR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LSR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.ACTIVE_LSR: {
                    if (nodo.isSelected() == TNode.UNSELECTED)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LSRA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.LSRA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
            }
            if (nodo.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(nodo.getName().toCharArray(), 0, nodo.getName().length());
                int posX = (nodo.getScreenPosition().x + 24) - ((anchoTexto/2));
                int posY = nodo.getScreenPosition().y+60;
                g2Dbuf.setColor(Color.WHITE);
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(nodo.getName(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * Este m�todo permite establecer cuantos milisegundos de espera va a haber entre
     * que se muestra un tic en la simulaci�n, y se meustra el siguiente. A efectos
     * pr�cticos permite ralentizar la simulaci�n en tiempo real.
     * @param mls N�mero de milisegundos de retardo entre frames de una misma simulaci�n.
     * @since 2.0
     */    
    public void ponerMlsPorTic(int mls) {
        this.mlsPorTic = mls;
    }
    
    /**
     * Este m�todo permite a�adir un evento a la lista de eventos que se deben mostrar
     * en la ventana del simulador.
     * @param evt El nuevo evento que se debe mostrar en la simulaci�n.
     * @since 2.0
     */    
    public void addEvent(TSimulationEvent evt) {
        cerrojo.lock();
        this.enviarATraza(evt);
        if (evt.getInstant() <= ticActual) {
            bufferEventos.add(evt);
            cerrojo.unLock();
        } else {
            ticActual = evt.getInstant();
            Iterator it = this.bufferParaSimular.iterator();
            TSimulationEvent evento = null;
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            it = bufferEventos.iterator();
            evento = null;
            while (it.hasNext()) {
                evento = (TSimulationEvent) it.next();
                bufferParaSimular.add(evento);
                it.remove();
            }
            cerrojo.unLock();
            repaint();
            bufferEventos.add(evt);
            try {
                Thread.currentThread().sleep(this.mlsPorTic);
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
    }
    
    /**
     * Este m�todo permite dibujar los eventos relacionados con las PDU's que circulan
     * por la red.
     * @since 2.0
     * @param g2D El lienzo donde se mostrar� el evento.
     */    
    public void dibujarEventosPaquete(Graphics2D g2D) {
        cerrojo.lock();
        try {
            Iterator it = bufferParaSimular.iterator();
            TSimulationEvent evento = null;
            while (it.hasNext()) {
                evento = (TSimulationEvent) it.next();
                if (evento != null) {
                    if (evento.getSubtype() == TSimulationEvent.PACKET_ON_FLY) {
                        TSimulationEventPacketOnFly ept = (TSimulationEventPacketOnFly) evento;
                        TLink et = (TLink) ept.getSource();
                        Point p = et.getScreenPacketPosition(ept.getTransitPercentage());
                        if (ept.getPacketType() == TAbstractPDU.GPSRP) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_GOS), p.x-14, p.y-14, null);
                        } else if (ept.getPacketType() == TAbstractPDU.TLDP) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_LDP), p.x-8, p.y-8, null);
                        } else if (ept.getPacketType() == TAbstractPDU.IPV4) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4), p.x-8, p.y-8, null);
                        } else if (ept.getPacketType() == TAbstractPDU.IPV4_GOS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4_GOS), p.x-8, p.y-8, null);
                        } else if (ept.getPacketType() == TAbstractPDU.MPLS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS), p.x-8, p.y-8, null);
                        } else if (ept.getPacketType() == TAbstractPDU.MPLS_GOS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS_GOS), p.x-8, p.y-8, null);
                        }
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_DISCARDED) {
                        TSimulationEventPacketDiscarded epd = (TSimulationEventPacketDiscarded) evento;
                        TNode nt = (TNode) epd.getSource();
                        Point p = nt.getScreenPosition();
                        if (epd.getPacketType() == TAbstractPDU.GPSRP) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_GOS_CAE), p.x, p.y+24, null);
                        } else if (epd.getPacketType() == TAbstractPDU.TLDP) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_LDP_CAE), p.x, p.y+24, null);
                        } else if (epd.getPacketType() == TAbstractPDU.IPV4) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4_CAE), p.x, p.y+24, null);
                        } else if (epd.getPacketType() == TAbstractPDU.IPV4_GOS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4_GOS_CAE), p.x, p.y+24, null);
                        } else if (epd.getPacketType() == TAbstractPDU.MPLS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS_CAE), p.x, p.y+24, null);
                        } else if (epd.getPacketType() == TAbstractPDU.MPLS_GOS) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS_GOS_CAE), p.x, p.y+24, null);
                        }
                    } else if (evento.getSubtype() == TSimulationEvent.LSP_ESTABLISHED) {
                        // Algo se har�.
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_GENERATED) {
                        TSimulationEventPacketGenerated epg = (TSimulationEventPacketGenerated) evento;
                        TNode nt = (TNode) epg.getSource();
                        Point p = nt.getScreenPosition();
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_GENERADO), p.x+8, p.y-16, null);
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_SENT) {
                        TSimulationEventPacketSent epe = (TSimulationEventPacketSent) evento;
                        TNode nt = (TNode) epe.getSource();
                        Point p = nt.getScreenPosition();
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_EMITIDO), p.x+24, p.y-16, null);
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_RECEIVED) {
                        TSimulationEventPacketReceived epr = (TSimulationEventPacketReceived) evento;
                        TNode nt = (TNode) epr.getSource();
                        Point p = nt.getScreenPosition();
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_RECIBIDO), p.x-8, p.y-16, null);
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_SWITCHED) {
                        TSimulationEventPacketSwitched epr = (TSimulationEventPacketSwitched) evento;
                        TNode nt = (TNode) epr.getSource();
                        Point p = nt.getScreenPosition();
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_CONMUTADO), p.x+40, p.y-16, null);
                    } else if (evento.getSubtype() == TSimulationEvent.PACKET_ROUTED) {
                        TSimulationEventPacketRouted epr = (TSimulationEventPacketRouted) evento;
                        TNode nt = (TNode) epr.getSource();
                        Point p = nt.getScreenPosition();
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_CONMUTADO), p.x+40, p.y-16, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        cerrojo.unLock();
    }

    
    /**
     * Este m�todo permite representar los eventos que tengan que ver con los nodos.
     * @since 2.0
     * @param g2D El lienzo donde se mostrar� el evento.
     */    
    public void dibujarEventosNodo(Graphics2D g2D) {
        cerrojo.lock();
        try {
            TSimulationEvent evento = null;
            Iterator it = bufferParaSimular.iterator();
            while (it.hasNext()) {
                evento = (TSimulationEvent) it.next();
                if (evento != null) {
                    if (evento.getSubtype() == TSimulationEvent.NODE_CONGESTED) {
                        TSimulationEventNodeCongested enc = (TSimulationEventNodeCongested) evento;
                        TNode nt = (TNode) enc.getSource();
                        Point p = nt.getScreenPosition();
                        int tipo = nt.getNodeType();
                        long cong = enc.getCongestionLevel();
                        if ((cong >= 50) && (cong < 75)) {
                            if (tipo == TNode.TRAFFIC_GENERATOR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.EMISOR_CONGESTIONADO_20), p.x, p.y, null);
                            } else if (tipo == TNode.TRAFFIC_SINK) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.RECEPTOR_CONGESTIONADO_20), p.x, p.y, null);
                            } else if (tipo == TNode.LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LER_CONGESTIONADO_20), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LERA_CONGESTIONADO_20), p.x, p.y, null);
                            } else if (tipo == TNode.LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSR_CONGESTIONADO_20), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSRA_CONGESTIONADO_20), p.x, p.y, null);
                            }
                        } else if ((cong >= 75) && (cong < 95)) {
                            if (tipo == TNode.TRAFFIC_GENERATOR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.EMISOR_CONGESTIONADO_60), p.x, p.y, null);
                            } else if (tipo == TNode.TRAFFIC_SINK) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.RECEPTOR_CONGESTIONADO_60), p.x, p.y, null);
                            } else if (tipo == TNode.LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LER_CONGESTIONADO_60), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LERA_CONGESTIONADO_60), p.x, p.y, null);
                            } else if (tipo == TNode.LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSR_CONGESTIONADO_60), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSRA_CONGESTIONADO_60), p.x, p.y, null);
                            }
                        } else if (cong >= 95) {
                            if (tipo == TNode.TRAFFIC_GENERATOR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.EMISOR_CONGESTIONADO), p.x, p.y, null);
                            } else if (tipo == TNode.TRAFFIC_SINK) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.RECEPTOR_CONGESTIONADO), p.x, p.y, null);
                            } else if (tipo == TNode.LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LER_CONGESTIONADO), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LER) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LERA_CONGESTIONADO), p.x, p.y, null);
                            } else if (tipo == TNode.LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSR_CONGESTIONADO), p.x, p.y, null);
                            } else if (tipo == TNode.ACTIVE_LSR) {
                                g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.LSRA_CONGESTIONADO), p.x, p.y, null);
                            }
                        }
                        if (nt.getTicksWithoutEmitting() > TNode.MAX_STEP_WITHOUT_EMITTING_BEFORE_ALERTING) {
                            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.TRABAJANDO), p.x, p.y, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        cerrojo.unLock();
    }

    
    /**
     * Este m�todo permite tratar los eventos que tienen que ver con los enlaces de
     * comunicaciones.
     * @since 2.0
     * @param g2D El lienzo donde se mostrar� el evento.
     */    
    public void dibujarEventosEnlace(Graphics2D g2D) {
        cerrojo.lock();
        try {
            TSimulationEvent evento = null;
            Iterator it = bufferParaSimular.iterator();
            while (it.hasNext()) {
                evento = (TSimulationEvent) it.next();
                if (evento != null) {
                    if (evento.getSubtype() == TSimulationEvent.LINK_BROKEN) {
                        TLink ent = (TLink) evento.getSource();
                        Point p = ent.getScreenPacketPosition(50);
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.ENLACE_CAIDO), p.x-41, p.y-41, null);
                    } else if (evento.getSubtype() == TSimulationEvent.LINK_RECOVERED) {
                        TLink ent = (TLink) evento.getSource();
                        Point p = ent.getScreenPacketPosition(50);
                        g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.ENLACE_RECUPERADO), p.x-41, p.y-41, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        cerrojo.unLock();
    }
    
    
    /**
     * Este m�todo muestra un contador en la ventana de simulaci�n que indica el
     * nanosegundo de simulaci�n que por el que se va.
     * @since 2.0
     * @param g2D El lienzo donde se mostrar� el contador.
     */    
    public void dibujarTicActual(Graphics2D g2D) {
        int posX = 8;
        int posY = 18;
        String textoTic = this.ticActual+ " " +java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JPanelSimulacion.Ns");
        FontMetrics fm = this.getFontMetrics(this.getFont());
        int anchoTexto = fm.charsWidth(textoTic.toCharArray(), 0, textoTic.length());
        g2Dbuf.setColor(Color.LIGHT_GRAY);
        g2Dbuf.fillRect(posX-2, posY-12, anchoTexto+6, 18);
        g2Dbuf.setColor(Color.WHITE);
        g2Dbuf.fillRect(posX-3, posY-13, anchoTexto+5, 17);
        g2Dbuf.setColor(Color.BLACK);
        g2Dbuf.drawString(textoTic, posX, posY);
        g2Dbuf.drawRect(posX-3, posY-13, anchoTexto+5, 17);
    }
    
    /**
     * Este m�todo dibuja en el panel de simulaci�n una leyenda con los objetos que
     * aparecer�n en la simulaci�n. Siempre, por supuesto, que la leyenda est�
     * configurada para aparecer.
     * @param g2D Lienzo del panel de simulaci�n, donde se dibujar� la leyenda.
     * @since 2.0
     */    
    public void dibujarLeyenda(Graphics2D g2D) {
        if (this.mostrarLeyenda) {
            int ancho = 0;
            int anchoTotal = 0;
            int alto = 0; 
            int xInicio = 0;
            int yInicio = 0;
            FontMetrics fm = this.getFontMetrics(this.getFont());
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_TLDP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_TLDP").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_TLDP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_TLDP").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_GPSRP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_GPSRP").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_GPSRP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_GPSRP").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.LSP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.LSP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.LSP_de_respaldo").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP_de_respaldo").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.LSP_de_respaldo").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP_de_respaldo").length());
            }
            if ((fm.charsWidth(this.translations.getString("Paquete_recibido").toCharArray(), 0, this.translations.getString("Paquete_recibido").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("Paquete_recibido").toCharArray(), 0, this.translations.getString("Paquete_recibido").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_enviado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_enviado").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_enviado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_enviado").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_conmutado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_conmutado").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_conmutado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_conmutado").length());
            }
            if ((fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_generado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_generado").length())) > ancho) {
                ancho = fm.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_generado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_generado").length());
            }
            anchoTotal = 5+13+5+ancho+20+13+5+ancho+5;
            alto = 113;
            xInicio = this.getWidth()-anchoTotal-6;
            yInicio = this.getHeight()-alto-6;
            g2D.setColor(Color.LIGHT_GRAY);
            g2D.fillRect(xInicio+2, yInicio+2, anchoTotal, alto);
            g2D.setColor(LEGEND_BACKGROUND_COLOR);
            g2D.fillRect(xInicio, yInicio, anchoTotal, alto);
            g2D.setColor(Color.BLACK);
            g2D.drawRect(xInicio, yInicio, anchoTotal, alto);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4), xInicio+5, yInicio+5, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_IPv4"), xInicio+23, yInicio+18);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_IPV4_GOS), xInicio+5, yInicio+23, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS"), xInicio+23, yInicio+36);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS), xInicio+5, yInicio+41, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_MPLS"), xInicio+23, yInicio+54);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_MPLS_GOS), xInicio+5, yInicio+59, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS"), xInicio+23, yInicio+72);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PDU_LDP), xInicio+5, yInicio+77, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_TLDP"), xInicio+23, yInicio+90);
            g2D.setColor(Color.LIGHT_GRAY);
            g2D.drawOval(xInicio+5, yInicio+95, 13, 13);
            g2D.setColor(Color.BLACK);
            g2D.fillOval(xInicio+8, yInicio+98, 7, 7);
            g2D.setColor(Color.RED);
            g2D.fillOval(xInicio+9, yInicio+99, 5, 5);
            g2D.setColor(Color.YELLOW);
            g2D.fillOval(xInicio+10, yInicio+100, 3, 3);
            g2D.setColor(Color.BLACK);
            g2D.fillOval(xInicio+11, yInicio+101, 1, 1);
            g2D.setColor(Color.BLACK);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_GPSRP"), xInicio+23, yInicio+108);
            xInicio = xInicio + 5 + 13 + 5 + ancho + 20 - 5;

            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_RECIBIDO), xInicio+5, yInicio+5, null);
            g2D.drawString(this.translations.getString("Paquete_recibido"), xInicio+23, yInicio+18);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_GENERADO), xInicio+5, yInicio+23, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_generado"), xInicio+23, yInicio+36);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_EMITIDO), xInicio+5, yInicio+41, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_enviado"), xInicio+23, yInicio+54);
            g2D.drawImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.PAQUETE_CONMUTADO), xInicio+5, yInicio+59, null);
            g2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_conmutado"), xInicio+23, yInicio+72);
            float dash1[] = {5.0f};
            BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
            g2Dbuf.setColor(this.LSP_COLOR);
            g2Dbuf.setStroke(dashed);
            g2D.drawLine(xInicio-5, yInicio+84, xInicio-5+30, yInicio+84);
            g2Dbuf.setColor(Color.BLACK);
            g2D.drawString(this.translations.getString("JPanelSimulacion.LSP"), xInicio+35, yInicio+90);
            float dash2[] = {10.0f, 5.0f, 0.2f, 5.0f};
            BasicStroke dashed2 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash2, 0.0f);
            g2Dbuf.setColor(Color.BLACK);
            g2Dbuf.setStroke(dashed2);
            g2D.drawLine(xInicio-5, yInicio+102, xInicio-5+30, yInicio+102);
            g2Dbuf.setColor(Color.BLACK);
            g2D.drawString(this.translations.getString("JPanelSimulacion.LSP_de_respaldo"), xInicio+35, yInicio+108);
            g2Dbuf.setStroke(new BasicStroke(1.0f));
        }
    }
    
    /**
     * Este m�todo permite obtener una representaci�n de la simulaci�n en un momento,
     * en forma de imagen bitmap.
     * @since 2.0
     * @return Una imagen accesible, cuyo contenido es una captura de la simulaci�n.
     */    
    public BufferedImage capturaDeDisenio() {
        if (imagenbuf == null) {
            imagenbuf = new BufferedImage(tamPantalla.width, tamPantalla.height, BufferedImage.TYPE_4BYTE_ABGR);
            g2Dbuf = imagenbuf.createGraphics();
        }
        prepararImagen(g2Dbuf);
        if (topologia != null) {
            dibujarDominio(g2Dbuf);
            dibujarEnlaces(g2Dbuf);
            dibujarEventosPaquete(g2Dbuf);
            dibujarNodos(g2Dbuf);
            dibujarEventosNodo(g2Dbuf);
            dibujarEventosEnlace(g2Dbuf);
            dibujarTicActual(g2Dbuf);
            dibujarLeyenda(g2Dbuf);
        }
        return imagenbuf;
    }

    /**
     * Este m�todo redibuja el panel de simulaci�n cada vez que es necesario.
     * @since 2.0
     * @param g El lienzo donde se debe redibujar el panel de simulaci�n.
     */    
    @Override
    public void paint(Graphics g) {
        BufferedImage ima = this.capturaDeDisenio();
        g.drawImage(ima, 0, 0, null);
    }

    /**
     * Este m�todo permite averiguar si el simulador est� mostrando la leyenda o no.
     * @return TRUE, si se est� mostrando la leyenda. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean obtenerMostrarLeyenda() {
        return this.mostrarLeyenda;
    }
    
    /**
     * Este m�todo permite establecer si el simulador mostrar� la leyenda o no.
     * @param ml TRUE, si el simulador debe mostrar la leyenda. FALSE en caso contrario.
     * @since 2.0
     */    
    public void ponerMostrarLeyenda(boolean ml) {
        this.mostrarLeyenda = ml;
    }
    
    private TImageBroker dispensadorDeImagenes;
    private BufferedImage imagenbuf;
    private Graphics2D g2Dbuf;
    private TTopology topologia;
    private Dimension tamPantalla;  
    private int maxX;
    private int maxY;
    private File ficheroTraza;
    private FileOutputStream streamFicheroTraza;
    private PrintStream streamTraza;
    private TreeSet bufferEventos;
    private TreeSet bufferParaSimular;
    private long ticActual;
    private TLock cerrojo;
    private int mlsPorTic;
    private boolean mostrarLeyenda;
    private ResourceBundle translations;
/*
    private static Color LEGEND_BACKGROUND_COLOR = new Color(255, 255, 255);
    private static Color LINK_NAME_COLOR = new Color(255, 255, 230);
    private static Color DOMAIN_BORDER_COLOR = new Color(128, 193, 255);
    private static Color DOMAIN_BACKGROUND_COLOR = new Color(204, 230, 255);
    private static Color LSP_COLOR = new Color(0, 0, 200);
*/
    private static Color LEGEND_BACKGROUND_COLOR = new Color(255, 255, 255);
    private static Color LINK_NAME_COLOR = new Color(255, 255, 230);
    private static Color DOMAIN_BORDER_COLOR = new Color(232, 212, 197);
    private static Color DOMAIN_BACKGROUND_COLOR = new Color(239, 222, 209);
    private static Color LSP_COLOR = new Color(0, 0, 200);
}
