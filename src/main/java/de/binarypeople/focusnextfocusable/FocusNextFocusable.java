package de.binarypeople.focusnextfocusable;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.TabSheet;
import java.util.Iterator;

/**
 *
 * @author Victor
 */
public class FocusNextFocusable {
    
    Class<? extends Field<?>>[] whiteListFieldsClasses = null;
    Class<? extends Field<?>>[] blackListFieldsClasses = null;
    boolean currentlyFocusedFieldFound = false;
    
    public FocusNextFocusable(Class<? extends Field<?>> ... focusingFieldsClasses) {
        
        this.whiteListFieldsClasses = focusingFieldsClasses;
                        
    }
    
    private FocusNextFocusable() {
        
    }
    
    public FocusNextFocusable butExcludeComponentClasses(Class<? extends Field<?>> ... excludeFieldsClasses) {
        
        this.blackListFieldsClasses = excludeFieldsClasses;
        
        return this;
        
    }
    
    private Field focusNextFocusable(Iterator<Component> iterate) {
                
        while (iterate.hasNext()) {     
            
            Component component = iterate.next();     
            
            for (Class<? extends Component> currentFieldClassFromWhiteList : this.whiteListFieldsClasses) {
                
                if(currentFieldClassFromWhiteList.isAssignableFrom(component.getClass())) {
                    
                    boolean foundInBlackList = false;
                    
                    if(this.blackListFieldsClasses != null) {
                        for (Class<? extends Component> currentFieldClassFromBlackList : this.blackListFieldsClasses) {

                            if(currentFieldClassFromBlackList.equals(component.getClass())) {

                                foundInBlackList = true;

                            }

                        }
                    }
                    
                    if(foundInBlackList == false && ((Field) component).isVisible() && ((Field) component).isEnabled() && ((Field) component).isReadOnly() == false) {
                        
                        ((Field) component).focus();

                        return (Field) component;
                        
                    } // else simply continue
                }
                
            }
            
            if (component instanceof HasComponents) {
                
                Field field = null;
                
                if(component instanceof TabSheet) {
                    
                    // if the current component is a TabSheet then continue in the currently selected tab
                    if(((TabSheet) component).getSelectedTab() instanceof HasComponents) {
                        field = focusNextFocusable(((HasComponents) ((TabSheet) component).getSelectedTab()).iterator());
                    }
                    
                } else {
                    
                    field = focusNextFocusable(((HasComponents) component).iterator());
                }
                

                if (field != null) {
                    return (Field) field;                        
                }
            }
        }
        
        return null;
    }
    
    public Field findCurrentComponentAndFocusNextFocusable(HasComponents root, Field<?> currentlyFocused) {
        
        Iterator<Component> iterate = root.iterator();
        
        while (iterate.hasNext()) {
            
            Component component = iterate.next();
            
            if(component == currentlyFocused) {
                
                currentlyFocusedFieldFound = true;
                
                Field nextFocusable = focusNextFocusable(iterate);
                
                if(nextFocusable == null) {
                    continue;
                } else {
                    return nextFocusable;
                }
                
            }
            
            if (component instanceof HasComponents) {
                
                Component focusedComponent = null;
                
                if(component instanceof TabSheet) {
                    
                    // if the current component is a TabSheet then continue in the currently selected tab
                    if(((TabSheet) component).getSelectedTab() instanceof HasComponents) {

                        focusedComponent = findCurrentComponentAndFocusNextFocusable((HasComponents) ((TabSheet) component).getSelectedTab(), currentlyFocused);
                    }
                } else {
                
                    focusedComponent = findCurrentComponentAndFocusNextFocusable((HasComponents) component, currentlyFocused);
                }
                
                if (focusedComponent != null) {
                    
                    return (Field) focusedComponent;
                    
                } else {
                    
                    // In case focusedComponent is 'null',
                    // the currently focused component is a leaf in the component tree
                    // so no further Component could be focused.
                    // Maybe TODO: Restart from the beginning?
                    if(currentlyFocusedFieldFound) {
                        
                        return focusNextFocusable(iterate);
                        
                    }
                }
            }
        }

        return null;
    }
    
}
