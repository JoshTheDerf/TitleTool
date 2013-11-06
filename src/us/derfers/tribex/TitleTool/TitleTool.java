package us.derfers.tribex.TitleTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Scale;
import org.apache.commons.lang3.*;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class TitleTool {
	private static String mode = "Dist";
	private static String version = "0.3";

	//GUI Globals
	private static Shell MainShell;
	public static Display display = Display.getDefault();
	private static Composite composite_5;
	private static Label lblTitle;
	private static Label lblDescription;
	private static Label lblImage;
	private static Tree Titletree;
	private static Label lblTitleList;
	private static Button btnRender;
	private static Composite composite;
	private static Button btnRefreshList;
	private static Label lblParameters;
	private static Scale previewScale;
	private static Composite composite_6;
	private static Label lblStFrame;
	private static Label lblEndFrame;
	//Holds all XML-Loaded widgets
	private static List<Widget> XmlObjects = new ArrayList<Widget>(); 
	private static String currentDirectory;
	//System Globals

	private static String currentTitle = "";
	private static String currentCategory = "";
	private static String startFrame = "1";
	private static String endFrame = "250";
	private static String outFileName = "";
	private static String renderQuality = "50";
	
	//Configure TitleTool Options
	private static String blenderExecutable = "blender";
	
	
	private static Button btnOutputOptions;

	//Main Method
	@SuppressWarnings("rawtypes")
	public static void main(String [] args) throws IOException {

		//DEBUG MODE: TESTING ONLY
		if (mode == "Debug") {
			currentDirectory = ".";
		} else {
			currentDirectory = getCWD();
		}

		MainShell = new Shell(display);
		MainShell.setSize(1010, 669);
		MainShell.setText("TitleTool - "+version);
		MainShell.setLayout(new GridLayout(1, false));

		ArrayList<String> config = Preferences.readConfig(currentDirectory);
		if (!config.isEmpty()) {
			blenderExecutable = config.get(0);
		} else {
			String[] prefs = {"a", "b"};
			prefs[0] = blenderExecutable;
			Preferences.writeConfig(prefs, currentDirectory);
		}

		//Test for Blender executable
		Boolean foundBlender = false;
		try {
			//Create a blender version process
			ProcessBuilder processBuilder = new java.lang.ProcessBuilder(blenderExecutable, "--version");
			java.util.Map env = processBuilder.environment();
			env.clear();
			//change the working directory
			processBuilder.directory(new java.io.File(currentDirectory+"/lib/"));

			// Start new process
			java.lang.Process p = null;
			p = processBuilder.start();
			
			//Read lines from process
			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line=reader.readLine();
			while(line!=null) 
			{ 
				System.out.println(line);
				//See if a line contains 'Blender 2.6'
				if (line.contains("Blender 2.6")) {
					foundBlender = true;
				}
				line=reader.readLine(); 
			}
		} catch (Exception e) {}

		//If no lines contained 'Blender 2.6'...
		if (foundBlender == false) {
			if (System.getProperty("os.name").contains("Windows")) {
				MessageBox alert = new MessageBox(MainShell);
				alert.setMessage("TitleTool has detected that Blender may not be in the system path. \nTo make this program work, set the \n"
						+ "Blender executable path to blender.exe in the location where you installed blender.\n\n");
				alert.open();
				String [] preferences = Preferences.Display(display, currentDirectory);
				blenderExecutable = preferences[0];
				
			} else if (System.getProperty("os.name").contains("OSX")) {
				MessageBox alert = new MessageBox(MainShell);
				alert.setMessage("TitleTool has detected that Blender may not be in the system path. \nTo make this program work, set the \n"
						+ "Blender executable path to blender.app in the location where you installed blender.\n\n");
				alert.open();
				String [] preferences = Preferences.Display(display, currentDirectory);
				blenderExecutable = preferences[0];
				
			} else {
				MessageBox alert = new MessageBox(MainShell);
				alert.setMessage("TitleTool has detected that Blender may not be in the system path. \nTo make this program work, set the \n"
						+ "Blender executable path to the blender executable in the location where you installed it.\n\n");
				alert.open();
				String [] preferences = Preferences.Display(display, currentDirectory);
				blenderExecutable = preferences[0];
			}
		}


final Composite composite_1 = new Composite(MainShell, SWT.NONE);
composite_1.setLayout(new GridLayout(1, false));
composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

final Composite composite_2 = new Composite(composite_1, SWT.NONE);
composite_2.setLayout(new GridLayout(2, false));
GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
gd_composite_2.widthHint = 693;
composite_2.setLayoutData(gd_composite_2);

Composite composite_3 = new Composite(composite_2, SWT.BORDER);
composite_3.setLayout(new GridLayout(1, false));
GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
gd_composite_3.widthHint = 277;
composite_3.setLayoutData(gd_composite_3);

lblTitleList = new Label(composite_3, SWT.NONE);
lblTitleList.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
lblTitleList.setText("Title List: ");

Titletree = new Tree(composite_3, SWT.BORDER);
GridData gd_Titletree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
gd_Titletree.widthHint = 259;
Titletree.setLayoutData(gd_Titletree);

populateTree();

//Make the composite area scroll properly
final ScrolledComposite scroller = new ScrolledComposite (composite_2, SWT.V_SCROLL | SWT.SCROLL_LINE | SWT.BORDER); 
scroller.setLayout(new GridLayout(1, false));
scroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
final Composite composite_4 = new Composite(scroller, SWT.NONE);
GridLayout gl_composite_4 = new GridLayout(1, false);
gl_composite_4.marginWidth = 3;
composite_4.setLayout(gl_composite_4);
composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
scroller.setContent(composite_4);
scroller.setExpandVertical(true);
scroller.setExpandHorizontal(true);

scroller.addControlListener(new ControlAdapter() {
	public void controlResized(ControlEvent e) {
		scroller.setMinSize(composite_4.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
});


composite_5 = new Composite(composite_4, SWT.NONE);
GridLayout gl_composite_5 = new GridLayout(1, false);
gl_composite_5.marginWidth = 0;
composite_5.setLayout(gl_composite_5);
GridData gd_composite_5 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
gd_composite_5.widthHint = 400;
composite_5.setLayoutData(gd_composite_5);

lblImage = new Label(composite_5, SWT.BORDER);
lblImage.setAlignment(SWT.CENTER);
lblImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

composite_6 = new Composite(composite_5, SWT.NONE);
composite_6.setLayout(new GridLayout(3, false));
composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

lblStFrame = new Label(composite_6, SWT.NONE);
GridData gd_lblStFrame = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
gd_lblStFrame.widthHint = 138;
lblStFrame.setLayoutData(gd_lblStFrame);
lblStFrame.setText("1");

previewScale = new Scale(composite_6, SWT.NONE);
previewScale.setSelection(1);
previewScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

lblEndFrame = new Label(composite_6, SWT.NONE);
lblEndFrame.setText("250");


lblTitle = new Label(composite_5, SWT.NONE);
lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
lblTitle.setFont(SWTResourceManager.getFont("Ubuntu", 14, SWT.BOLD));
lblTitle.setText("TITLENAME");


lblDescription = new Label(composite_5, SWT.NONE);
lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
lblDescription.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.ITALIC));
lblDescription.setText("DESCRIPTION");

lblParameters = new Label(composite_5, SWT.NONE);
lblParameters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
lblParameters.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.BOLD));
lblParameters.setText("Parameters: ");

