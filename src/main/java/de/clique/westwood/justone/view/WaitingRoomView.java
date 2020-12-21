package de.clique.westwood.justone.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import de.clique.westwood.justone.event.GameEvent;
import de.clique.westwood.justone.event.GameEventListener;
import de.clique.westwood.justone.model.Game;
import de.clique.westwood.justone.model.Player;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;

import static de.clique.westwood.justone.event.GameEventType.GAME_STARTED;
import static de.clique.westwood.justone.event.GameEventType.PLAYER_CHANGED;

/**
 * Waining room where {@link Player}s waits until the {@link Game} starts.
 */
@Push
@Route("waintingroom")
public class WaitingRoomView extends VerticalLayout implements GameEventListener {

    private final transient SessionStorageService sessionStorageService;
    private final transient GameService gameService;

    private final H1 titleLbl;
    private final H3 subtitleLbl;
    private final Grid<Player> playerGrid;
    private final Button leaveBtn;
    private final Button startBtn;
    private final HorizontalLayout buttonGrp;

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     */
    public WaitingRoomView(SessionStorageService sessionStorageService, GameService gameService) {
        this.sessionStorageService = sessionStorageService;
        this.gameService = gameService;

        titleLbl = new H1("Game " + sessionStorageService.getGameId());
        subtitleLbl = new H3("Player " + sessionStorageService.getPlayer().getName());
        playerGrid = new Grid<>(Player.class);
        playerGrid.setItems(gameService.getPlayersForGame(sessionStorageService));
        leaveBtn = new Button("Leave game");
        leaveBtn.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
        leaveBtn.addClickListener(this::leaveGame);
        startBtn = new Button("Start game");
        startBtn.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
        startBtn.addClickListener(this::startGame);
        buttonGrp = new HorizontalLayout(leaveBtn, startBtn);

        setHorizontalComponentAlignment(Alignment.END, buttonGrp);
        add(titleLbl, subtitleLbl, playerGrid, buttonGrp);

        addGameEventListener();
    }

    /**
     * @see GameEventListener
     */
    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch (gameEvent.getType()) {
            case PLAYER_CHANGED:
                getUI().ifPresent(ui -> ui.access(() ->
                    playerGrid.setItems(gameService.getPlayersForGame(sessionStorageService))
                ));
                break;
            case GAME_STARTED:
                removeGameEventListener();
                getUI().ifPresent(ui -> ui.access(() ->
                    ui.navigate("gameroom")
                ));
                break;
            default:
                // do not handle
        }
    }

    private void addGameEventListener() {
        sessionStorageService.getPlayer().addGameEventListener(PLAYER_CHANGED, this);
        sessionStorageService.getPlayer().addGameEventListener(GAME_STARTED, this);
    }

    private void removeGameEventListener() {
        sessionStorageService.getPlayer().removeGameEventListener(PLAYER_CHANGED, this);
        sessionStorageService.getPlayer().removeGameEventListener(GAME_STARTED, this);
    }

    private void startGame(ClickEvent<Button> e) {
        try {
            gameService.startGame(sessionStorageService);
        } catch (IllegalStateException ex) {
            getUI().ifPresent(ui -> ui.access(() ->
                Notification.show("Can't start! " + ex.getMessage())
            ));
        }
    }

    private void leaveGame(ClickEvent<Button> e) {
        removeGameEventListener();
        gameService.leave(sessionStorageService);
        getUI().ifPresent(ui -> ui.access(() ->
            ui.navigate("")
        ));
    }

}
