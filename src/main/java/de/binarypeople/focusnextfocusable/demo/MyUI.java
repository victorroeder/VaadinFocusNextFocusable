package de.binarypeople.focusnextfocusable.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import de.binarypeople.focusnextfocusable.FocusNextFocusable;
import org.vaadin.artur.KeyAction;

/**
 *
 */
@Theme("mytheme")
@Widgetset("de.binarypeople.focusnextfocusable.demo.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
        final VerticalLayout mainLayout = new VerticalLayout();

        final Panel panelWithOnEnterScope = new Panel(mainLayout);
        
        final FocusNextFocusable fnf = new FocusNextFocusable(AbstractTextField.class, ComboBox.class, DateField.class); //.butExcludeComponentClasses(TextArea.class);

        final TextField tf1 = new TextField();
        tf1.focus();
        final TextArea ta2 = new TextArea();
        ta2.setInputPrompt("Use Shift+Enter to create new line");
        
        // Add special onEnter behaviour to our TextArea
        KeyAction action = new KeyAction(ShortcutAction.KeyCode.ENTER);
        action.addKeypressListener((KeyAction.KeyActionEvent keyPressEvent) -> {
            fnf.findCurrentComponentAndFocusNextFocusable(keyPressEvent.getComponent().getUI(), ta2 /* or: (Field<?>) keyPressEvent.getComponent()*/);
        });
        
        action.extend(ta2);
        
        action.setPreventDefault(true);
        action.setStopPropagation(false);
        ///////////////////
        
        
        mainLayout.addComponent(tf1);
        mainLayout.addComponent(ta2);
        
        panelWithOnEnterScope.addShortcutListener(new ShortcutListener("Handle OnEnter Event", ShortcutAction.KeyCode.ENTER, new int[]{}) {

            // sender is the panelWithOnEnterScope and
            // target is the current Field/Focusable
            @Override
            public void handleAction(Object sender, Object target) {

                Field<?> newlyFocusedField = null;

                if (sender instanceof HasComponents && target instanceof Field<?>) {
                    
                    newlyFocusedField = fnf.findCurrentComponentAndFocusNextFocusable((HasComponents) sender, (Field) target);

                    Notification.show("" + newlyFocusedField);
                    
                    // in case newlyFocusedField is 'null'
                    // there was no further Field to focus
                    // so return the focus to the first Focusable in our UI again
                    if (newlyFocusedField == null) {
                        tf1.focus();
                    }
                }
            }
        });
        
        final TextField tf11 = new TextField("");
        final TextField tf12 = new TextField("");
        final TextField tf13 = new TextField("");
        tf13.setReadOnly(true);
        tf13.setInputPrompt("ReadOnly fields not considered");
        final TextField tf14 = new TextField("");
        final TextField tf15 = new TextField("");
        final ComboBox cb16 = new ComboBox();
        
        final TextField tf21 = new TextField("");
        final TextField tf22 = new TextField("");
        final TextField tf23 = new TextField("");
        tf23.setEnabled(false);
        tf23.setInputPrompt("Disabled fields not considered");
        final TextField tf24 = new TextField("");
        final TextField tf25 = new TextField("");
        final TextField tf26 = new TextField("");
        final DateField df27 = new DateField();
        
        
        final VerticalLayout vl1 = new VerticalLayout(tf11, tf12, tf13, tf14, tf15, cb16);
        final VerticalLayout vl2 = new VerticalLayout(tf21, tf22, tf23, tf24, tf25, tf26, df27);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(vl1, "Tab 1");
        tabSheet.addTab(vl2, "Tab 2");
        
        mainLayout.addComponents(tabSheet);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        
        setContent(panelWithOnEnterScope);
        
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
