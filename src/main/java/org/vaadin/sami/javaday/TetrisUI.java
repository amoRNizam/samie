package org.vaadin.sami.javaday;

import com.sun.org.apache.xerces.internal.xs.StringList;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.vaadin.hezamu.canvas.Canvas;
import org.vaadin.sami.tetris.Game;
import org.vaadin.sami.tetris.Grid;
import org.vaadin.sami.tetris.Tetromino;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Notification.Type;

import javax.servlet.annotation.WebServlet;
import javax.swing.filechooser.FileSystemView;

import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.vaadin.sami.rk7.GetFailTestFrimSystem.showChildrenRes;

@Push
@Theme("valo")
@Title("Vaadin Tetris")
public class TetrisUI extends UI {

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TetrisUI.class)
    public static class Servlet extends VaadinServlet {
    }

    private static final int PAUSE_TIME_MS = 500;

    private static final long serialVersionUID = -152735180021558969L;

    // Tile size in pixels
    protected static final int TILE_SIZE = 30;

    // Playfield width in tiles
    private static final int PLAYFIELD_W = 10;

    // Playfield height in tiles
    private static final int PLAYFIELD_H = 20;

    // Playfield background color
    private static final String PLAYFIELD_COLOR = "#000";

    private VerticalLayout layout;
    private Canvas canvas;
    protected boolean running;
    protected Game game;

    private Label scoreLabel;

    //---------RK7---------
    public static FileSystemView fileSystemView;
    public static ArrayList<File> listFile = new ArrayList<>();
    public static TextField resultDirPath;

    //---------------------

    @Override
    protected void init(VaadinRequest request) {
        // Find the application directory
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();

        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(new About());

        //*** RK7 *****************************************************************
        //--- РАСПОЛОЖЕНИЕ (горизонтальная панель)
        HorizontalLayout settingsPanel = new HorizontalLayout();
        settingsPanel.addStyleName("outlined");
        settingsPanel.setSpacing(false);
        settingsPanel.setMargin(false);
        settingsPanel.setSizeFull();
        layout.addComponent(settingsPanel);

        //--- РАСПОЛОЖЕНИЕ (вертикальная панель)
        HorizontalLayout generalPanel = new HorizontalLayout();
        generalPanel.setMargin(true);
        generalPanel.setSpacing(true);
        generalPanel.addStyleName("outlined");
        generalPanel.setHeight(100.0f, Unit.PERCENTAGE);
        layout.addComponent(generalPanel);
//        layout.setComponentAlignment(generalPanel, Alignment.BOTTOM_RIGHT);

        //*********************ИЗОБРАЖЕНИЯ ******************************

        // Serve the image from the theme
        Resource res = new FileResource(
                new File(basepath + "\\WEB-INF\\images\\difference_scr1.png"));
        Resource res2 = new FileResource(
                new File(basepath + "\\WEB-INF\\images\\scr1.bmp"));

        // Display the image without caption
        Image image = new Image();
        image.setHeight("500");
        image.setWidth("600");
        image.setSource(res);

//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);

        //********* ЗДЕСЬ БУДЕТ ВЫГРУЗКА ПАПОК В СПИСОК *****************
        fileSystemView = FileSystemView.getFileSystemView();

        TwinColSelect<String> listFailTest = new TwinColSelect<>();
        listFailTest.setWidth("350");
        listFailTest.setHeight("525");
        showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
        listFailTest.setRightColumnCaption("Для отладки");
        listFailTest.setLeftColumnCaption("Все упавшие тесты");

        // Put some items in the listFailTest
//            listFailTest.setItems("1", "2", "3");

        // Few items, so we can set rows to match item count
//        listFailTest.setRows(listFailTest.size());

//         Preselect a few items
//        listFailTest.select("4", "5", "6");

        // Handle value changes
        listFailTest.addSelectionListener(event ->
                layout.addComponent(
                        new Label("Selected: " + event.getNewSelection())));
        generalPanel.addComponent(listFailTest);
        generalPanel.setComponentAlignment(listFailTest, Alignment.BOTTOM_LEFT);

        generalPanel.addComponent(image);
        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);


        // КНОПКИ
        Button btnUploadFTest = new Button("Загрузить fail-тесты");
        btnUploadFTest.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            System.out.println("123");
            showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
            // запоним список fail-тетсов
            ArrayList<String> s = new ArrayList<>();
            listFile.forEach(x -> s.add(x.getName()));
            listFailTest.setItems(s);
                });
        settingsPanel.addComponent(btnUploadFTest);

        Button btnChooseResultDir = new Button("выбрать");
        btnChooseResultDir.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            image.setSource(res2);
        });
        //-##########################################################################


//        final Window window = new Window("Window");
//        window.setWidth(300.0f, Unit.PIXELS);
//        final FormLayout content = new FormLayout();
//        content.setMargin(true);
//        window.setContent(content);
//
//        layout.getUI().getUI().addWindow(window);

        settingsPanel.addComponent(btnChooseResultDir);

//        LineBreakCounter lineBreakCounter = new LineBreakCounter();
//        lineBreakCounter.setSlow(true);
//
//        Upload sample = new Upload("123", lineBreakCounter);
//        sample.setImmediateMode(false);
//        sample.setButtonCaption("Upload File");
//
//        UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(sample, lineBreakCounter);
//
//        sample.addStartedListener(event -> {
//            if (uploadInfoWindow.getParent() == null) {
//                UI.getCurrent().addWindow(uploadInfoWindow);
//            }
//            uploadInfoWindow.setClosable(false);
//        });
//        sample.addFinishedListener(event -> uploadInfoWindow.setClosable(true));
//        settingsPanel.addComponent(sample);



