package org.vaadin.sami.javaday;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.vaadin.hezamu.canvas.Canvas;
import org.vaadin.sami.rk7.Config;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.vaadin.sami.rk7.GetFailTestFromSystem.showChildrenRes;

@Push
@Theme("valo")
@Title("Vaadin Tetris")
public class TetrisUI extends UI {

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TetrisUI.class, resourceCacheTime = 1)
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
    public static Map<String, String> difImg = new HashMap<>();
    public static Map<String, String> ERROR_DIFF_IMG = new HashMap<>();
    public static Map<String, String> ERROR_TEST = new HashMap<>();

    public static TextField pathProject;

    public static String SELECTED_ER_T_IN_ALL;
    public static String SELECTED_ER_T_IN_SEL;

    public static Resource selectDeffImg;

    public static String basepath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();

    //---------------------

    @Override
    protected void init(VaadinRequest request) {
        // Find the application directory
        // Инициализируем конфиги
        new Config();

        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(new About());

        //*** RK7 *****************************************************************
        //--- РАСПОЛОЖЕНИЕ (горизонтальная панель)
        VerticalLayout btnPanelListT = new VerticalLayout();
        btnPanelListT.addStyleName("outlined");
        btnPanelListT.setSpacing(false);
        btnPanelListT.setMargin(false);
        btnPanelListT.setSizeFull();

        //--- РАСПОЛОЖЕНИЕ (горизонтальная панель)
        HorizontalLayout settingsPanel = new HorizontalLayout();
        settingsPanel.addStyleName("outlined");
        settingsPanel.setSpacing(false);
        settingsPanel.setMargin(false);
        settingsPanel.setSizeFull();

        //--- РАСПОЛОЖЕНИЕ (вертикальная панель)
        HorizontalLayout generalPanel = new HorizontalLayout();
        generalPanel.setMargin(true);
        generalPanel.setSpacing(true);
        generalPanel.addStyleName("outlined");
        generalPanel.setHeight(100.0f, Unit.PERCENTAGE);
//        layout.setComponentAlignment(generalPanel, Alignment.BOTTOM_RIGHT);

        //*********************ИЗОБРАЖЕНИЯ ******************************

        // Serve the image from the theme
        Resource res = new FileResource(
                new File(basepath + "\\WEB-INF\\images\\difference_scr1.png"));
        Resource res2 = new FileResource(
                new File("D:\\TestingResult_19.11.2019_01.03.56\\50805\\Screens\\difference_scr1.png"));

        // Display the image without caption
        Image image = new Image();
        image.setHeight("500");
        image.setWidth("600");
//        image.setSource(res);

//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);

        //********* ЗДЕСЬ БУДЕТ ВЫГРУЗКА ПАПОК В СПИСОК *****************
        ListSelect listAllFailTest = new ListSelect<>("Все упавшие тесты");
        listAllFailTest.setRows(6);
//        listAllFailTest.setWidth(100.0f, Unit.PERCENTAGE);
        listAllFailTest.setWidth("170");
        listAllFailTest.setHeight("525");
//        generalPanel.addComponent(listAllFailTest);
//        generalPanel.setComponentAlignment(listAllFailTest, Alignment.BOTTOM_LEFT);

        Set<String> eList = new LinkedHashSet<>();

        listAllFailTest.addValueChangeListener(event -> {
                SELECTED_ER_T_IN_ALL = event.getValue().toString()
                 .replace("[","").replace("]","");
            System.out.println("|" + SELECTED_ER_T_IN_ALL + "|");
            System.out.println(difImg.get(SELECTED_ER_T_IN_ALL.trim()));

            selectDeffImg = new FileResource(
                    new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));

            image.setSource(selectDeffImg);
        });

        ListSelect listSelectFailTest = new ListSelect<>("Выбранные для отладки");
        listAllFailTest.setRows(6);
        listSelectFailTest.setWidth("170");
        listSelectFailTest.setHeight("525");
        generalPanel.addComponent(listSelectFailTest);
        generalPanel.setComponentAlignment(listSelectFailTest, Alignment.BOTTOM_RIGHT);
        //--------------------------------------------
        listSelectFailTest.addValueChangeListener(event -> {
            SELECTED_ER_T_IN_SEL = event.getValue().toString()
                    .replace("[","").replace("]","");
        });

        fileSystemView = FileSystemView.getFileSystemView();

        TwinColSelect<String> listFailTest = new TwinColSelect<>();
        listFailTest.setWidth("325");
        listFailTest.setHeight("525");
        showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
        listFailTest.setRightColumnCaption("Для отладки");
        listFailTest.setLeftColumnCaption("Все упавшие тесты");

        // Handle value changes
        listFailTest.addSelectionListener(event -> {
                layout.addComponent(
                        new Label("Selected: " + event.getNewSelection()));
        });


