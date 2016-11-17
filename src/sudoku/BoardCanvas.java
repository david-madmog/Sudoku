/*
 * This module is responsible for all drawing and UI event handling for the application
 *
 * (C) David Poirier July 2013
*/
package sudoku;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.Date;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.swing.JApplet;
import javax.swing.Timer;

/**
 *
 * @author davidp
 */
public class BoardCanvas extends Canvas implements HierarchyBoundsListener, ActionListener {
    private Timer GameTimer ;
    public Board board ;
    public ToggleButton GuessModeButton ;
    public Menu DiffMenu ;
    public JApplet applet ;
    public Label TimerLabel;
    
    private double XMainOff, YMainOff ;
    private double XGuessOff, YGuessOff ;
    private Font MainFont ;
    private Font GuessFont ;
    private SudokuPuzzle.Difficulty CurrentDiff ;
    private Date GameStarted ;

    BoardCanvas(int x, int y) {
        super(x, y) ;
        
        CurrentDiff = SudokuPuzzle.Difficulty.EASY ;
        
        // Timer for refreshing game timer
        GameTimer = new Timer(200, this) ;
        GameTimer.stop() ;
    }
    
    // Event handler on... mouse click event!
    public void mouseClick(MouseEvent t) {
        if (t.getClickCount()> 1) {
            // See if it's a promote...
            int XX = (int) ((int)(t.getX() * 9.0)/getWidth());
            int YY = (int) ((int)(t.getY() * 9.0)/getHeight());
            
            if(board.getCellValue(XX, YY) == 0) {
                if(board.countGuesses(XX, YY) == 0) {
                    SudokuPuzzle.newGame(CurrentDiff, board);                    
                    StartGameTimer() ;
                } else if(board.countGuesses(XX, YY) == 1) {
                    board.CurrentCellX = XX ;
                    board.CurrentCellY = YY ;
                    int V = 0;
                    for (int i = 1; i<=9; i++) {
                        if (board.getGuesses(XX, YY, i))
                            V = i ;
                    }
                    board.setCurrentCell(V);
                }
            }
        } else {
            board.CurrentCellX = (int) ((int)(t.getX() * 9.0)/getWidth());
            board.CurrentCellY = (int) ((int)(t.getY() * 9.0)/getHeight());
            if (board.getCurrentCell() != 0)
                board.toggleCurrentCellHighlight() ;
        }
        // Normal behaviour is to set focus...
        requestFocus();
        draw() ;
    }
    
