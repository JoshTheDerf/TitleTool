package us.derfers.tribex.TitleTool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

public class OutputOptions {
	private static Text txtOutputfilename;
	private static String [] options = {"A", "B"};
		/**
		 * @wbp.parser.entryPoint
		 */
		public static String[] Display(final String currentDirectory, Display display, String outfilename, String quality) {
			final Shell shlOutputOptions = new Shell(display);
			shlOutputOptions.setSize(452, 188);
			shlOutputOptions.setText("Output Options");
			shlOutputOptions.setLayout(new GridLayout(1, false));
			
			Composite composite = new Composite(shlOutputOptions, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Label lblOutputFileName = new Label(composite, SWT.NONE);
			lblOutputFileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblOutputFileName.setText("Output File Name: ");
			
			txtOutputfilename = new Text(composite, SWT.BORDER);
			txtOutputfilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			txtOutputfilename.setText(outfilename);
			
			Composite composite_1 = new Composite(shlOutputOptions, SWT.NONE);
			composite_1.setLayout(new GridLayout(3, false));
			composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			
			Label lblRenderQuality = new Label(composite_1, SWT.NONE);
			lblRenderQuality.setText("Render Quality: (1-100)");
			
			final Spinner qualitySpinner = new Spinner(composite_1, SWT.BORDER);
			qualitySpinner.setMinimum(1);
			qualitySpinner.setSelection(Integer.valueOf(quality));
			qualitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			
			Label label = new Label(composite_1, SWT.NONE);
			label.setText("%");
			
			Composite composite_2 = new Composite(shlOutputOptions, SWT.NONE);
			composite_2.setLayout(new GridLayout(2, false));
			composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			
			Button btnSave = new Button(composite_2, SWT.NONE);
			btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			btnSave.setText("Save");
			btnSave.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event arg0) {
					options[0] = txtOutputfilename.getText();
					options[1] = qualitySpinner.getText();
					shlOutputOptions.dispose();
					
				}
			});
			
			Button btnCancel = new Button(composite_2, SWT.NONE);
			btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnCancel.setText("Cancel");
			
			btnCancel.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event arg0) {
					shlOutputOptions.dispose();
					
				}
				
			});
			
			shlOutputOptions.open();
			while (!shlOutputOptions.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			
			return options;
		}

}

