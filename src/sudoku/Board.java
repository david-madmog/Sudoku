/*
 * Class for a board as a whole
 *
 * (C) David Poirier July 2013
*/
package sudoku;

import java.util.Iterator;
/**
 *
 * @author davidp
 */
public class Board implements Iterable<Cell> {
    private Cell Cells[][] ;
    
    public int CurrentCellX, CurrentCellY ; 
    public boolean GuessMode ;
    private int CurrentHighlight ;
    
    Board () {
        Cells = new Cell[9][9] ;
        for (int i=0; i< 9; i++)
            for (int j=0; j< 9; j++)
                Cells[i][j] = new Cell(i, j) ;
    }

    public String serialiseGrid() {
        String S = "";
        for (int i=0; i< 9; i++)
            for (int j=0; j< 9; j++) {
                if (Cells[i][j].Value > 0) {
                    S = S + (String.format("%d", Cells[i][j].Value)) ;
                } else {
                    S = S + (" ") ;
                }
            }
        return S ;
    }
    
    public void deserialiseGrid(String BString) {
        char V ;
        for (int i=0; i< 9; i++)
            for (int j=0; j< 9; j++) {
               V = BString.charAt((i*9) + j) ;
               if (V == ' ') {
                   Cells[i][j].Value = 0 ;
                   Cells[i][j].isBase = false ;
               } else {
                   Cells[i][j].isBase = true ;                   
                   Cells[i][j].Value = V - '0' ;
               }
                   
            }
    }
           
    public int getCellValue(int x, int y) {
        return Cells[x][y].Value ;
    }

    public boolean getGuesses(int x, int y, int z) {
        return Cells[x][y].Guesses[z] ;
    }
    
    public void toggleCurrentCellGuess(int z) {
        Cells[CurrentCellX][CurrentCellY].Guesses[z] = ! Cells[CurrentCellX][CurrentCellY].Guesses[z]  ;        
    }

    public boolean isBase(int x, int y) {
        return Cells[x][y].isBase ;
    }

    public boolean isError(int x, int y) {
        int v ;
        v = Cells[x][y].Value ;
        if (v == 0)
            return false ;
        for (int i=0; i< 9; i++) {
            if ((i != x) && (Cells[i][y].Value == v))
                return true ;
            if ((i != y) && (Cells[x][i].Value == v))
                return true ;
        }
        
        int xx = ((int)(x/3))*3 ;
        int yy = ((int)(y/3))*3 ;
        for (int i=0; i<3; i++ )
            for (int j=0; j<3; j++ ) 
                if (!((i + xx == x)
                        && (j + yy == y))
                        && (Cells[i+xx][j+yy].Value == v))
                    return true ;
        
        return false ;
    }

    public boolean isSameValAsCurrent(int x, int y) {
        return (Cells[x][y].Value == CurrentHighlight) ;
    }

    public void setCurrentCell(int V) {
        setCell(CurrentCellX, CurrentCellY, V) ;
    }

    public void setCell(Cell cell, int V) {
        setCell(cell.x, cell.y, V) ;
    }
    
    public void setCell(int x, int y, int V) {
        Cells[x][y].Value = V ;
        setOtherCellGuesses(x, y) ;
    }
    
    public void setCellBare(int x, int y, int V) {
        Cells[x][y].Value = V ;
    }
    
    public void setCellGuess(int x, int y, int V, boolean B) {
        Cells[x][y].Guesses[V] = B ;
    }
    
    public void setOtherCellGuesses(Cell c) {
        setOtherCellGuesses(c.x, c.y);
    }

    public void setOtherCellGuesses(int x, int y) {
        int V = Cells[x][y].Value ;
        
        // And wipe out guesses for all relevant cells
        for (int i=0; i< 9; i++) {
            Cells[i][y].Guesses[V] = false ;
            Cells[x][i].Guesses[V] = false ;
        }
        
        int xx = ((int)(x/3))*3 ;
        int yy = ((int)(y/3))*3 ;
        for (int i=0; i<3; i++ )
            for (int j=0; j<3; j++ ) 
                Cells[i+xx][j+yy].Guesses[V] = false ;
    }

    public void toggleCurrentCellHighlight() {
        if (CurrentHighlight == Cells[CurrentCellX][CurrentCellY].Value)
            CurrentHighlight = 0 ;
        else
            CurrentHighlight = Cells[CurrentCellX][CurrentCellY].Value ;
    }
    
    public int getCurrentCell() {
        return Cells[CurrentCellX][CurrentCellY].Value ;
    }
    
    public boolean isFinished() {
        for (int i=0; i<9; i++ )
            for (int j=0; j<9; j++ ) {
                if (isError(i, j) || (Cells[i][j].Value == 0))
                    return false ;
            }
        return true ;
    }

    public boolean hasAnyGuess(int x, int y) {
        return Cells[x][y].hasAnyGuess() ;
    }
    
    public int countGuesses(int x, int y) {
        return Cells[x][y].countGuesses() ;
     }
    
    public void setAllGuesses(int x, int y, boolean B) {
        Cells[x][y].setAllGuesses(B);
    }
    
    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator(Cells);
    }

//    public boolean getGuessesPairs(int i, int j, int k) {
//        if (Cells[i][j].GuessesPairs == null)
//            return false ;
//        if (Cells[i][j].Value != 0)
//            return false ;
//        
//        return Cells[i][j].GuessesPairs[k] ;
//    }

    int getFirstGuessPair(int i, int j) {
        if (Cells[i][j].Value != 0)
            return 0 ;
        
        return Cells[i][j].getFirstGuessPair() ;
    }
    
}
