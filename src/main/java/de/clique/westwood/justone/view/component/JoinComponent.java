package de.clique.westwood.justone.view.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.clique.westwood.justone.model.Player;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.WaitingRoomView;

/**
 * UI representation of {@link Player} on @{@link WaitingRoomView}.
 */
public class JoinComponent extends VerticalLayout {

    private final SessionStorageService sessionStorageService;
    private final GameService gameService;
    private final TextField username;
    private final TextField gameId;
    private final Button start;

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     */
    public JoinComponent(SessionStorageService sessionStorageService, GameService gameService) {
        this.sessionStorageService = sessionStorageService;
        this.gameService = gameService;
        username = new TextField("Username", "John Doe");
        username.setRequired(true);
        username.focus();
        gameId = new TextField("Game ID", "#123456");
        gameId.setRequired(true);
        start = new Button("Let's start");
        start.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);

        start.addClickShortcut(Key.ENTER);
        start.addClickListener(buttonClickEvent -> {
            try {
                if (username.isEmpty() || gameId.isEmpty()) {
                    throw new IllegalArgumentException("Username or Game ID not found");
                }
                this.gameService.createOrJoinGame(gameId.getValue(), username.getValue(), this.sessionStorageService);
                UI.getCurrent().navigate("waintingroom");
            } catch (IllegalArgumentException ex) {
                Notification.show("Can't join! " + ex.getMessage());
            }
        });
        start.focus();

        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        add(username, gameId, start);
    }

}