//        generalPanel.addComponent(listFailTest);
//        generalPanel.setComponentAlignment(listFailTest, Alignment.BOTTOM_LEFT);
//
//        generalPanel.addComponent(image);
//        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);


        // КНОПКИ
        Button btnUploadFTest = new Button("Загрузить fail-тесты");
        btnUploadFTest.addClickListener(event -> {
//            System.out.println("123");
            showChildrenRes("D:\\TestingResult_19.11.2019_01.03.56");
            // запоним список fail-тетсов
            ArrayList<String> s = new ArrayList<>();

//            listFile.forEach(x -> s.add(x.getName()));
//            listFailTest.setItems(s);
            try {
                for (Map.Entry<String, String> fTest : ERROR_TEST.entrySet()) {
                    s.add(fTest.getKey());
                }
//                listFailTest.setItems(s);
                listAllFailTest.setItems(s);
                Notification.show("Загрузка успешно завершена!", Type.TRAY_NOTIFICATION);
            }catch (Exception e) {
                Notification.show("Произошла ошибка! \n" +e, Type.ERROR_MESSAGE);
            }

        });

        Button btnChooseResultDir = new Button("выбрать");
        btnChooseResultDir.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
//            image.setSource(res2);
//            System.out.println("OPEN IMG " + difImg.get(SELECTED_ER_T_IN_ALL.trim()));
//            selectDeffImg = new FileResource(
//                    new File(difImg.get(SELECTED_ER_T_IN_ALL.trim())));
//
            image.setSource(res2);
        });

        Button btnAdd = new Button(">");
        btnAdd.setIconAlternateText(">");

        btnAdd.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            eList.add(SELECTED_ER_T_IN_ALL);
            listSelectFailTest.setItems(eList);
        });
        Button btnDel = new Button("<");
        btnDel.setIconAlternateText("<");

        btnDel.addClickListener(event -> {
            Notification.show("The button was clicked", Type.TRAY_NOTIFICATION);
            eList.remove(SELECTED_ER_T_IN_SEL);
            listSelectFailTest.setItems(eList);
        });
        //-##########################################################################


        //-##########################################################################

        // ПОЛЯ
        resultDirPath = new TextField();
        resultDirPath.setPlaceholder("Write something");
//        resultDirPath.setMaxLength(10);

        pathProject = new TextField();
        pathProject.setPlaceholder("Укажите путь к папке 'input' в проекте");
        pathProject.setWidth("350");
//        pathProject.setMaxLength(15);

        /// ПОСТРОЕНИЕ ИНТЕРФЕЙСА (РАСПОЛОЖЕНИЕ ЭЕЛЕМЕНТОВ)

        layout.addComponent(settingsPanel);
        layout.addComponent(generalPanel);

        settingsPanel.addComponent(btnUploadFTest); // кнопка Загрузить fail-тесты
        settingsPanel.setComponentAlignment(btnUploadFTest, Alignment.BOTTOM_LEFT);

        settingsPanel.addComponent(resultDirPath);

        generalPanel.addComponent(listAllFailTest);
        generalPanel.setComponentAlignment(listAllFailTest, Alignment.BOTTOM_LEFT);

        btnPanelListT.addComponent(btnAdd);
        btnPanelListT.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        btnPanelListT.addComponent(btnDel);
        btnPanelListT.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);
        generalPanel.addComponent(btnPanelListT);
        generalPanel.setComponentAlignment(btnPanelListT, Alignment.MIDDLE_CENTER);

//        generalPanel.addComponent(btnAdd);
//        generalPanel.setComponentAlignment(btnAdd, Alignment.MIDDLE_CENTER);

        generalPanel.addComponent(listSelectFailTest);
        generalPanel.setComponentAlignment(listSelectFailTest, Alignment.BOTTOM_RIGHT);

//        generalPanel.addComponent(listFailTest);
//        generalPanel.setComponentAlignment(listFailTest, Alignment.BOTTOM_LEFT);

        generalPanel.addComponent(image);
        generalPanel.setComponentAlignment(image, Alignment.BOTTOM_RIGHT);

        settingsPanel.addComponent(btnChooseResultDir);
        settingsPanel.addComponent(pathProject);
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
