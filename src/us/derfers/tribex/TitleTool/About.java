package us.derfers.tribex.TitleTool;


import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class About {
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void Display(Display display) {
		Shell aboutshell = new Shell(display);
    	aboutshell.setText("About TitleTool");
		aboutshell.pack();
		aboutshell.setSize(404, 271);
		aboutshell.setLayout(new GridLayout(1, false));
		Image IconImage = loadImage("icon.png", true);
		Label Icon = new Label(aboutshell, SWT.CENTER);
		Label ProgName = new Label(aboutshell, SWT.CENTER);
		ProgName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ProgName.setText("TitleTool\n\n 0.2\n\n A program for creating cinematic titles quickly and easily.\n\n Copyright © 2013 TribeX Software Development");
		
		Icon.setImage(IconImage);
		Icon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		
		aboutshell.open();
		while (!aboutshell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}
	
	public static Image loadImage(String path, boolean inJar)
	{
	    Image newImage = null;

	    try
	    {
	        if(inJar)
	        {
	            newImage = new Image(null, TitleTool.class.getClassLoader().getResourceAsStream(path));
	        }
	        else
	        {
	            newImage = new Image(null, path);
	        }
	    }
	    catch(SWTException ex)
	    {
	        System.out.println("Couldn't find " + path);
	        ex.printStackTrace();
	    }

	    return newImage;
	}

}



