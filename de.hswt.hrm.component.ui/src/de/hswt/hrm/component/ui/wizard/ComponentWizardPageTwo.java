package de.hswt.hrm.component.ui.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.inject.Inject;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.google.common.base.Optional;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import de.hswt.hrm.common.ui.swt.forms.FormUtil;
import de.hswt.hrm.common.ui.swt.layouts.LayoutUtil;
import de.hswt.hrm.common.ui.swt.utils.SWTResourceManager;
import de.hswt.hrm.component.model.Component;
import de.hswt.hrm.i18n.I18n;
import de.hswt.hrm.i18n.I18nFactory;
import de.hswt.hrm.scheme.service.ComponentConverter;

public class ComponentWizardPageTwo extends WizardPage {
    
    private static final I18n I18N = I18nFactory.getI18n(ComponentWizardPageTwo.class);
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	@Inject
	private IShellProvider shellProvider;
	
    private Optional<Component> component;

	private Text rlText;
	private Text udText;
	private Text lrText;
	private Text duText;

	private Label lrLabel;

	private Label rlLabel;

	private Label udLabel;

	private Label duLabel;

	/**
	 * Create the wizard.
	 */
	public ComponentWizardPageTwo(Optional<Component> component) {
		super(I18N.tr("Component Wizard"));
		setTitle(I18N.tr("Component Wizard"));
        this.component = component;
        setDescription(I18N.tr("Select files for the component."));
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setLayout(new GridLayout(2, true));
		
		Section lrSection = formToolkit.createSection(container, Section.TITLE_BAR);
		formToolkit.paintBordersFor(lrSection);
		lrSection.setText(I18N.tr("Left")+" "+I18N.tr("to")+" "+I18N.tr("right"));
		lrSection.setExpanded(true);
		lrSection.setLayoutData(LayoutUtil.createFillData());
		FormUtil.initSectionColors(lrSection);

		Composite lrComposite = new Composite(lrSection, SWT.NONE);
		formToolkit.adapt(lrComposite);
		formToolkit.paintBordersFor(lrComposite);
		lrSection.setClient(lrComposite);
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = 1;
		lrComposite.setLayout(gl);
		
		lrText = new Text(lrComposite, SWT.READ_ONLY);
		lrText.setLayoutData(LayoutUtil.createHorzCenteredFillData());
		formToolkit.adapt(lrText, true, true);
		
		Button lrButton = new Button(lrComposite, SWT.NONE);
		lrButton.setLayoutData(LayoutUtil.createRightGridData());
		formToolkit.adapt(lrButton, true, true);
		lrButton.setText(I18N.tr("Browse ..."));


		lrLabel = new Label(lrComposite, SWT.BORDER);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 100;
		gd.heightHint = 100;
		lrLabel.setLayoutData(gd);
		formToolkit.adapt(lrLabel, true, true);

		Section rlSection = formToolkit.createSection(container, Section.TITLE_BAR);
		formToolkit.paintBordersFor(rlSection);
		rlSection.setText(I18N.tr("Right")+" "+I18N.tr("to")+" "+I18N.tr("left"));
		rlSection.setExpanded(true);
		rlSection.setLayoutData(LayoutUtil.createFillData());
		FormUtil.initSectionColors(rlSection);

		Composite rlComposite = new Composite(rlSection, SWT.NONE);
		formToolkit.adapt(rlComposite);
		formToolkit.paintBordersFor(rlComposite);
		rlSection.setClient(rlComposite);
		gl = new GridLayout(2, false);
		gl.marginWidth = 1;
		rlComposite.setLayout(gl);
		
		rlText = new Text(rlComposite, SWT.READ_ONLY);
		rlText.setLayoutData(LayoutUtil.createHorzCenteredFillData());
		formToolkit.adapt(rlText, true, true);
		
		Button rlButton = new Button(rlComposite, SWT.NONE);
		rlButton.setLayoutData(LayoutUtil.createRightGridData());
		formToolkit.adapt(rlButton, true, true);
		rlButton.setText(I18N.tr("Browse ..."));


		rlLabel = new Label(rlComposite, SWT.BORDER);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 100;
		gd.heightHint = 100;
		rlLabel.setLayoutData(gd);
		formToolkit.adapt(rlLabel, true, true);

		Section udSection = formToolkit.createSection(container, Section.TITLE_BAR);
		formToolkit.paintBordersFor(udSection);
		udSection.setText(I18N.tr("Up")+" "+I18N.tr("to")+" "+I18N.tr("down"));
		udSection.setExpanded(true);
		udSection.setLayoutData(LayoutUtil.createFillData());
		FormUtil.initSectionColors(udSection);
		
		Composite udComposite = new Composite(udSection, SWT.NONE);
		formToolkit.adapt(udComposite);
		formToolkit.paintBordersFor(udComposite);
		udSection.setClient(udComposite);
		gl = new GridLayout(2, false);
		gl.marginWidth = 1;
		udComposite.setLayout(gl);
		
		udText = new Text(udComposite, SWT.READ_ONLY);
		udText.setLayoutData(LayoutUtil.createHorzCenteredFillData());
		formToolkit.adapt(udText, true, true);
		
		Button udButton = new Button(udComposite, SWT.NONE);
		udButton.setLayoutData(LayoutUtil.createRightGridData());
		formToolkit.adapt(udButton, true, true);
		udButton.setText(I18N.tr("Browse ..."));


		udLabel = new Label(udComposite, SWT.BORDER);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 100;
		gd.heightHint = 100;
		udLabel.setLayoutData(gd);
		formToolkit.adapt(udLabel, true, true);

		Section duSection = formToolkit.createSection(container, Section.TITLE_BAR);
		formToolkit.paintBordersFor(duSection);
		duSection.setText(I18N.tr("Down")+" "+I18N.tr("to")+" "+I18N.tr("up"));
		duSection.setExpanded(true);
		duSection.setLayoutData(LayoutUtil.createFillData());
		FormUtil.initSectionColors(duSection);
		
		Composite duComposite = new Composite(duSection, SWT.NONE);
		formToolkit.adapt(duComposite);
		formToolkit.paintBordersFor(duComposite);
		duSection.setClient(duComposite);
		gl = new GridLayout(2, false);
		gl.marginWidth = 1;
		duComposite.setLayout(gl);
		
		duText = new Text(duComposite, SWT.READ_ONLY);
		duText.setLayoutData(LayoutUtil.createHorzCenteredFillData());
		formToolkit.adapt(duText, true, true);
		
		Button duButton = new Button(duComposite, SWT.NONE);
		duButton.setLayoutData(LayoutUtil.createRightGridData());
		formToolkit.adapt(duButton, true, true);
		
		duButton.setText(I18N.tr("Browse ..."));
		
		duLabel = new Label(duComposite, SWT.BORDER);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 100;
		gd.heightHint = 100;
		duLabel.setLayoutData(gd);
		formToolkit.adapt(duLabel, true, true);
		
		setUploadButtons(duText, duLabel, duButton);
		setUploadButtons(udText, udLabel, udButton);
		setUploadButtons(lrText, lrLabel, lrButton);
		setUploadButtons(rlText, rlLabel, rlButton);
		
		
		if (component.isPresent()) {
			updateFields();
		}
		checkPageComplete();
	}

