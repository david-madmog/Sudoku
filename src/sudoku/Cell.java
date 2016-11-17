/*
 * Class for a single cell on a board
 *
 * (C) David Poirier July 2013
*/
package sudoku;

/**
 *
 * @author davidp
 */
public class Cell {
    public int Value ;
    public boolean isBase ;
    public boolean[] Guesses;
    public int x, y ;
//    public boolean[] GuessesPairs;
    
    Cell(int i, int j) {
        Guesses = new boolean[10] ;
        setAllGuesses(false) ;
        x = i ;
        y = j ;
    }
    
/**
 * Sets all the guesses for the cell to a particular value
 * 
 * @param  B    the value to set the guesses to
 * @see         The Light
 */
    public final void setAllGuesses(boolean B) {
        for(int i=1; i<10; i++ )
            Guesses[i] = B ;
    }

 /**
 * Determines if any of the guesses for the current cell are true
 * 
 * @return      if there are any guesses set to true
 */
   public boolean hasAnyGuess() {
        boolean G = false ;
        for(int i=1; i<10; i++ )
            G = G || Guesses[i]  ;
        return G ;
    }

 /**
 * Determines the number of guesses for the current cell that are true
 * 
 * @return      the number of guesses set to true
 */
    public int countGuesses() {
        int G = 0 ;
        for(int i=1; i<10; i++ )
            if (Guesses[i]) 
                G++ ;
        return G ;
    }

//    void computeGuessPairs() {
//        GuessesPairs = new boolean[100] ;
//        for (int i=1; i<100; i++) {
//            GuessesPairs[i] = false ;
//        }
//        
//        for (int i=1; i<10; i++) {
//            if (Guesses[i]) {
//                for (int j=i+1; j<10; j++) {
//                    if (Guesses[j]) 
//                        GuessesPairs[i*10 + j] = true ;
//                }
//            }
//        }
//    }

    int getFirstGuessPair() {
        for (int i=1; i<10; i++) {
            if (Guesses[i]) {
                for (int j=i+1; j<10; j++) {
                    if (Guesses[j]) 
                        return (i*10 + j) ;
                }
            }
        }
        return 0 ;
    }
}
