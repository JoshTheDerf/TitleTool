package us.derfers.tribex.TitleTool;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Preferences {
	private static Text txtBlenderLocation;
	/**
	 * @return 
	 * @wbp.parser.entryPoint
	 */
	public static String[] Display(Display display, final String currentDir) {
		final String[] preferences = {readConfig(currentDir).get(0)};
		final Shell prefsshell = new Shell(display);
    	prefsshell.setText("Configure TitleTool");
		prefsshell.pack();
		prefsshell.setSize(404, 271);
		prefsshell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(prefsshell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		Label lblPathToBlender = new Label(composite, SWT.NONE);
		lblPathToBlender.setText("Path to Blender Executable: ");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		txtBlenderLocation = new Text(composite, SWT.BORDER);
		txtBlenderLocation.setText(preferences[0]);
		txtBlenderLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setText("Browse");
		btnBrowse.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final FileDialog fileDialog = new FileDialog(prefsshell, SWT.MULTI);

				String dir = fileDialog.open();
				if (dir != null) {
					txtBlenderLocation.setText(dir);
				}
			}

		});
		Label lblNote = new Label(prefsshell, SWT.NONE);
		lblNote.setText("NOTE:\n Browse to where your blender executable is\n (eg. blender.exe, blender.app, blender.bin) \n and select it. \n\nIf you do not, the program will hang\n and not render anything.");
		
		Button btnSave = new Button(prefsshell, SWT.NONE);
		btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSave.setText("Save");
		btnSave.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				preferences[0] = txtBlenderLocation.getText();
				writeConfig(preferences, currentDir);
				prefsshell.dispose();
			}

		});
		prefsshell.open();
		while (!prefsshell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return preferences;

	}
	
	public static void writeConfig(String[] preferences, String currentDir) {
		PrintWriter configFile = null;
		try {
			configFile = new PrintWriter(currentDir+"/config.txt", "UTF-8");
			configFile.print("##TitleTool Configuration File##\n");
			configFile.print("blenderExecutablePath = "+preferences[0]+"\n");
			configFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> readConfig(String currentDir) {
		ArrayList<String> preferences = new ArrayList<String>();
		Path configFile = FileSystems.getDefault().getPath(currentDir+"/config.txt");
		try(BufferedReader reader = Files.newBufferedReader(
		        configFile, Charset.defaultCharset())){
		  String line = "";
		  String contents = "";
		  while((line = reader.readLine()) != null){
		    contents = contents+"\n"+line;
		    if (!line.startsWith("#")) {
		    	String value = line.split("= ")[1];
		    	preferences.add(value);
		    }

		  }
		}catch(IOException exception){
		  System.out.println("Error while reading file");
		}
		
		return preferences;
		
	}
	
}



