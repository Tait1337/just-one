package de.clique.westwood.justone.view.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * CSS absolute Layout.
 */
public class AbsoluteLayout extends Div {

    /**
     * Constructor
     */
    public AbsoluteLayout() {
    }

    /**
     * Constructor
     * @param components the components to add
     */
    public AbsoluteLayout(Component... components) {
        super(components);
    }

    /**
     * Set the position of a component
     * @param cmp component to layout
     * @param top position on top (optional)
     * @param left position on left (optional)
     * @param right position on right (optional)
     * @param bottom position on bottom (optional)
     */
    public void layoutAbsolute(Component cmp, String top, String left, String right, String bottom) {
        if (cmp == null){
            throw new IllegalArgumentException("Component must not be null");
        }
        cmp.getElement().getStyle().set("position", "absolute");
        if (top != null) {
            cmp.getElement().getStyle().set("top", top);
        }
        if (left != null) {
            cmp.getElement().getStyle().set("left", left);
        }
        if (right != null) {
            cmp.getElement().getStyle().set("right", right);
        }
        if (bottom != null) {
            cmp.getElement().getStyle().set("bottom", bottom);
        }
    }

}
