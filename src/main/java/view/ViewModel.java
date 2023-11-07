package view;

import java.beans.PropertyChangeListener;

public abstract class ViewModel {
    private final String viewName;

    public ViewModel(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return this.viewName;
    }

    public abstract void firePropertyChanged(String propertyName);

    public abstract void addPropertyChangeListener(PropertyChangeListener listener);
}