/*
 * Copyright (c) 2017 Dukascopy (Suisse) SA. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Dukascopy (Suisse) SA or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. DUKASCOPY (SUISSE) SA ("DUKASCOPY")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL DUKASCOPY OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF DUKASCOPY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package jforex.plugin.strategies;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;


import com.dukascopy.api.Instrument;
import com.dukascopy.api.plugins.PluginGuiListener;
import com.dukascopy.api.plugins.widget.IPluginWidget;
import com.dukascopy.api.plugins.widget.PluginWidgetListener;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;

public class StrategyTableFromSdk {

    private static String jnlpUrl = "http://platform.dukascopy.com/demo/jforex.jnlp";
	private static String userName = "username";
	private static String password = "password";
	
    private static JFrame frame;
    private static UUID pluginId =null;
    
	public static void main(String[] args) throws Exception {
		final IClient client = ClientFactory.getDefaultInstance();
		connect(client);
		subscribeInstruments(client);
        pluginId = client.runPlugin(new StratTablePlugin(), null, getPluginGuiListener(client));
	}

	private static PluginGuiListener getPluginGuiListener(final IClient client) {
		return new PluginGuiListener(){

            @Override
            public void onWidgetAdd(IPluginWidget pluginWidget) {
                frame = buildPluginFrame(pluginWidget);
            }

            @Override
            public void onWidgetListenerAdd(final PluginWidgetListener listener){
                frame.addWindowListener(new WindowAdapter(){
                    @Override
                    public void windowClosing(WindowEvent e) {
                        listener.onWidgetClose();
                        client.stopPlugin(pluginId);
                        System.exit(0);
                    }
                });
            }

        };
	}

	private static void connect(IClient client) throws Exception {
		client.connect(jnlpUrl, userName, password);

		//wait for it to connect
		int i = 10; //wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			System.err.println("Failed to connect Dukascopy servers");
			System.exit(1);
		}
	}

	private static void subscribeInstruments(IClient client) {
		Set<Instrument> instruments = new HashSet<>();
		instruments.add(Instrument.EURUSD);
		System.out.println("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);
	}

	private static JFrame buildPluginFrame(IPluginWidget pluginWidget) {
		JFrame frame = new JFrame("Strategy table");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);

		JPanel panel = pluginWidget.getContentPanel();
		panel.setMinimumSize(new Dimension(600,100));
		panel.setPreferredSize(new Dimension(600,100));
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		return frame;
	}

}