//        File rootFile3 = new File("C:/");
//        FileSelect fileSelect = new FileSelect(rootFile3);
//        fileSelect.addValueChangeListener(event -> {
//            File file = fileSelect.getValue();
//            Date date = new Date(file.lastModified());
//            if (!file.isDirectory()) {
//                Notification.show(file.getPath()+", "+date+", "+file.length());
//            } else {
//                Notification.show(file.getPath()+", "+date);
//            }
//        });
//        tabSheet.addTab(fileSelect,"FileSelect demo");
//
//        setContent(tabSheet);
//
//
//
//        final Panel layout1 = new Panel();
//        layout1.setSizeFull();
//        layout1.setContent(tree);
        //-##########################################################################

        // ПОЛЯ
        resultDirPath = new TextField();
        resultDirPath.setPlaceholder("Write something");
        resultDirPath.setMaxLength(10);
        settingsPanel.addComponent(resultDirPath);
        //---------------

        setContent(layout);


        // **************************************************************************
        // ДАЛЬШЕ ТЕТРИС ------------------------------------------------------------

        // Button for moving left
        final Button leftBtn = new Button(VaadinIcons.ARROW_LEFT);
        leftBtn.addClickListener(e -> {
            game.moveLeft();
            drawGameState();
        });
        leftBtn.setClickShortcut(KeyCode.ARROW_LEFT);

        // Button for moving right
        final Button rightBtn = new Button(VaadinIcons.ARROW_RIGHT);
        rightBtn.addClickListener(e -> {
            game.moveRight();
            drawGameState();

        });
        rightBtn.setClickShortcut(KeyCode.ARROW_RIGHT);

        // Button for rotating clockwise
        final Button rotateCWBtn = new Button("[key down]",
                VaadinIcons.ROTATE_RIGHT);
        rotateCWBtn.addClickListener(e -> {
            game.rotateCW();
            drawGameState();
        });
        rotateCWBtn.setClickShortcut(KeyCode.ARROW_DOWN);

        // Button for rotating counter clockwise
        final Button rotateCCWBtn = new Button("[key up]",
                VaadinIcons.ROTATE_LEFT);
        rotateCCWBtn.addClickListener(e -> {
            game.rotateCCW();
            drawGameState();
        });
        rotateCCWBtn.setClickShortcut(KeyCode.ARROW_UP);

        // Button for dropping the piece
        final Button dropBtn = new Button("[space]", VaadinIcons.ARROW_DOWN);
        dropBtn.addClickListener(e -> {
            game.drop();
            drawGameState();
        });
        dropBtn.setClickShortcut(KeyCode.SPACEBAR);

        // Button for restarting the game
        final Button restartBtn = new PrimaryButton().withIcon(VaadinIcons.PLAY);
        restartBtn.addClickListener(e -> {
            running = !running;
            if (running) {
                game = new Game(10, 20);
                startGameThread();
                restartBtn.setIcon(VaadinIcons.STOP);
                dropBtn.focus();
            } else {
                restartBtn.setIcon(VaadinIcons.PLAY);
                gameOver();
            }
        });

        layout.addComponent(new MHorizontalLayout(
                restartBtn, leftBtn, rightBtn, rotateCCWBtn, rotateCWBtn,
                dropBtn
        ));

        // Canvas for the game
        canvas = new Canvas();
        layout.addComponent(canvas);
        canvas.setWidth((TILE_SIZE * PLAYFIELD_W) + "px");
        canvas.setHeight((TILE_SIZE * PLAYFIELD_H) + "px");
		// canvas.setBackgroundColor(PLAYFIELD_COLOR);

        // Label for score
        scoreLabel = new Label("");
        layout.addComponent(scoreLabel);

    }

    /**
     * Start the game thread that updates the game periodically.
     *
     */
    protected synchronized void startGameThread() {
        Thread t = new Thread() {
            public void run() {

                // Continue until stopped or game is over
                while (running && !game.isOver()) {

                    drawGameState();

                    // Pause for a while
                    try {
                        sleep(PAUSE_TIME_MS);
                    } catch (InterruptedException igmored) {
                    }

                    // Step the game forward and update score
                    game.step();
                    updateScore();

                }

                // Notify user that game is over
                gameOver();

            }
        };
        t.start();

    }

    /**
     * Update the score display.
     *
     */
    protected synchronized void updateScore() {
        access(() -> {
            scoreLabel.setValue("Score: " + game.getScore());
        });
    }

    /**
     * Quit the game.
     *
     */
    protected synchronized void gameOver() {
        running = false;
        // Draw the state
        access(() -> {
            Notification.show("Game Over", "Your score: " + game.getScore(),
                    Type.HUMANIZED_MESSAGE);
        });
    }

    /**
     * Draw the current game state.
     *
     */
    protected synchronized void drawGameState() {

        // Draw the state
        access(() -> {

            // Reset and clear canvas
            canvas.clear();
            canvas.setFillStyle(PLAYFIELD_COLOR);
            canvas.fillRect(0, 0, game.getWidth() * TILE_SIZE + 2, game.getHeight()
                    * TILE_SIZE + 2);

            // Draw the tetrominoes
            Grid state = game.getCurrentState();
            for (int x = 0; x < state.getWidth(); x++) {
                for (int y = 0; y < state.getHeight(); y++) {

                    int tile = state.get(x, y);
                    if (tile > 0) {

                        String color = Tetromino.get(tile).getColor();
                        canvas.setFillStyle(color);
                        canvas.fillRect(x * TILE_SIZE + 1, y * TILE_SIZE + 1,
                                TILE_SIZE - 2, TILE_SIZE - 2);
                    }
                }
            }
        });
    }

}
