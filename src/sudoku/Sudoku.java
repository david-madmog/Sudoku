/*
 * (C) David Poirier July 2013
 */
package sudoku;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author davidp
 */
public class Sudoku extends JApplet {
    
    private static final int JFXPANEL_WIDTH_INT = 500;
    private static final int JFXPANEL_HEIGHT_INT = 500;
    private static JFXPanel fxContainer;
//    private static BoardPanel BoardPainter; 
    private static Board board ;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                }
                
                JFrame frame = new JFrame("David's Sudoku");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                JApplet applet = new Sudoku();
                applet.init();
                
                frame.setContentPane(applet.getContentPane());
                
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                applet.start();
            }
        });
    }
    
    @Override
    public void init() {
//        BoardPainter = new BoardPanel() ;
        board = new Board() ;
//        BoardPainter.board = board ;
        fxContainer = new JFXPanel();
        fxContainer.setPreferredSize(new Dimension(JFXPANEL_WIDTH_INT, JFXPANEL_HEIGHT_INT));
        add(fxContainer, BorderLayout.CENTER);
        // create JavaFX scene
        Platform.runLater(new Runnable() {
            @Override
            public void run() {         
                createScene(board);
            }
        });
    }
    
     private void createScene(final Board board ) {
        // We need to create canvas first, as it's going to handle all the events for
        // all the controls on the form
        final BoardCanvas CC = new BoardCanvas(JFXPANEL_WIDTH_INT, JFXPANEL_WIDTH_INT) ;
        CC.board = board ;
        CC.setFocusTraversable(true);
        CC.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    CC.mouseClick(t);
                }
            }
        ) ;
        
        CC.setOnKeyPressed(
             new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent k) {
                    CC.keyPressed(k);
                }
            }
        ) ;

        CC.focusedProperty().addListener(
            new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                        CC.focusEvent();
                }
            }
        ) ;
         
         // Now Create menu structure 
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        
        MenuItem menuFileNew = new MenuItem("New Game") ;
        menuFileNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.SHORTCUT_DOWN));
        menuFileNew.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.FileMenuHandler(A, "N");
                }
            }
        ) ;        
        MenuItem menuFileOpen = new MenuItem("Open Game") ;
        menuFileOpen.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.FileMenuHandler(A, "O");
                }
            }
        ) ;        
        MenuItem menuFileSave = new MenuItem("Save Game") ;
        menuFileSave.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.FileMenuHandler(A, "S");
                }
            }
        ) ;        
        SeparatorMenuItem mnuFileSep1 = new SeparatorMenuItem() ;
        
        MenuItem menuFileExit = new MenuItem("Exit") ;
        menuFileExit.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.FileMenuHandler(A, "X");
                }
            }
        ) ;        
        menuFile.getItems().addAll(menuFileNew, menuFileOpen, menuFileSave, mnuFileSep1, menuFileExit);
        
        Menu menuDiff = new Menu("Difficulty");
        CC.DiffMenu = menuDiff ;
        CheckMenuItem menuDiffVEasy = new CheckMenuItem("Very Easy") ;
        menuDiffVEasy.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.DiffMenuHandler(A, SudokuPuzzle.Difficulty.VERY_EASY);
                }
            }
        ) ;        
        CheckMenuItem menuDiffEasy = new CheckMenuItem("Easy") ;
        menuDiffEasy.setSelected(true);
        menuDiffEasy.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.DiffMenuHandler(A, SudokuPuzzle.Difficulty.EASY);
                }
            }
        ) ;        
        CheckMenuItem menuDiffMedium = new CheckMenuItem("Medium") ;
        menuDiffMedium.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.DiffMenuHandler(A, SudokuPuzzle.Difficulty.MEDIUM);
                }
            }
        ) ;        
        CheckMenuItem menuDiffHard = new CheckMenuItem("Hard") ;
        menuDiffHard.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.DiffMenuHandler(A, SudokuPuzzle.Difficulty.HARD);
                }
            }
        ) ;        
        CheckMenuItem menuDiffFiendish = new CheckMenuItem("Fiendish") ;
        menuDiffFiendish.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.DiffMenuHandler(A, SudokuPuzzle.Difficulty.FIENDISH);
                }
            }
        ) ;        
        menuDiff.getItems().addAll(menuDiffVEasy, menuDiffEasy, menuDiffMedium, menuDiffHard, menuDiffFiendish);
        
        menuBar.getMenus().addAll(menuFile, menuDiff);

        // Next, create toolbar
        ToolBar TB = new ToolBar() ;
        Label L = new Label("00:00:00") ;
        TB.getItems().add(L) ;
        CC.TimerLabel = L ;
        Button B[] = new Button[9] ;
        for (int i=0; i<9; i++) {
            B[i] = new Button(String.format("%d", i+1)) ;
            TB.getItems().add(B[i]) ;
            B[i].setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent A) {
                        CC.ButtonHandler(A);
                    }
                }
            ) ;        
        }
        Button B0 = new Button("Clear");
        B0.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.ButtonHandlerClear(A);
                }
            }
        ) ;        
        ToggleButton BL = new ToggleButton("Guess Mode");
        CC.GuessModeButton = BL ;
        BL.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent A) {
                    CC.ButtonHandlerGuessMode(A);
                }
            }
        ) ;        
        TB.getItems().add(B0);
        TB.getItems().add(BL);
        
        // Create layout pane and add items to it
//        FlowPane root = new FlowPane();
//        AnchorPane root = new AnchorPane();
        VBox root = new VBox();
        
        // First, menu
        root.getChildren().add(menuBar);
        
        // And toolbar
        root.getChildren().add(TB);
        
        // Add the main drawing area 
        root.getChildren().add(CC);
        
        // And set up into window
        fxContainer.setScene(new Scene(root));
        fxContainer.addHierarchyBoundsListener(CC) ; // Pick up on resize events
        
        // We need focus to get KB events
        CC.requestFocus();
 
        // and set intial size
        CC.size(fxContainer);
    }

    
}