btnRefreshList = new Button(composite_2, SWT.NONE);
btnRefreshList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
btnRefreshList.setText("Refresh List");

btnRefreshList.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		populateTree();
	}

});
composite = new Composite(composite_2, SWT.NONE);
composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
composite.setLayout(new GridLayout(2, false));

btnRender = new Button(composite, SWT.NONE);
btnRender.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
btnRender.setText("Render");

btnRender.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		setupRenderConfig(false, 0);
	}

});

previewScale.addListener(SWT.MouseUp, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		setupRenderConfig(true, previewScale.getSelection());
	}

});

previewScale.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		lblStFrame.setText("Preview Frame: "+Integer.toString(previewScale.getSelection()-Integer.valueOf(startFrame)+1));
	}

});

btnOutputOptions = new Button(composite, SWT.NONE);
btnOutputOptions.setText("Output Options");

btnOutputOptions.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		String[] options = OutputOptions.Display(currentDirectory, display, outFileName, renderQuality);
		if (options[0].length() > 0) {
			outFileName = options[0];
		} else {
			outFileName = currentTitle;
		}
		renderQuality = options[1];

	}

});

composite_5.setVisible(false);
btnRender.setVisible(false);
btnOutputOptions.setVisible(false);

Menu menu = new Menu(MainShell, SWT.BAR);
MainShell.setMenuBar(menu);

MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
mntmFile.setText("File");

Menu menu_1 = new Menu(mntmFile);
mntmFile.setMenu(menu_1);

MenuItem mntmInstallTitlePack = new MenuItem(menu_1, SWT.NONE);
mntmInstallTitlePack.setText("Install Title Pack");

mntmInstallTitlePack.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		InstallPack.Display(currentDirectory, display);
		populateTree();
	}

});

MenuItem mntmConfigureTitleTool = new MenuItem(menu_1, SWT.NONE);
mntmConfigureTitleTool.setText("Configure TitleTool");

mntmConfigureTitleTool.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		String [] preferences = Preferences.Display(display, currentDirectory);
		blenderExecutable = preferences[0];
		populateTree();
	}

});

MenuItem mntmQuit = new MenuItem(menu_1, SWT.NONE);
mntmQuit.setText("Quit");

mntmQuit.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		MainShell.dispose();
		display.dispose();
	}

});

MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
mntmHelp.setText("Help");

Menu menu_2 = new Menu(mntmHelp);
mntmHelp.setMenu(menu_2);

MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
mntmAbout.setText("About");

mntmAbout.addListener(SWT.Selection, new Listener() {

	@Override
	public void handleEvent(Event arg0) {
		About.Display(display);
	}

});

MainShell.open();

//Load with current variables from PopulateList()
if (Titletree.getChildren().length > 0) {
	LoadXML(currentTitle, currentCategory);
}

//Run Loop for resource consumption.
while (!MainShell.isDisposed()) {
	if (!display.readAndDispatch())
		display.sleep();
}
File previewDirectory = new File(currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/previews");
deleteDir(previewDirectory);
display.dispose();
}