    private void updateFields() {
    	Component comp = component.get();
    	if(comp.getDownUpImage().isPresent()){
    		setPreviewImage(comp.getDownUpImage().get().getBlob(), duLabel);
    	}
    	if(comp.getRightLeftImage().isPresent()){
    		setPreviewImage(comp.getRightLeftImage().get().getBlob(), rlLabel);
    	}
    	if(comp.getLeftRightImage().isPresent()){
    		setPreviewImage(comp.getLeftRightImage().get().getBlob(), lrLabel);
    	}
    	if(comp.getUpDownImage().isPresent()){
    		setPreviewImage(comp.getUpDownImage().get().getBlob(), udLabel);
    	}
    }
    
    private void checkPageComplete() {
    	if(lrLabel.getImage() == null && rlLabel.getImage() == null && udLabel.getImage() == null
    			&& duLabel.getImage() == null){
    		setPageComplete(false);
    		return;    		
    	}
    	setPageComplete(true);

    }
    
    private void setUploadButtons(final Text path, final Label preview, Button upload){
    	upload.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FileDialog fileDialog = new FileDialog(shellProvider.getShell(), SWT.OPEN);
		        fileDialog.setText(I18N.tr("Open"));
		        String[] filterExt = { "*.pdf"};
		        fileDialog.setFilterExtensions(filterExt);
		        String selected = fileDialog.open();				
				if(selected != null){			        
			        path.setText(selected);
					try {
						setPreviewImage(selected, preview);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				checkPageComplete();
			}
		});    	
    }
    
    private void setPreviewImage(String path, Label preview) throws IOException{
    	File f = new File(path);
    	FileInputStream in;
    	byte[] data = null;
		try {
			in = new FileInputStream(f);
	    	data = new byte[in.available()];
	    	in.read(data);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

    	
		ByteBuffer buf = ByteBuffer.wrap(data);
		PDFFile pdffile;
		try {
			pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(0);
			
			Image imge = ComponentConverter.getSWTImage(preview.getDisplay(),ComponentConverter.renderImage(page, 100, 100));
	        preview.setImage(imge);
	    	preview.setSize( preview.computeSize( SWT.DEFAULT, SWT.DEFAULT ));

		} catch (IOException e) {
			e.printStackTrace();
		}	    	
    }
    
    private void setPreviewImage(byte[] data, Label preview){
		ByteBuffer buf = ByteBuffer.wrap(data);
		PDFFile pdffile;
		try {
			pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(0);
			
			Image imge = ComponentConverter.getSWTImage(preview.getDisplay(),ComponentConverter.renderImage(page, 100, 100));
	        preview.setImage(imge);
	    	preview.setSize( preview.computeSize( SWT.DEFAULT, SWT.DEFAULT ));

		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    private byte[] convertPDF(String path) throws IOException{
    	File f = new File(path);
    	FileInputStream in = null;
    	byte[] data = null;
		try {
			in = new FileInputStream(f);
	    	data = new byte[in.available()];
	    	in.read(data);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return data;
    }
    
    public byte[] getImageRL(){    	
		if(!rlText.getText().isEmpty()){
	    	try {
				return convertPDF(rlText.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;    	
    }
    public byte[] getImageLR(){    	
    	if(!lrText.getText().isEmpty()){
	    	try {
				return convertPDF(lrText.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return null;    	
    }
    public byte[] getImageDU(){    	
    	if(!duText.getText().isEmpty()){
	    	try {
				return convertPDF(duText.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return null;    	
    }
    public byte[] getImageUD(){    	
    	if(!udText.getText().isEmpty()){
	    	try {
				return convertPDF(udText.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return null;
    }
}
