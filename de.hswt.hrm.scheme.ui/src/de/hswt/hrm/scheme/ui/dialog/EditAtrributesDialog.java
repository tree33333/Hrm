package de.hswt.hrm.scheme.ui.dialog;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import de.hswt.hrm.common.ui.swt.forms.FormUtil;
import de.hswt.hrm.common.ui.swt.layouts.LayoutUtil;
import de.hswt.hrm.common.ui.swt.table.ColumnDescription;
import de.hswt.hrm.common.ui.swt.table.TableViewerController;
import de.hswt.hrm.common.ui.swt.utils.SWTResourceManager;
import de.hswt.hrm.component.model.Attribute;
import de.hswt.hrm.component.model.Component;
import de.hswt.hrm.i18n.I18n;
import de.hswt.hrm.i18n.I18nFactory;
import de.hswt.hrm.scheme.ui.util.SchemeDialogUtil;

public class EditAtrributesDialog extends TitleAreaDialog {
    private static final I18n I18N = I18nFactory.getI18n(EditAtrributesDialog.class);
    
    private Component component;
    
    private Table table;
    private TableViewer tableViewer;

    private Text newEditor;

    private Collection<Attribute> attributes;

    private Map<Attribute, String> attributeMap;

    public EditAtrributesDialog(Shell parentShell, Component component,
            Collection<Attribute> attributes, Map<Attribute, String> assignedValues) {
        super(parentShell);
        this.component = component;
        this.attributes = attributes;
        
        // We're working on a copy as we won't change the given map
        this.attributeMap = new HashMap<>(assignedValues);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        // Contents of Dialog
        draw(composite);

        setMessage(I18N.tr("Edit attributes for component")+ ": " + component.getName());
        setTitle(I18N.tr("Attributes"));
        
        composite.forceFocus();
        return composite;

    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control c = super.createButtonBar(parent);
        getButton(OK).setText(I18N.tr("OK"));
        getButton(CANCEL).setText(I18N.tr("Cancel"));
        return c;
    }

    private void draw(Composite parent) {
        Section headerSection = new Section(parent, Section.TITLE_BAR);
        headerSection.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        headerSection.setText(I18N.tr("Available attributes"));
        headerSection.setLayoutData(LayoutUtil.createFillData());
        FormUtil.initSectionColors(headerSection);
        headerSection.setLayout(new FillLayout());
        headerSection.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        Composite headerComposite = new Composite(headerSection, SWT.NONE);
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        headerComposite.setLayout(gl);
        headerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        tableViewer = new TableViewer(headerSection, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        headerSection.setClient(table);
        GridData gd = LayoutUtil.createFillData();
        gd.widthHint = 800;
        gd.heightHint = 300;
        table.setLayoutData(gd);
        initTable();
    }

    private void initTable() {

        List<ColumnDescription<Attribute>> columns = SchemeDialogUtil.getColumns(attributeMap);

        TableViewerController<Attribute> filler = new TableViewerController<>(tableViewer);
        filler.createColumns(columns);
        filler.createColumnSelectionMenu();

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setInput(attributes);
        tableViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object o1, Object o2) {
                Attribute a1 = (Attribute) o1;
                Attribute a2 = (Attribute) o2;
                return a1.getName().compareToIgnoreCase(a2.getName());
            }
        });
        
        final TableEditor editor = new TableEditor(table);
        // The editor must have the same size as the cell and must
        // not be any smaller than 50 pixels.
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;
        // editing the second column
        final int EDITABLECOLUMN = 1;

        table.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // Clean up any previous editor control
                Control oldEditor = editor.getEditor();
                if (oldEditor != null)
                    oldEditor.dispose();

                // Identify the selected row
                final TableItem item = (TableItem) e.item;
                if (item == null)
                    return;

                final Attribute attr = (Attribute) item.getData();

                // The control that will be the editor must be a child of the Table
                newEditor = new Text(table, SWT.NONE);
                newEditor.setText(item.getText(EDITABLECOLUMN));
                newEditor.addModifyListener(new ModifyListener() {
                    public void modifyText(ModifyEvent me) {
                        Text text = (Text) editor.getEditor();
                        editor.getItem().setText(EDITABLECOLUMN, text.getText());
                        
                        // Store updated value in map aka sync map with table
                        attributeMap.put(attr, text.getText());
                    }
                });

                newEditor.selectAll();
                newEditor.setFocus();
                editor.setEditor(newEditor, item, EDITABLECOLUMN);
            }
        });

    }

    // The attribute map is automatically synced with the table
    public Map<Attribute, String> getAttributeMap() {
        return attributeMap;
    }
}