public static void LoadXML(String titlePathName, String category) {
	outFileName = titlePathName;
	renderQuality = "50";
	//Remove any previous XMLObject widgets
	for (Widget widget : XmlObjects) {
		widget.dispose();
	}
	XmlObjects.clear();

	//CONFIGURATION: Define parent composite variable
	Composite parentComposite = composite_5;
	//END CONFIGURATION

	try {
		btnRender.setVisible(true);
		btnOutputOptions.setVisible(true);
		composite_5.setVisible(true);

		//XML File Loading
		File file = new File(currentDirectory+"/Titles/"+category+"/"+titlePathName+"/gui.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();

		//Get Root Element to make sure file is working
		NodeList rootElementList = doc.getDocumentElement().getChildNodes();
		//Loop through all children of the root element.
		for (int counter=0; counter < rootElementList.getLength(); counter++) {
			Node widgetNode = rootElementList.item(counter);

			if (widgetNode.getNodeType() == Node.ELEMENT_NODE) {
				Element widgetElement = (Element) widgetNode;


				//BUTTONS
				if (widgetElement.getNodeName().equals("button")) {
					//Get the button tags
					//Create a temporary button as a parent class Widget


					Button tempbutton = new Button(parentComposite, SWT.NONE);
					//Set style variables for temporary button
					tempbutton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					//Set button text with the content of the <button></button> tags
					tempbutton.setText(widgetElement.getTextContent());
					//Get the parameter value from the XML attribute and add it to the button's data, so that we know what to 
					//do with the button once the renderer is started
					tempbutton.setData("param", ((String) widgetElement.getAttributeNode("param").getNodeValue()));
					//Let the program know what type of widget this is
					tempbutton.setData("type", "button");
					//Append the button to the XMLObjects list to iterate through it later
					XmlObjects.add(tempbutton);
				} else if (widgetElement.getNodeName().equals("textarea")) {
					Element textAreaElement = (Element) widgetNode;

					Label templabel = new Label(parentComposite, SWT.NONE);
					templabel.setText(textAreaElement.getTextContent());

					Text temptextarea;
					//Create a temporary textarea as a parent class Widget
					if (textAreaElement.getAttributeNode("multiline") != null && textAreaElement.getAttributeNode("multiline").getNodeValue().equals("true")) {
						temptextarea = new Text(parentComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
						GridData textAreaHeight = new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1);
						textAreaHeight.heightHint = 50;
						temptextarea.setLayoutData(textAreaHeight);
					} else {
						temptextarea = new Text(parentComposite, SWT.BORDER);
						temptextarea.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));


					}
					//Set style variables for temporary textarea
					//Set textarea text with the content of the <textarea></textarea> tags
					if (textAreaElement.getAttributeNode("value") != null) {
						temptextarea.setText((String) textAreaElement.getAttributeNode("value").getNodeValue());
					}
					//Get the parameter value from the XML attribute and add it to the textarea's data, so that we know 
					//what to do with the textarea once the renderer is started
					temptextarea.setData("param", ((String) textAreaElement.getAttributeNode("param").getNodeValue()));
					//Let the program know what type of widget this is
					temptextarea.setData("type", "textarea");

					//Append the textarea to the XMLObjects list to iterate through it later
					XmlObjects.add(templabel);
					XmlObjects.add(temptextarea);


				} else if (widgetElement.getNodeName().equals("combo")) {
					//COMBOBOX
					Element comboElement = (Element) widgetNode;
					//Create a temporary composite for sub-elements
					Composite tempcomposite = new Composite(parentComposite, SWT.NONE);
					tempcomposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					tempcomposite.setLayout(new GridLayout(2, false));


					//Create a temporary combobox as a parent class Widget
					Combo tempcombo;

					if (comboElement.getAttributeNode("description") != null) {
						Label templabel = new Label(tempcomposite, SWT.NONE);
						templabel.setText(comboElement.getAttributeNode("description").getNodeValue());
						XmlObjects.add(templabel);

					}

					if (comboElement.getAttributeNode("readonly") != null && comboElement.getAttributeNode("readonly").getNodeValue().equals("true")) {
						tempcombo = new Combo(tempcomposite, SWT.NONE | SWT.READ_ONLY);
						tempcombo.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));

					} else {
						tempcombo = new Combo(tempcomposite, SWT.NONE);
						tempcombo.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));


					}
					NodeList comboElementList = comboElement.getElementsByTagName("option");
					for (int i = 0; i < comboElementList.getLength(); i++) {
						tempcombo.add(comboElementList.item(i).getTextContent());
						if (tempcombo.getSelectionIndex() < 0) {
							tempcombo.select(0);
						}
						if (((Element) comboElementList.item(i)).getAttributeNode("selected") != null && (((Element) comboElementList.item(i)).getAttributeNode("selected").getNodeValue().equals("true"))) {
							tempcombo.select(i);
						}
					}
					//Set style variables for temporary textarea
					//Set combobox text with the content of the <combo></combo> tags
					if (comboElement.getAttributeNode("value") != null) {
						tempcombo.setText(comboElement.getAttributeNode("value").getNodeValue());
					}

					//Get the parameter value from the XML attribute and add it to the textarea's data, so that we know 
					//what to do with the textarea once the renderer is started
					tempcombo.setData("param", ((String) comboElement.getAttributeNode("param").getNodeValue()));
					tempcombo.setData("type", "combo");

					//Append the combobox to the XMLObjects list to iterate through it later
					XmlObjects.add(tempcombo);
					XmlObjects.add(tempcomposite);


				} else if (widgetElement.getNodeName().equals("color")) {
					//COLOR
					final Element colorElement = (Element) widgetNode;
					//Create new composite for sub-elements
					Composite tempcomposite = new Composite(parentComposite, SWT.NONE);
					tempcomposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					tempcomposite.setLayout(new GridLayout(2, false));

					//Add a temporary label
					final Label templabel = new Label(tempcomposite, SWT.NONE);
					templabel.setText(colorElement.getTextContent());

					//Create a temporary color widget as a parent class Widget
					final Button tempcolor = new Button(tempcomposite, SWT.FLAT);
					tempcolor.setText("                        ");

					//Listener to set the button color when added.
					tempcolor.addListener(SWT.Selection, new Listener() {

						@Override
						public void handleEvent(Event arg0) {
							//Popup a color selection dialog
							ColorDialog colorDialog = new ColorDialog(MainShell);
							colorDialog.setText(colorElement.getTextContent());
							RGB selectedRGB = colorDialog.open();
							//Get the color and set the background color of the color widget
							if (selectedRGB != null) {
								Color selectedColor = new Color(display, selectedRGB);
								tempcolor.setBackground(selectedColor);
								for (Widget widget : XmlObjects) {
									if (widget.getData("type") != null && !widget.getData("type").equals("color")) {
										((Control) widget).setFocus();
										break;
									}
								}
							}
						}

					});

					if (colorElement.getAttributeNode("default") != null) {
						Color color = stringToRGB("rgb("+colorElement.getAttributeNode("default").getNodeValue()+")");
						tempcolor.setBackground(color);


					}

					//Set style variables for temporary textarea
					//Set color widget text with the content of the <color></color> tags
					if (colorElement.getAttributeNode("value") != null) {
						tempcolor.setText(colorElement.getAttributeNode("value").getNodeValue());
					}

					//Get the parameter value from the XML attribute and add it to the textarea's data, so that we know 
					//what to do with the textarea once the renderer is started
					tempcolor.setData("param", ((String) colorElement.getAttributeNode("param").getNodeValue()));
					tempcolor.setData("type", "color");

					//Append the color widget to the XMLObjects list to iterate through it later
					XmlObjects.add(tempcolor);
					XmlObjects.add(templabel);
					XmlObjects.add(tempcomposite);

				} else if (widgetElement.getNodeName().equals("separator") || widgetElement.getNodeName().equals("seperator") ) {
					//SEPARATOR

					Label separator = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
					separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

					XmlObjects.add(separator);

				} else if (widgetElement.getNodeName().equals("label")) {
					//SEPARATOR
					final Element labelElement = (Element) widgetNode;

					Label templabel = new Label(parentComposite, SWT.NONE);
					templabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					templabel.setText(labelElement.getTextContent());

					XmlObjects.add(templabel);

				} else if (widgetElement.getNodeName().equals("spinner")) {
					//SPINNER
					final Element spinnerElement = (Element) widgetNode;
					//Create a new composite for sub-elements
					Composite tempcomposite = new Composite(parentComposite, SWT.NONE);
					tempcomposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					tempcomposite.setLayout(new GridLayout(2, false));
					final Label templabel = new Label(tempcomposite, SWT.NONE);
					templabel.setText(spinnerElement.getTextContent());
					Spinner tempspinner = new Spinner(tempcomposite, SWT.BORDER);
					tempspinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

					if (spinnerElement.getAttributeNode("value") != null) {
						tempspinner.setSelection(Integer.valueOf(spinnerElement.getAttributeNode("value").getNodeValue()));
					}

					if (spinnerElement.getAttributeNode("max") != null) {
						tempspinner.setMaximum(Integer.valueOf(spinnerElement.getAttributeNode("max").getNodeValue()));
					}

					if (spinnerElement.getAttributeNode("min") != null) {
						tempspinner.setMinimum(Integer.valueOf(spinnerElement.getAttributeNode("min").getNodeValue()));
					}

					tempspinner.setData("param", ((String) spinnerElement.getAttributeNode("param").getNodeValue()));

					XmlObjects.add(tempspinner);
					XmlObjects.add(templabel);
					XmlObjects.add(tempcomposite);
				}

			}
		}

		//END WIDGETS
		//Get all <name> tags <-[Heheeeee.. Name tags, get it!]
		NodeList nodeLst = doc.getElementsByTagName("name");

		//Iterate through the name tags (in case there are more then one, use the latest one.
		for (int s = 0; s < nodeLst.getLength(); s++) {

			//Convert list to node
			Node nameNode = nodeLst.item(s);

			//Make sure the node is valid
			if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
				//Get the element from the node
				Element nameElmnt = (Element) nameNode;
				//Set the Title of the Title to the value of the element
				lblTitle.setText(nameElmnt.getTextContent());
			}

		}

		//Description rendering

		nodeLst = doc.getElementsByTagName("description");

		for (int s = 0; s < nodeLst.getLength(); s++) {

			Node fstNode = nodeLst.item(s);

			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
				Element fstElmnt = (Element) fstNode;
				lblDescription.setText("Description: "+fstElmnt.getTextContent());
			}

		}

		//Image Rendering
		Image image = (Image) SWTResourceManager.getImage(currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/thumb.png");
		ImageData imgData = image.getImageData();
		imgData = imgData.scaledTo(400, 200);
		Image scaledImage = new Image(display, imgData);
		lblImage.setImage(scaledImage);

		//Set frame variables
		//Start Frame
		nodeLst = doc.getElementsByTagName("start-frame");
		if (nodeLst.getLength() > 0) {
			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					startFrame = fstElmnt.getTextContent();
				}

			}
		} else {
			startFrame = "1";
		}

		//Start Frame
		nodeLst = doc.getElementsByTagName("end-frame");
		if (nodeLst.getLength() > 0) {
			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					endFrame = fstElmnt.getTextContent();
				}

			}
		} else {
			endFrame = "250";
		}

		lblStFrame.setText("Preview Frame: 1");
		lblEndFrame.setText(Integer.toString(Integer.valueOf(endFrame)-Integer.valueOf(startFrame)+1));
		previewScale.setMinimum(Integer.valueOf(startFrame));
		previewScale.setMaximum(Integer.valueOf(endFrame));


		((ScrolledComposite) parentComposite.getParent().getParent()).setMinSize(parentComposite.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		parentComposite.redraw();
		parentComposite.layout(true);
		parentComposite.getParent().redraw();
		parentComposite.getParent().layout(true);
		previewScale.setSelection(1);

	} catch (Exception e) {
		MessageBox alert = new MessageBox(MainShell);
		alert.setMessage("Error, config.xml for the "+currentTitle+" title is invalid!\nDetails:\n"+e.fillInStackTrace()+"\n\n Unexpected program behavior may result, please restart application.");
		alert.open();
	}
}

