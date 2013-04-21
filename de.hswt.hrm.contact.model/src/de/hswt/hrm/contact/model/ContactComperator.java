package de.hswt.hrm.contact.model;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class ContactComperator extends ViewerComparator {

    private int propertyIndex;
    private static final int DESCENDING = 1;
    private int direction = 0;

    public ContactComperator() {
        this.propertyIndex = 0;
        direction = 0;
    }

    public int getDirection() {

        return direction == 1 ? SWT.DOWN : SWT.UP;

    }

    public void setColumn(int column) {
        if (column == this.propertyIndex) {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        }
        else {
            // New column; do an ascending sort
            this.propertyIndex = column;
            direction = DESCENDING;
        }
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        Contact c1 = (Contact) e1;
        Contact c2 = (Contact) e2;
        int rc = 0;
        switch (propertyIndex) {
        case 0:
            rc = c1.getLastName().compareTo(c2.getLastName());
            break;
        case 1:
            rc = c1.getFirstName().compareTo(c2.getFirstName());
            break;
        default:
            rc = 0;
        }
        // If descending order, flip the direction
        if (direction == DESCENDING) {
            rc = -rc;
        }
        return rc;
    }

}
