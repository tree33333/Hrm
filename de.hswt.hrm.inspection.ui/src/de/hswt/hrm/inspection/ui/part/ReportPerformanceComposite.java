package de.hswt.hrm.inspection.ui.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.List;

import de.hswt.hrm.common.ui.swt.forms.FormUtil;
import de.hswt.hrm.common.ui.swt.layouts.LayoutUtil;
import de.hswt.hrm.component.model.Component;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

public class ReportPerformanceComposite extends AbstractComponentRatingComposite {

	// TODO remove unused injections, add others as needed
	// TODO use this if it is implemented
//	@Inject
//	private InspectionService inspectionService;

	@Inject
	private IEclipseContext context;
	
	@Inject
	private IShellProvider shellProvider;
	private FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Do not use this constructor when instantiate this composite!
	 * It is only included to make the WindowsBuilder working.
	 * 
	 * @param parent
	 * @param style
	 */
	private ReportPerformanceComposite(Composite parent, int style) {
		super(parent, SWT.NONE);
		createControls();
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 */
	public ReportPerformanceComposite(Composite parent) {
		super(parent, SWT.NONE);
		formToolkit.dispose();
		formToolkit = FormUtil.createToolkit();
	}

	@PostConstruct
	public void createControls() {
		setBackgroundMode(SWT.INHERIT_FORCE);
		GridLayout gl = new GridLayout(5, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.marginLeft = 5;
		gl.marginBottom = 5;
		setLayout(gl);
		
		Section targetSection = formToolkit.createSection(this, Section.TITLE_BAR);
		targetSection.setLayoutData(LayoutUtil.createFillData());
		formToolkit.paintBordersFor(targetSection);
		FormUtil.initSectionColors(targetSection);
		targetSection.setText("Targets");
		
		// TODO add this list to show and select the target states
		List targetsList = new List(targetSection, SWT.BORDER);
		formToolkit.adapt(targetsList, true, true);
		targetSection.setClient(targetsList);
		
		Section currentSection = formToolkit.createSection(this, Section.TITLE_BAR);
		currentSection.setLayoutData(LayoutUtil.createFillData());
		formToolkit.paintBordersFor(currentSection);
		FormUtil.initSectionColors(currentSection);
		currentSection.setText("Current");
		
		// TODO add this list to show and select the current items
		List currentList = new List(currentSection, SWT.BORDER);
		formToolkit.adapt(currentList, true, true);
		currentSection.setClient(currentList);
		
		Section activitySection = formToolkit.createSection(this, Section.TITLE_BAR);
		activitySection.setLayoutData(LayoutUtil.createFillData());
		formToolkit.paintBordersFor(activitySection);
		FormUtil.initSectionColors(activitySection);
		activitySection.setText("Activities");

		// TODO use this list to show and select the activities
		List activitiesList = new List(activitySection, SWT.BORDER);
		formToolkit.adapt(activitiesList, true, true);
		activitySection.setClient(activitiesList);
		
		Composite buttonComposite = new Composite(this, SWT.NONE);
		formToolkit.adapt(buttonComposite);
		formToolkit.paintBordersFor(buttonComposite);
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Button addButton = new Button(buttonComposite, SWT.NONE);
		formToolkit.adapt(addButton, true, true);
		addButton.setText(">>");
		
		Button removeButton = new Button(buttonComposite, SWT.NONE);
		formToolkit.adapt(removeButton, true, true);
		removeButton.setText("<<");
		
		Section containedSection = formToolkit.createSection(this, Section.TITLE_BAR);
		containedSection.setLayoutData(LayoutUtil.createFillData());
		formToolkit.paintBordersFor(containedSection);
		FormUtil.initSectionColors(containedSection);
		containedSection.setText("Assigned");
		
		Composite composite = new Composite(containedSection, SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		containedSection.setClient(composite);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		
		// TODO use this tree to display the added items
		Tree tree = new Tree(composite, SWT.BORDER);
		tree.setLayoutData(LayoutUtil.createFillData(2));
		formToolkit.adapt(tree);
		formToolkit.paintBordersFor(tree);

		// TODO translate
		Label label = new Label(composite, SWT.NONE);
		label.setText("Priority");
		
		Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(LayoutUtil.createHorzFillData());
		formToolkit.adapt(combo);
		formToolkit.paintBordersFor(combo);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void dispose() {
		formToolkit.dispose();
		super.dispose();
	}
	
	@Override
	public void setSelectedComponent(Component component) {
		// TODO Auto-generated method stub
		
	}
}