public static Color stringToRGB (String input) 
{
	Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
	Matcher m = c.matcher(input);

	if (m.matches()) 
	{
		return new Color(display, Integer.valueOf(m.group(1)),  // r
				Integer.valueOf(m.group(2)),  // g
				Integer.valueOf(m.group(3))); // b 
	} else {
		System.out.println("Error, incompatable color.");
	}
	return null;  
}

public static void setupRenderConfig(Boolean preview, int inframe) {
	String towrite = "params = {\n\t";
	for (Widget widget : XmlObjects) {
		if (widget.getData("type") != null) {
			if (widget.getData("type").equals("textarea")) {
				Text specificWidget = (Text) widget;
				String escapedText = StringEscapeUtils.escapeJava(specificWidget.getText());
				towrite = towrite+"'"+specificWidget.getData("param")+"' : '"+escapedText+"',\n\t";
			} else if (widget.getData("type").equals("color")) {
				Button specificWidget = (Button) widget;

				System.out.println(Float.valueOf(specificWidget.getBackground().getRed())/255.0);

				towrite = towrite+"\'"+specificWidget.getData("param")+"' : "
						+ "("+Float.valueOf(specificWidget.getBackground().getRed())/255.0+", "+Float.valueOf(specificWidget.getBackground().getGreen())/255.0+", "+Float.valueOf(specificWidget.getBackground().getBlue())/255.0+"),\n\t";
			} else if (widget.getData("type").equals("combo")) {
				Combo specificWidget = (Combo) widget;
				String escapedText = StringEscapeUtils.escapeJava(specificWidget.getText());
				towrite = towrite+"'"+specificWidget.getData("param")+"' : '"+escapedText+"',\n\t";
			} else if (widget.getData("type").equals("spinner")) {
				Spinner specificWidget = (Spinner) widget;
				String escapedText = Integer.toString(specificWidget.getSelection());
				towrite = towrite+"'"+specificWidget.getData("param")+"' : "+escapedText+",\n\t";
			}

		}
	}

	//DO NOT REMOVE: For bootstrap python file
	towrite = towrite + "'__Category' : '"+currentCategory+"',\n\t";
	towrite = towrite + "'__TitleName' : '"+currentTitle+"',\n\t";
	towrite = towrite + "'__OutFileName' : '"+outFileName+"',\n\t";
	towrite = towrite + "'__Resolution' : '"+renderQuality+"',\n\t";




	if (preview == false) {
		towrite = towrite + "'animation' : True\n";
		towrite = towrite + "# END OF PARAMS \n}";
	} else {
		towrite = towrite + "'animation' : False\n";
		towrite = towrite + "# END OF PARAMS \n}";
	}


	PrintWriter configFile = null;
	try {
		configFile = new PrintWriter(currentDirectory+"/lib/bootstrapConfig.py", "UTF-8");
		configFile.print(towrite);
		configFile.close();
		try {
			doRender(preview, inframe);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
}
public static void doRender(final Boolean preview, final int inframe) throws InterruptedException, IOException {
	Thread renderThread = new Thread( new Runnable() {
		public Boolean doPreview = preview;
		public String frame = Integer.toString(inframe);

		@SuppressWarnings("rawtypes")
		@Override
		public void run() {
			java.lang.ProcessBuilder processBuilder = null;
			try {
				if (doPreview == true) {
					processBuilder = new java.lang.ProcessBuilder(blenderExecutable, "-b", currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/title.blend", "-P", currentDirectory+"/lib/titleBootstrap.py", "-F", "PNG", "-o", "//previews/####.png", "-f", frame);
				} else {
					processBuilder = new java.lang.ProcessBuilder(blenderExecutable, "-b", currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/title.blend", "-P", currentDirectory+"/lib/titleBootstrap.py");

				}
				// Create an environment (shell variables)
				java.util.Map env = processBuilder.environment();
				env.clear();
				//change the working directory
				processBuilder.directory(new java.io.File(currentDirectory+"/lib/"));

				// Start new process
				java.lang.Process p = null;
				p = processBuilder.start();
				BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line=reader.readLine();
				String [] currframe = null;
				while(line!=null) 
				{ 
					System.out.println(line);
					if (line.contains(":")) {
						currframe = line.split(":");
					} else if (line.contains("Append")) {
						currframe = line.split(" ");
					}
					if (currframe.length > 1) {
						if (preview == false) {
							if (currframe[0].contains("Append")) {
								updateCurrFrame("NULL");
							} else {
								updateCurrFrame(currframe[1]);

							}
						}
					}
					line=reader.readLine(); 

				}
				updateRenderBtn();

			} catch (IOException e) {
				e.printStackTrace();
				btnRender.setEnabled(true);
				btnRender.setGrayed(false);
				previewScale.setEnabled(true);
				btnRender.setText("Error: Blender Executable not found.");
				String [] preferences = Preferences.Display(display, currentDirectory);
				blenderExecutable = preferences[0];
			} 

		}


		public void updateRenderBtn() {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					btnRender.setEnabled(true);
					btnRender.setGrayed(false);
					previewScale.setEnabled(true);
					Integer framelength = frame.length();
					if (framelength == 4) {
						System.out.println("Not modifying frame var.");
					} else if (framelength == 3) {
						frame = "0"+frame;
					} else if (framelength == 2) {
						frame = "00"+frame;
					} else if (framelength == 1) {
						frame = "000"+frame;
					}
					if (doPreview = true && new File(currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/previews/"+frame+".png").exists()) {
						SWTResourceManager.disposeImages();

						Image image = (Image) SWTResourceManager.getImage(currentDirectory+"/Titles/"+currentCategory+"/"+currentTitle+"/previews/"+frame+".png");
						ImageData imgData = image.getImageData();
						imgData = imgData.scaledTo(400, 200);
						Image scaledImage = new Image(display, imgData);
						lblImage.setImage(scaledImage);

					}
				}
			});
		}

		public void updateCurrFrame(final String inframe) {
			display.syncExec(new Runnable() {
				public String frame = inframe;
				@Override
				public void run() {
					if (frame != null && !inframe.contains("NULL")) {
						frame = frame.replaceAll("[^0-9]", "");
						if (!frame.equals("") && !frame.isEmpty() && frame != null && !frame.equals("00") && Integer.valueOf(frame)-(Integer.valueOf(startFrame)) <= Integer.valueOf(endFrame)-Integer.valueOf(startFrame)) {
							btnRender.setText("Currently rendering frame "+Integer.toString(Integer.valueOf(frame)+1-(Integer.valueOf(startFrame)))+" out of "+Integer.toString(Integer.valueOf(endFrame)+1-Integer.valueOf(startFrame))+".");
						} else {
							btnRender.setText("Render (Previously Rendered)");
						}
					} else if (inframe.contains("NULL")) {
						//Do nothing
					} else {
						btnRender.setText("Render (Previously Rendered)");
					}
				}
			});
		}

	});
	btnRender.setEnabled(false);
	btnRender.setGrayed(true);
	previewScale.setEnabled(false);
	renderThread.start();



	// Get runtime

}

public static void deleteDir(File f) throws IOException {
	if (f.isDirectory()) {
		for (File c : f.listFiles()) {
			deleteDir(c);
		}
	}
	if (!f.delete())
		System.out.println("Failed to delete directory: " + f + ". [Does not exist]");
}

public static String getCWD() {
	File file = new File("");
	try {
		file = new File(URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return file.toString();
}


//Populate Title Tree
public static void populateTree() {
	String filedelimiter = "/";
	if (System.getProperty("os.name").contains("Windows")) {
		filedelimiter = "\\\\";
	}
	Titletree.removeAll();
	int catcounter = 0;

	TreeItem catItem;
	TreeItem titleItem;
	Path dir = FileSystems.getDefault().getPath(currentDirectory+"/Titles");
	try {
		DirectoryStream<Path> stream = Files.newDirectoryStream( dir );
		for (Path path : stream) {
			if (path.toFile().isDirectory()) {
				String [] tempCatList = path.toString().split(filedelimiter);
				String CatName = tempCatList[tempCatList.length-1];
				//Iterator to generate Tree

				catItem = new TreeItem(Titletree, SWT.NONE, catcounter);
				catItem.setText(CatName.replace('_', ' '));

				catcounter++;
				int itemcounter = 0;

				DirectoryStream<Path> catStream = Files.newDirectoryStream(path);
				for (Path tpath : catStream) {
					String [] tempNameList = tpath.toString().split(filedelimiter);
					String TitleName = tempNameList[tempNameList.length-1];
					if (tpath.toFile().isDirectory()) {
						//Iterator to generate Tree

						titleItem = new TreeItem(catItem, SWT.NONE, itemcounter);
						titleItem.setText(TitleName.replace('_', ' '));
						Image image = (Image) SWTResourceManager.getImage(tpath.toString()+"/thumb.png");
						ImageData imgData = image.getImageData();
						imgData = imgData.scaledTo(100, 50);
						Image scaledImage = new Image(display, imgData);
						titleItem.setImage(scaledImage);
						titleItem.setData("title", TitleName);
						titleItem.setData("category", CatName);
						itemcounter++;

					}
					if (catcounter == 1 && itemcounter == 1) {
						currentTitle = TitleName;
						currentCategory = CatName;
					}
				}
			}
		}
		stream.close();
	} catch (Exception e){
		e.printStackTrace();
	}

	Titletree.addMouseListener(new MouseListener() {

		@Override
		public void mouseDoubleClick(MouseEvent arg0) {
			Tree obj = (Tree) arg0.getSource();
			TreeItem[] selected = obj.getSelection();
			if(selected[0].getExpanded() == true) {
				selected[0].setExpanded(false);
			} else if (selected[0].getExpanded() == false) {
				selected[0].setExpanded(true);
			}
		}

		@Override
		public void mouseUp(MouseEvent arg0) {
			//Trace selection point to get Data
			Tree obj = (Tree) arg0.getSource();
			TreeItem[] selected = obj.getSelection();

			//Load selected title and delete previous title's preview directory
			if (selected.length > 0 && (String) selected[0].getData("title") != null) {
				File previewDirectory = new File(currentDirectory+"/Titles/"+selected[0].getData("category")+"/"+currentTitle+"/previews");
				try {
					deleteDir(previewDirectory);
				} catch (IOException e1) {}

				String title = (String) selected[0].getData("title");
				String category = (String) selected[0].getData("category");

				currentCategory = category;
				currentTitle = title;
				LoadXML(title, category);
			}
		}

		//Function that must exist, but I have no use for.
		@Override
		public void mouseDown(MouseEvent arg0) {}

	});
}
}