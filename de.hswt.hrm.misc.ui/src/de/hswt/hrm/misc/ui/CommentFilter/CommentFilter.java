package de.hswt.hrm.misc.ui.CommentFilter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.hswt.hrm.evaluation.model.Evaluation;

public class CommentFilter extends ViewerFilter {

    private String searchString;

    public void setSearchString(String substring) {
        searchString = (".*" + substring + ".*").toLowerCase();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (searchString == null || searchString.length() == 0) {
            return true;
        }

        Evaluation e = (Evaluation) element;

        if (e.getName().toLowerCase().matches(searchString)) {
            return true;
        }

        if (e.getText().toLowerCase().matches(searchString)) {
            return true;
        }

        return false;
    }

}