    @Override
    public void ancestorResized(HierarchyEvent e) {
        size((JFXPanel) e.getComponent()) ;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void ancestorMoved(HierarchyEvent e) {
        ; // Don't care about this
    }

    public void size(JFXPanel fxContainer ) {
        // resize event: resize my main drawing canvas
        setWidth(fxContainer.getScene().getWidth());
        setHeight(fxContainer.getHeight() - getLayoutY());
        
        // Work out font size (Main numbers) and positioning offset
        // DO this here, and cache so we don't need to do each time
        MainFont = new Font("Arial", Math.min(getWidth()/12, getHeight()/12)) ;
//        MainFont = new Font("Trebuchet MS", Math.min(getWidth()/11, getHeight()/11)) ;
        FontMetrics FM = Toolkit.getToolkit().getFontLoader().getFontMetrics(MainFont) ;
        YMainOff = ((getHeight()/9) + FM.getAscent()) / 2 ;
        XMainOff = ((getWidth()/9) - FM.computeStringWidth("0")) / 2 ;
        GuessFont = new Font("Arial", Math.min(getWidth()/30, getHeight()/30)) ;
        FM = Toolkit.getToolkit().getFontLoader().getFontMetrics(GuessFont) ;
        YGuessOff = ((getHeight()/27) + FM.getAscent()) / 2 ;
        XGuessOff = ((getWidth()/27) - FM.computeStringWidth("0")) / 2 ;
        
//      and request a redraw...
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                draw() ;
            }
        });
    }
    
        // Called when game timer fires.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (board.isFinished())
            GameTimer.stop(); 
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Date now = new Date() ; // Gives the current date
                long diff = now.getTime() - GameStarted.getTime();
                String S = String.format("%02d:%02d:%02d", 
                                     (diff / 3600000 % 60), 
                                     (diff / 60000 % 60), 
                                     (diff / 1000 % 60));
                TimerLabel.setText( S );
            }
        });
    }
    
    // Called when we get or lose focus - so we need to redraw focus highlight
    void focusEvent() {
        draw() ;
    }

   //Actually draws the grid
     private void draw() {
         double x, y ;
         
        GraphicsContext gc = getGraphicsContext2D();
        x = getWidth() ;
        y = getHeight() ;
        
        for (int i = 0; i<3; i++)
            for (int j = 0; j<3; j++) {
                if ((i+j)%2 == 1)
                    if (board.isFinished()) 
                        gc.setFill(Color.CHARTREUSE);
                    else
                        gc.setFill(Color.WHITE);
                else
                    if (board.isFinished()) 
                        gc.setFill(Color.LIGHTGREEN);
                    else
                        gc.setFill(Color.LIGHTGREY);
                gc.fillRect((x*i)/3, (y*j)/3, x/3, y/3);
        }
      
        // If in guess mode, grey the cell to indicate...
        if (board.GuessMode) {
            gc.setFill(Color.DARKGRAY);            
            gc.fillRect((x * board.CurrentCellX)/9, (y * board.CurrentCellY)/9, x/9, y/9) ;
        }
        
        // First the gridlines
        gc.setLineWidth(1);
//        gc.setStroke(Color.DARKGRAY);
//        for (int i = 1; i<9; i++)
//        {
//            int Offset = 1 ;
//            if (i%3 == 0)
//                Offset = 2 ;
//            
//            gc.strokeLine(0, ((y * i)/9) - Offset, x, ((y * i)/9) - Offset);
//            gc.strokeLine(((x * i)/9) - Offset, 0, ((x * i)/9) - Offset, y);
//        }
//        gc.setStroke(Color.WHITE);
//        for (int i = 1; i<9; i++)
//        {
//            int Offset = 1 ;
//            if (i%3 == 0)
//                Offset = 2 ;
//
//            gc.strokeLine(0, ((y * i)/9) + Offset, x, ((y * i)/9) + Offset);
//            gc.strokeLine(((x * i)/9) + Offset, 0, ((x * i)/9) + Offset, y);
//        }
        gc.setStroke(Color.BLACK);
        for (int i = 1; i<9; i++)
        {
//            if (i%3 == 0)
//                gc.setLineWidth(3);   
//            else
                gc.setLineWidth(1);   
                
            gc.strokeLine(0, (y * i)/9, x, (y * i)/9);
            gc.strokeLine((x * i)/9, 0, (x * i)/9, y);
        }
        
        // now the numbers
        for (int i=0; i< 9; i++)
            for (int j=0; j< 9; j++) {
                if (board.getCellValue(i, j) != 0) {
                    // Board has a value
                    if ( board.isError(i,j) )
                        gc.setFill(Color.RED) ;
                    else if ( board.isSameValAsCurrent(i, j))
                        gc.setFill(new Color(0, .75, 0, 1)) ;
                    else if (board.isBase(i, j))
                        gc.setFill(Color.BLACK);
                    else
                        gc.setFill(Color.BLUE);

                    gc.setFont(MainFont);
                    gc.fillText(String.format("%d", board.getCellValue(i, j)), ((x * i)/9) + XMainOff, ((y * j)/9) + YMainOff);
                } else {
                    // Board is empty - does it have a guess?
                    if (GuessFont != null) {
                        gc.setFont(GuessFont);
                        gc.setFill(Color.BLUE);
                            
                        for (int k = 1; k<10; k++) {
                            if (board.getGuesses(i, j, k)) {
                                gc.fillText(String.format("%d", k), 
                                        ((x * (i + ((double)((k-1) % 3)/3.0)))/9) + XGuessOff , 
                                        ((y * (j + (double)(((k-1) / 3)/3.0)))/9) + YGuessOff);                    
                            }
                        }
                    }
                }
            }

        // now highlight the current cell and indicate if we have focus
        if (isFocused()) {
            gc.setLineWidth(3);
            gc.setStroke(Color.BLUE) ;         
        } else {
            gc.setLineWidth(1);
            gc.setStroke(Color.DARKBLUE) ;
        }
        gc.strokeRect((x * board.CurrentCellX)/9, (y * board.CurrentCellY)/9, x/9, y/9) ;
        
    }

     // Event handler for keyboard events
    void keyPressed(KeyEvent k) {
        switch (k.getCode())
        {
            case DIGIT1:
                numberPressed(1);
                break ;
            case DIGIT2:
                numberPressed(2);
                break ;
            case DIGIT3:
                numberPressed(3);
                break ;
            case DIGIT4:
                numberPressed(4);
                break ;
            case DIGIT5:
                numberPressed(5);
                break ;
            case DIGIT6:
                numberPressed(6);
                break ;
            case DIGIT7:
                numberPressed(7);
                break ;
            case DIGIT8:
                numberPressed(8);
                break ;
            case DIGIT9:
                numberPressed(9);
                break ;
            case TAB:
                ; // get rid of focus
                break ;
            case DELETE:
            case BACK_SPACE:
                numberPressed(0);
                break ;
            case LEFT:
                if (board.CurrentCellX > 0)
                    board.CurrentCellX --;
                draw() ;
                break ;
            case RIGHT:
                if (board.CurrentCellX < 8)
                    board.CurrentCellX ++;
                draw() ;
                break ;
            case UP:
                if (board.CurrentCellY > 0)
                    board.CurrentCellY --;
                draw() ;
                break ;
            case DOWN:
                if (board.CurrentCellY < 8)
                    board.CurrentCellY ++;
                draw() ;
                break ;
            case H:
                board.toggleCurrentCellHighlight() ;
                draw() ;
                break ;
            case SPACE:
                board.GuessMode = ! board.GuessMode ;
                if (GuessModeButton != null) {
                    GuessModeButton.setSelected(board.GuessMode) ;
                }
                draw() ;
                break ;
            case S:
                SudokuPuzzle.solve(SudokuPuzzle.Difficulty.FIENDISH, board);
                draw() ;
                break ;
            case G:
                SudokuPuzzle.setAllGuesses(board);
                draw() ;
                break ;
            default:
                
        }
    }

    private void numberPressed(int i) {
        if (!board.isBase(board.CurrentCellX, board.CurrentCellY)) {
            if (i == 0) {
                if (board.GuessMode)
                {
                    board.setAllGuesses(board.CurrentCellX, board.CurrentCellY, true);
                    draw() ;
                } else {
                    board.setCurrentCell(0) ;
                    draw() ;                                
                }
            } else 
            if (board.GuessMode)
            {
                if (board.getCurrentCell() == 0) {
                    board.toggleCurrentCellGuess(i);
                    draw() ;
                }
            } else {
                board.setCurrentCell(i) ;
                draw() ;
            }
        }
        this.requestFocus();
    }

    void ButtonHandler(javafx.event.ActionEvent A) {
        Button B = (Button) A.getSource() ;

        switch (B.getText())
        {
            case "1":
                numberPressed(1);
                break ;
            case "2":
                numberPressed(2);
                break ;
            case "3":
                numberPressed(3);
                break ;
            case "4":
                numberPressed(4);
                break ;
            case "5":
                numberPressed(5);
                break ;
            case "6":
                numberPressed(6);
                break ;
            case "7":
                numberPressed(7);
                break ;
            case "8":
                numberPressed(8);
                break ;
            case "9":
                numberPressed(9);
                break ;
        }
    }
    
    void ButtonHandlerClear(javafx.event.ActionEvent A) {
        numberPressed(0);
    }

    void ButtonHandlerGuessMode(javafx.event.ActionEvent A) {
        board.GuessMode = ! board.GuessMode ;
        if (GuessModeButton != null) {
            GuessModeButton.setSelected(board.GuessMode) ;
        }
        draw() ;
        this.requestFocus();
    }

    void DiffMenuHandler(javafx.event.ActionEvent A, SudokuPuzzle.Difficulty difficulty) {
        CheckMenuItem C; 
        CurrentDiff = difficulty ;
        if (DiffMenu != null)
            for(MenuItem M : DiffMenu.getItems()) {
                if (M instanceof CheckMenuItem)
                {
                    C = (CheckMenuItem) M ;
                    if (C == A.getSource()) 
                        C.setSelected(true);
                    else
                        C.setSelected(false);
                }
            }
    }

    void FileMenuHandler(javafx.event.ActionEvent A, String n) {
        switch (n)
        {
            case "N":
                SudokuPuzzle.newGame(CurrentDiff, board);
                StartGameTimer() ;
                draw() ;
                break ;
            case "O":
                break ;
            case "S":
                break ;
            case "X":               
                Platform.exit();
                System.exit(0);
                break ;
        }
    }

    private void StartGameTimer() {
        GameStarted = new Date() ; // sets to current time
        GameTimer.start();
    }

}
