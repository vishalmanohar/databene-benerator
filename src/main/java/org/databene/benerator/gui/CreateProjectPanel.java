/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.benerator.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.databene.benerator.archetype.Archetype;
import org.databene.benerator.archetype.ArchetypeManager;
import org.databene.benerator.archetype.MavenFolderLayout;
import org.databene.benerator.main.DBSnapshotTool;
import org.databene.commons.ArrayUtil;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.ui.FileOperation;
import org.databene.commons.ui.FileTypeSupport;
import org.databene.commons.ui.I18NError;
import org.databene.commons.ui.I18NSupport;
import org.databene.commons.ui.swing.AlignedPane;
import org.databene.commons.ui.swing.ProgressMonitor;
import org.databene.commons.ui.swing.SwingUtil;
import org.databene.commons.ui.swing.delegate.PropertyCheckBox;
import org.databene.commons.ui.swing.delegate.PropertyComboBox;
import org.databene.commons.ui.swing.delegate.PropertyFileField;
import org.databene.commons.ui.swing.delegate.PropertyPasswordField;
import org.databene.commons.ui.swing.delegate.PropertyTextField;
import org.databene.jdbacl.JDBCDriverInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lets the user enter benerator project data and 
 * calls the ArchetypeBuilder for creating a new benerator project.<br/>
 * <br/>
 * Created at 17.07.2008 08:00:00
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class CreateProjectPanel extends JPanel {
	
	static final Logger logger = LoggerFactory.getLogger(CreateProjectPanel.class);
	
	private static final String SETUP_FILE = "setup.ser";

	private static final long serialVersionUID = 167461075459757736L;

	private static final int WIDE = 30;
	
	Setup setup;
	I18NSupport i18n;
	PropertyFileField folderField;
	JButton createButton;
	JComboBox archetypeField;
	JComboBox dbDriverTypeField;
	JTextField dbUrlField;
	JTextField dbDriverField;
	JTextField dbUserField;
	JTextField dbSchemaField;
	JTextField dbPasswordField;
	JComboBox dbSnapshotField;
	JButton testButton;
	PropertyFileField createTablesField;
	PropertyFileField dropTablesField;
	
	public CreateProjectPanel(I18NSupport i18n) {
		super(new BorderLayout());
		this.setup = loadOrCreateSetup();
		this.i18n = i18n;
		
		setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		add(createPropertiesPane(), BorderLayout.CENTER);
		add(createButtonPane(), BorderLayout.SOUTH);
		
		// Exit the application if 'Escape' is pressed
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private Component createButtonPane() {
		JPanel pane = new JPanel();

		createButton = createButton("create", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Creator()).start();
			}
		});
		pane.add(createButton);
		
		pane.add(createButton("cancel", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		}));
		
		return pane;
	}

	private Component createPropertiesPane() { // TODO v0.8 simplify using AlignedPropertyPane
		AlignedPane pane = AlignedPane.createVerticalPane(4);
		
		// project name
		createTextField("projectName", pane);
		folderField = new PropertyFileField(setup, "projectFolder", WIDE, 
				FileTypeSupport.directoriesOnly, FileOperation.save);
		folderField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File folder = CreateProjectPanel.this.folderField.getFile();
				if (! setup.isOverwrite() && folder.exists() && !FileUtil.isEmptyFolder(folder))
					showErrors(i18n.getString("error.projectFolderNotEmpty"));
			}
		});

		// archetype
		archetypeField = createComboBox("archetype", null, pane, (Object[]) ArchetypeManager.getInstance().getArchetypes());
		archetypeField.setRenderer(new ArchetypeRenderer());
		ArchetypeListener archetypeListener = new ArchetypeListener();
		archetypeField.addActionListener(archetypeListener);
		pane.endRow();

		// project folder
		pane.addRow(i18n.getString("projectFolder"), folderField);
		pane.addSeparator();
		
		// maven group id, version & options
		createTextField("groupId", pane);
		createTextField("version", pane);
		pane.endRow();

		pane.addSeparator();
			
		Box optionsPane = Box.createHorizontalBox();
		createCheckBox("eclipseProject", optionsPane);
		createCheckBox("overwrite", optionsPane);
		createCheckBox("offline", optionsPane);
		pane.addRow(i18n.getString("projectOptions"), optionsPane);

		pane.endRow();
		pane.addSeparator();

		
		// db properties
		dbDriverTypeField = createComboBox("jdbcDriverType", null, pane, JDBCDriverInfo.getInstances().toArray());
		dbDriverField = createTextField("dbDriver", pane);
		pane.endRow();
		
		dbUrlField = createTextField("dbUrl", pane);
		dbSchemaField = createTextField("dbSchema", pane);
		pane.endRow();

		dbUserField = createTextField("dbUser", pane);
		dbPasswordField = createPasswordField("dbPassword", pane);
		pane.endRow();
		
		pane.addElement(new JLabel(""));
		testButton = createButton("testConnection", new TestConnectionListener());
		pane.addElement(testButton);

		String[] supportedFormats = ArrayUtil.append("none", DBSnapshotTool.supportedFormats());
		dbSnapshotField = createComboBoxRow( "dbSnapshot", i18n, pane, (Object[]) supportedFormats);
		pane.endRow();

		// 'create/drop table' scripts
		createTablesField = new PropertyFileField(setup, "createScriptFile", 20, FileTypeSupport.filesOnly, FileOperation.open);
		pane.addElement(i18n.getString("createScriptFile"), createTablesField);
		dropTablesField = new PropertyFileField(setup, "dropScriptFile", 20, FileTypeSupport.filesOnly, FileOperation.open);
		pane.addElement(i18n.getString("dropScriptFile"), dropTablesField);
		pane.addSeparator();

		createTextField("encoding",      pane);
		createTextField("lineSeparator", pane);
		pane.endRow();

		createTextField("locale", pane);
		createTextField("dataset", pane);
		pane.endRow();
		pane.addSeparator();
		
		archetypeListener.actionPerformed(null);
		return pane;
	}

	private JCheckBox createCheckBox(String propertyName, Container pane) {
		PropertyCheckBox checkBox = new PropertyCheckBox(setup, propertyName, i18n.getString(propertyName));
		pane.add(checkBox);
		return checkBox;
	}

	private JComboBox createComboBoxRow(String propertyName, I18NSupport itemI18n, AlignedPane pane, Object... options) {
		JComboBox comboBox = createComboBox(propertyName, itemI18n, pane, options);
		pane.endRow();
		return comboBox;
	}

	private JComboBox createComboBox(String propertyName, I18NSupport itemI18n, AlignedPane pane, Object... options) {
	    JComboBox comboBox = new PropertyComboBox(setup, propertyName, itemI18n, propertyName + ".", options);
		String label = this.i18n.getString(propertyName);
		pane.addElement(label, comboBox);
	    return comboBox;
    }

	@SuppressWarnings("unused")
    private JTextField createTextFieldRow(String propertyName, AlignedPane pane) {
		JTextField textfield = new PropertyTextField(setup, propertyName, WIDE);
		String label = i18n.getString(propertyName);
		pane.addRow(label, textfield);
		return textfield;
	}
	
	private JTextField createTextField(String propertyName, AlignedPane pane) {
		JTextField textfield = new PropertyTextField(setup, propertyName, WIDE / 2);
		String label = i18n.getString(propertyName);
		pane.addElement(label, textfield);
		return textfield;
	}
	
	private JTextField createPasswordField(String propertyName, AlignedPane pane) {
		PropertyPasswordField pwfield = new PropertyPasswordField(setup, propertyName, WIDE / 2);
		String label = i18n.getString(propertyName);
		pane.addElement(label, pwfield);
		return pwfield;
	}
	
	private JButton createButton(String label, ActionListener listener) {
		JButton button = new JButton(i18n.getString(label));
		button.addActionListener(listener);
		return button;
	}
	
	public void exit() {
		saveSetup();
		JFrame frame = (JFrame) SwingUtilities.getRoot(this);
		frame.dispose();
		System.exit(0);
	}

	void showErrors(Object... errors) {
		String[] messages = new String[errors.length];
		for (int i = 0; i < errors.length; i++) {
			Object error = errors[i];
			if (error instanceof ZipException) {
				messages[i] = I18NError.renderMessage("ZipException", i18n);
			} else if (error instanceof I18NError) {
				messages[i] = ((I18NError) error).renderMessage(i18n);
			} else
				messages[i] = ToStringConverter.convert(error, "null");
		}
		JOptionPane.showMessageDialog(CreateProjectPanel.this, messages, "Error", JOptionPane.ERROR_MESSAGE);
	}

	
	class ArchetypeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
	        boolean useDB = setup.isDatabaseProject();
	        dbDriverTypeField.setEnabled(useDB);
        	dbUrlField.setEnabled(useDB);
        	dbDriverField.setEnabled(useDB);
        	dbUserField.setEnabled(useDB);
        	dbSchemaField.setEnabled(useDB);
        	dbPasswordField.setEnabled(useDB);
        	testButton.setEnabled(useDB);
        	dbSnapshotField.setEnabled(useDB);
        	createTablesField.setEnabled(useDB);
        	dropTablesField.setEnabled(useDB);
        	
        	boolean shop = setup.isShopProject();
        	if (shop) {
	        	dbSnapshotField.setEnabled(false);
	        	createTablesField.setEnabled(false);
	        	dropTablesField.setEnabled(false);
        	}
        }
	}
	
	class Creator implements Runnable {

		public void run() {
			try {
				logger.info("Creating project " + setup.getProjectName() + " " +
						"of type " + setup.getArchetype().getId() + " " +
								"in " + setup.getProjectFolder());
				createButton.setEnabled(false);
				String taskName = i18n.format("message.project.create", setup.getProjectName());
				String message = i18n.getString("message.project.initializing");
				ProgressMonitor monitor = new ProgressMonitor(null, taskName, message, 0, 100);
				monitor.setMillisToDecideToPopup(10);
				monitor.setMillisToPopup(10);
				ProjectBuilder builder = new ProjectBuilder(setup, new MavenFolderLayout(), monitor);
				builder.run();
				Exception[] errors = builder.getErrors();
				if (errors.length > 0)
					showErrors((Object[]) errors);
				else {
					JOptionPane.showMessageDialog(CreateProjectPanel.this, i18n.getString("message.done"));
					exit();
				}
			} catch (I18NError e) {
				e.printStackTrace();
				showErrors(e);
			} catch (Exception e) {
				e.printStackTrace();
				showErrors(e);
			} finally {
				createButton.setEnabled(true);
				SwingUtil.repaintLater(CreateProjectPanel.this);
			}
		}
		
	}

	class TestConnectionListener implements ActionListener {

        public void actionPerformed(ActionEvent actionevent) {
	        try {
	        	Class.forName(setup.getDbDriver());
	        	DriverManager.getConnection(setup.getDbUrl(), setup.getDbUser(), setup.getDbPassword());
	        	JOptionPane.showMessageDialog(CreateProjectPanel.this, i18n.getString("message.connect.successful"));
	        } catch (Exception e) {
	        	showErrors(e.toString());
	        }
        }
	}

	private Setup loadOrCreateSetup() {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(SETUP_FILE));
			return (Setup) in.readObject();
		} catch (Exception e) {
			// if no serialized setup exists or loading fails, simply create a new one
		    return new Setup();
		} finally {
			IOUtil.close(in);
		}
    }

	private void saveSetup() {
	    ObjectOutputStream out = null;
		try {
	        out = new ObjectOutputStream(new FileOutputStream(SETUP_FILE));
	        out.writeObject(setup);
        } catch (Exception e) {
	        e.printStackTrace();
        } finally {
        	IOUtil.close(out);
        }
    }
	
	public JButton getCreateButton() {
		return createButton;
	}
	
	@SuppressWarnings("serial")
    class ArchetypeRenderer extends DefaultListCellRenderer {
		
		private Map<Archetype, Icon> icons = new HashMap<Archetype, Icon>(20);
	
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
		        boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Archetype archetype = (Archetype) value;
			archetype.getIconURL();
			Icon icon = getIcon(archetype);
			setIcon(icon);
			setText(archetype.getDescription());
		    return this;
		}

		private Icon getIcon(Archetype archetype) {
			Icon icon = icons.get(archetype);
	        if (icon == null) {
	        	icon = new ImageIcon(archetype.getIconURL());
	        	icons.put(archetype, icon);
	        }
	        return icon;
        }
	}
	
}