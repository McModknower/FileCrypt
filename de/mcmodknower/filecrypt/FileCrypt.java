package de.mcmodknower.filecrypt;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 *
 * Ein einfacher Dateiverschlüsseler
 *
 * @author McModknower
 */

public class FileCrypt extends Frame {
	private static final long serialVersionUID = -4458515117637023280L;
	// Anfang Attribute
	private TextField tfDateiname = new TextField();
	private TextField tfPasswort = new TextField();
	private TextArea taAnzeige = new TextArea("", 1, 1, TextArea.SCROLLBARS_BOTH);
	private Button bEntschlusseln = new Button();
	private Button bVerschlusseln1 = new Button();
	private JFileChooser fc = new JFileChooser();
	// Ende Attribute

	public FileCrypt() {
		// Frame-Initialisierung
		super();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
			}
		});
		int frameWidth = 304;
		int frameHeight = 332;
		setSize(frameWidth, frameHeight);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width - getSize().width) / 2;
		int y = (d.height - getSize().height) / 2;
		setLocation(x, y);
		setTitle("DateiSicherer");
		setResizable(true);
		Panel cp = new Panel(null);
		add(cp);
		// Anfang Komponenten

		tfDateiname.setBounds(8, 8, 233, 25);
		tfDateiname.setText("Dateiname");
		tfDateiname.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				FocusGained(evt);
			}
		});
		cp.add(tfDateiname);
		setUndecorated(false);
		tfPasswort.setBounds(8, 40, 273, 25);
		tfPasswort.setText("Passwort");
		tfPasswort.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				FocusGained(evt);
			}
		});
		cp.add(tfPasswort);
		taAnzeige.setBounds(8, 96, 273, 193);
		cp.add(taAnzeige);
		bEntschlusseln.setBounds(8, 72, 129, 17);
		bEntschlusseln.setLabel("Entschlüsseln");
		bEntschlusseln.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bEntschlusseln_ActionPerformed(evt);
			}
		});
		cp.add(bEntschlusseln);
		bVerschlusseln1.setBounds(144, 72, 129, 17);
		bVerschlusseln1.setLabel("Verschlüsseln");
		bVerschlusseln1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				bVerschlusseln1_ActionPerformed(evt);
			}
		});
		cp.add(bVerschlusseln1);
		fc.setDialogTitle("Datei Auswählen");
		fc.setAcceptAllFileFilterUsed(true);
		// Ende Komponenten

		setVisible(true);
	}

	// Anfang Methoden

	public static void main(String[] args) {
		new FileCrypt();
	}

	public void FocusGained(FocusEvent evt) {
		((TextComponent) evt.getSource()).selectAll();
	}

	private File file;
	private short[] Schluessel;
	private String text;

	private boolean readInputs() {
		if (tfDateiname.getText().length() == 0) {
			taAnzeige.setText("Der Dateiname darf nicht leer sein!");
			return false;
		}
		if (tfPasswort.getText().length() == 0) {
			taAnzeige.setText("Das Passwort darf nicht leer sein!");
			return false;
		}
		try {
			file = new File(tfDateiname.getText());
			if (!file.exists()) {
				file.createNewFile();
			}
			Schluessel = Crypting.getKey(tfPasswort.getText());
			return true;
		} catch (IOException e) {
			taAnzeige.setText("Dateifehler: " + e.getLocalizedMessage());
			text = "";
			Schluessel = null;
			return false;
		} catch (SecurityException e) {
			taAnzeige.setText("Der Zugriff auf die Datei wurde verweigert! (" + e.getLocalizedMessage() + ")");
			text = "";
			Schluessel = null;
			return false;
		}
	}

	public void bEntschlusseln_ActionPerformed(ActionEvent evt) {
		if (!readInputs())
			return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			text = "";
			while ((line = br.readLine()) != null) {
				text = text + line;
			}
			br.close();
		} catch (IOException e) {
			taAnzeige.setText("Dateifehler: " + e.getLocalizedMessage());
			text = "";
			Schluessel = null;
			return;
		}
		taAnzeige.setText(Crypting.decrypt(Schluessel, text));
		text = "";
		Schluessel = null;
	} // end of bEntschlusseln_ActionPerformed

	public void bVerschlusseln1_ActionPerformed(ActionEvent evt) {
		if (!readInputs())
			return;
		char[] ctext = Crypting.encrypt(Schluessel, taAnzeige.getText()).toCharArray();
		try {
			FileWriter bw = new FileWriter(file);
			bw.write(ctext);
			bw.close();
			taAnzeige.setText("Datei verschlüsselt gespeichert.");
			text = "";
			Schluessel = null;
		} catch (IOException e) {
			taAnzeige.setText("Dateifehler: " + e.getLocalizedMessage());
			text = "";
			Schluessel = null;
			return;
		} catch (SecurityException e) {
			taAnzeige.setText("Der Zugriff auf die Datei wurde verweigert! (" + e.getLocalizedMessage() + ")");
			text = "";
			Schluessel = null;
			return;
		}
	} // end of bVerschlusseln1_ActionPerformed

	public File jFileChooser1_openFile() {
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else {
			return null;
		}
	}

	// end Methods
} // end of class
