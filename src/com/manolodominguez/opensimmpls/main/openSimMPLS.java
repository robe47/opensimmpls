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
package com.manolodominguez.opensimmpls.main;

import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import com.manolodominguez.opensimmpls.gui.simulator.JOpenSimMPLS;
import com.manolodominguez.opensimmpls.gui.splash.JSplash;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * This class implements a new OpenSimMPLS network simulator. This is the main
 * class, that should be executed to run the simulator.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class openSimMPLS {

    /**
     * This method start OpenSimMPLS.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param args Arguments specified at command line.
     * @since 2.0
     */
    public static void main(String args[]) {
        try {
            boolean nimbusSet = false;
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusSet = true;
                    break;
                }
            }
            if (!nimbusSet) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("An error happened when starting OpenSimMPLS");
        }
        splash = new JSplash();
        SwingUtilities.invokeLater(() -> {
            splash.setVisible(true);
        });
        splash.setMessage(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Loading_icons..."));
        imagesBroker = TImageBroker.getInstance();
        splash.setMessage(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("openSimMPLS.generandoInterfaz"));
        simulator = new JOpenSimMPLS(imagesBroker);
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        simulator.setBounds(0, 0, tamPantalla.width, tamPantalla.height);
        simulator.setVisible(true);
        splash.dispose();
    }

    // Variables declaration - do not modify
    private static TImageBroker imagesBroker;
    private static JSplash splash;
    private static JOpenSimMPLS simulator;
}
