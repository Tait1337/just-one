package de.clique.westwood.justone.view;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import de.clique.westwood.justone.model.Game;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.component.JoinComponent;

/**
 * Main Page with initial UI to create/join a {@link Game}.
 */
@Route
@PWA(name = "Just-One",
        shortName = "Just-One",
        description = "Web version of Asmodée's Just One board game",
        enableInstallPrompt = false)
@Theme(value = Material.class)
public class MainView extends VerticalLayout {

    private final Image logo;
    private final JoinComponent join;
    private final Paragraph footer;

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     */
    public MainView(SessionStorageService sessionStorageService, GameService gameService) {
        logo = new Image("logo.png", "logo");
        logo.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.access(() ->
                ui.getPage().executeJs("window.open('https://youtu.be/IzXhC_NQct');")
            ));
        });
        join = new JoinComponent(sessionStorageService, gameService);
        footer = new Paragraph(new Html("<span>© 2020 Oliver Tribess | powered by <a href=\"https://vaadin.com\" target=\"blank\">Vaadin</a> | support the board game publisher <a href=\"https://asmodee.com\" target=\"blank\">Asmodée</a></span>"));
        footer.getStyle().set("position", "absolute");
        footer.getStyle().set("bottom", "0px");

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(logo, join, footer);
    }

}
