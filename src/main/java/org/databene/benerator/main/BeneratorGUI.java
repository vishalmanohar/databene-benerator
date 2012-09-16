/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator.main;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.databene.benerator.BeneratorError;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.LogCategories;
import org.databene.commons.SystemInfo;
import org.databene.commons.log.LoggingInfoPrinter;
import org.databene.commons.ui.ApplicationUtil;
import org.databene.commons.ui.JavaApplication;
import org.databene.commons.version.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Small Swing application for editing and running Benerator descriptors.<br/><br/>
 * Created: 31.05.2010 08:05:35
 * @since 0.6.3
 * @author Volker Bergmann
 */
@SuppressWarnings("serial")
public class BeneratorGUI {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(BeneratorGUI.class);
	
	static final File BUFFER_FILE = new File(BeneratorGUI.class.getSimpleName() + ".txt");
	
	public static void main(String[] args) throws Exception {
		ApplicationUtil.prepareNativeLAF("Benerator GUI");
		BeneratorGUIFrame appAndFrame = new BeneratorGUIFrame();
		ApplicationUtil.configureApplication(appAndFrame);
		appAndFrame.setVisible(true);
	}
	
	public static class BeneratorGUIFrame extends JFrame implements JavaApplication {
		
		JTextArea text;
		
		public BeneratorGUIFrame() throws IOException {
		    super("Benerator GUI");
		    Container contentPane = getContentPane();
		    text = new JTextArea();
		    if (BUFFER_FILE.exists())
		    	text.setText(IOUtil.getContentOfURI(BUFFER_FILE.getAbsolutePath()));
		    contentPane.add(new JScrollPane(text));
			createMenuBar();
		    setSize(600, 400);
		    addWindowListener(new WindowAdapter() {
		    	@Override
		    	public void windowClosing(WindowEvent e) {
		    	    exit();
		    	}
		    });
			setLocationRelativeTo(null);
	    }

		private void createMenuBar() {
		    JMenuBar menubar = new JMenuBar();
		    
		    // create file menu
		    JMenu fileMenu = new JMenu("File");
		    fileMenu.setMnemonic('F');
		    menubar.add(fileMenu);
		    
		    // create edit menu
		    JMenu editMenu = new JMenu("Edit");
		    editMenu.setMnemonic('E');
		    
		    createRunAction(editMenu);
		    menubar.add(editMenu);
		    
			setJMenuBar(menubar);
	    }

		private void createRunAction(JMenu editMenu) {
		    JMenuItem urlDecodeItem = editMenu.add(new RunAction());
		    urlDecodeItem.setAccelerator(
		    		KeyStroke.getKeyStroke('R',
		    	    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), 
		    	    false));
		    urlDecodeItem.setMnemonic('R');
	    }
		
		public void exit() {
			try {
		        String content = text.getText();
				IOUtil.writeTextFile(BUFFER_FILE.getAbsolutePath(), content);
	        } catch (IOException e) {
		        throw new RuntimeException(e);
	        } finally {
	        	System.exit(0);
	        }
		}

		private class RunAction extends AbstractAction {

			public RunAction() {
		        super("Run");
	        }

			public void actionPerformed(ActionEvent evt) {
				File file = null;
				try {
		            file = File.createTempFile("benerator-", ".ben.xml");
		            CharSequence builder = createXML();
		            IOUtil.writeTextFile(file.getAbsolutePath(), builder.toString());
		            Benerator.runFile(file.getAbsolutePath(), new LoggingInfoPrinter(LogCategories.CONFIG));
	            } catch (BeneratorError e) {
	        		System.err.println("Error: " + e.getMessage());
	            	LOGGER.error(e.getMessage());
	            } catch (Exception e) {
		            e.printStackTrace();
	            } finally {
	            	if (file != null)
	            		FileUtil.deleteIfExists(file);
	            }
	        }

			private CharSequence createXML() {
				if (text.getText().startsWith("<?xml"))
					return text.getText();
		        StringBuilder builder = new StringBuilder("<setup>");
		        builder.append(text.getText());
		        builder.append("</setup>");
		        return builder;
	        }
			
		}

		public void about() {
			JOptionPane.showMessageDialog(this, 
					"Benerator GUI " + VersionInfo.getInfo("benerator").getVersion() + SystemInfo.getLineSeparator() + 
					"(c) 2011 by Volker Bergmann");
        }

	}
	
}
