package us.derfers.tribex.TitleTool;

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import us.derfers.tribex.TitleTool.Extractor;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class InstallPack {
	private static Text txtFileLocation;
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void Display(final String currentDirectory, Display display) {
		final Shell shlInstallTitlePack = new Shell(display);
		shlInstallTitlePack.setText("Install Title Pack");
		shlInstallTitlePack.setSize(521, 130);
		shlInstallTitlePack.setLayout(new GridLayout(3, false));
		new Label(shlInstallTitlePack, SWT.NONE);
		
		final Label lblErrlabel = new Label(shlInstallTitlePack, SWT.NONE);
		GridData gd_lblErrlabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblErrlabel.widthHint = 14;
		lblErrlabel.setLayoutData(gd_lblErrlabel);
		lblErrlabel.setText("   ");
		new Label(shlInstallTitlePack, SWT.NONE);
		lblErrlabel.setVisible(false);
		lblErrlabel.redraw();
		
		Label lblTitlePackLocation = new Label(shlInstallTitlePack, SWT.NONE);
		lblTitlePackLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitlePackLocation.setText("Title Pack Location:");

		txtFileLocation = new Text(shlInstallTitlePack, SWT.BORDER);
		txtFileLocation.setEditable(false);
		txtFileLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnBrowse = new Button(shlInstallTitlePack, SWT.NONE);
		btnBrowse.setText("Browse...");

		btnBrowse.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final FileDialog fileDialog = new FileDialog(shlInstallTitlePack, SWT.MULTI);

				String dir = fileDialog.open();
				if (dir != null) {

					txtFileLocation.setText(dir);
				}
			}

		});

		Button btnInstall = new Button(shlInstallTitlePack, SWT.NONE);
		btnInstall.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnInstall.setText("Install");
		btnInstall.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (!txtFileLocation.getText().isEmpty()) {
					try {
						Install(currentDirectory, txtFileLocation.getText());
						shlInstallTitlePack.dispose();
					} catch (Exception e) {
						e.printStackTrace();
						lblErrlabel.setText("File is not a zip file!");
					}
				} else {
					lblErrlabel.setVisible(true);
					lblErrlabel.setText("Please browse for a Title Pack zip file!");
					shlInstallTitlePack.layout();
					
				}
			}

		});
		new Label(shlInstallTitlePack, SWT.NONE);

		Button btnCancel = new Button(shlInstallTitlePack, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shlInstallTitlePack.dispose();
			}

		});
		shlInstallTitlePack.open();
		while (!shlInstallTitlePack.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void Install(String currentDirectory, String SourceFile) throws IOException {
		String zipFilePath = SourceFile;
		String destDirectory = currentDirectory+"/Titles/";
		Extractor extractor = new Extractor();
		extractor.unzip(zipFilePath, destDirectory);
		
	}
}
