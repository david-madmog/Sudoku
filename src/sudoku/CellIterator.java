/*
 * Iterator class for simple looping through array of cells held by a board
 * 
 * (C) David Poirier July 2013
 *
*/
package sudoku;

import java.util.Iterator;

/**
 *
 * @author davidp
 */
public class CellIterator implements Iterator<Cell>{
    private Cell Cells[][] ;

    private int i, j;
 
    public CellIterator(Cell vCells[][]) {
        Cells = vCells;
        i = 0 ;
        j = 0 ;
    }
 
    @Override
    public boolean hasNext() {
        return ((i < 9) && (j < 9)) ;
    }

    @Override
    public Cell next() {
        Cell c = Cells[i][j] ;
        i++ ;
        if (i >= 9) {
            i = 0 ;
            j++ ;
        }
        return c ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Iterator does not support removal"); 
    }
    
}
