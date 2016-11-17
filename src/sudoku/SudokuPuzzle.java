/*
 * This module is essentially static code that will generate (and solve) a 
 * sudoku puzzle.
 *
 * (C) David Poirier July 2013
*/
package sudoku;

import java.io.Console;
import java.util.Random;

/**
 *
 * @author davidp
 */
public class SudokuPuzzle {
    private static Random Rnd = new Random();

    public enum Difficulty {
        VERY_EASY (1),
        EASY (2),
        MEDIUM (3),
        HARD (4),
        FIENDISH (5) ,
        IMPOSSIBLE (99) ;
        
        private int Ord ;
        Difficulty(int O) {this.Ord = O;}
        static int comp(Difficulty D1, Difficulty D2) {
            if (D1.Ord < D2.Ord) return -1 ;
                    else if (D1.Ord > D2.Ord) return 1 ;
                    else return 0 ;
        }
        int comp(Difficulty D2) { return comp(this, D2) ; }
    }
    
    SudokuPuzzle() {
        // only ever static...
    }
    
    public static void newGame(Difficulty Diff, Board B) {
        
        // First, generate a complete solution
        generateSolvedGrid (B) ;
        removeCells(Diff, B) ;
    
        // And lock in
        for (Cell cell: B) {
            cell.setAllGuesses(false);
            if (cell.Value != 0)
                cell.isBase = true ;
            else
                cell.isBase = false ;
        }        
    }

    private static void generateSolvedGrid(Board B) {
        // First, clear out the grid
        for (Cell cell: B) {
            cell.Value = 0 ;
            cell.setAllGuesses(true);
        }
        
        // We can use the handy guesses for each cell to help track the allowable values
        int x, y, z = 0 ;
        int V ;
        while (z < 81) {
            x = z % 9 ;
            y = z / 9 ;
            
            // See if there is an allowable guess for the current cell
            if (B.hasAnyGuess(x, y)) {
                // Find and apply one
                V = 0 ;
                while (V == 0) {
                    V = Rnd.nextInt(9) + 1 ;
                    if (!B.getGuesses(x, y, V)) 
                        V = 0 ;
                }
                // Set the cell, remember we've tried it and see if it conflicts
                B.setCellBare(x, y, V); 
                B.setCellGuess(x, y, V, false) ;
                if (!B.isError(x, y)) {
                    // All cool so far, so move on to the next square
                    // Otherwise we'll try another one
                    z++ ;
                }
            } else {
                // there are no options, so we need to backtrack
                // Reset current cell as if never touched...
                B.setCellBare((z % 9),(z / 9), 0) ;
                B.setAllGuesses((z % 9),(z / 9), true);
                z -- ;
            }
       }        
    }
      
    private static void removeCells(Difficulty Diff, Board B) {
        String Stash ;
        int BoredomCounter ;
        Difficulty NeededDiff ;
        //Difficulty PastNeededDiff = Difficulty.EASY ;

        if (Diff == Difficulty.VERY_EASY)
            BoredomCounter = 1 ;
        else
            BoredomCounter = 5 ;
            
        while (BoredomCounter > 0) {
            // Keep removing cells at random untill...
            // * We have used at least the requred diffuclty
            // * We get bored
            
            // Stash Board
            Stash = B.serialiseGrid() ;
            
            // Remove a cell at random
            int x = Rnd.nextInt(9) ;
            int y = Rnd.nextInt(9) ;
            B.setCellBare(x, y, 0);

            // See if board is solvable, and if so, how hard it is to solve
            NeededDiff = solve(Diff, B) ;
            
            // Revert to how we were before removal
            B.deserialiseGrid(Stash);
            
            // If it's impossible, try again
            if (NeededDiff == Difficulty.IMPOSSIBLE) {
//                if (PastNeededDiff == Diff) {
                    // Impossible, but previous one was difficult enough
                    BoredomCounter -- ;
//                }
            } else {
                // Not impossible, so remember removal
                B.setCellBare(x, y, 0);
//                PastNeededDiff = NeededDiff ;
                if (Diff == Difficulty.VERY_EASY)
                    BoredomCounter = 1 ;
                else
                    BoredomCounter = 5 ;
            }
        }
    }
    
    public static Difficulty solve(Board B) {
        return solve(Difficulty.FIENDISH, B) ;
    }

    public static Difficulty solve(Difficulty Diff, Board B) {
        Difficulty DiffNeeded = Difficulty.EASY ;
        
        // First, set allowed possibilities in all empty cells
        setAllGuesses(B);
        
        boolean bDoneOne = true;
        while (bDoneOne)
        {
            bDoneOne = method1(B) ;
            if ((!bDoneOne) && (Diff.comp(Difficulty.MEDIUM) >= 0)) {
                bDoneOne = method2(B) ;
                if (DiffNeeded.ordinal()<Difficulty.MEDIUM.ordinal())
                    DiffNeeded = Difficulty.MEDIUM ;
            }
            if (!bDoneOne && (Diff.comp(Difficulty.HARD) >= 0)) {
                bDoneOne = method3(B) ;
                if (DiffNeeded.ordinal()<Difficulty.HARD.ordinal())
                    DiffNeeded = Difficulty.HARD ;
            }
            if (!bDoneOne && (Diff.comp(Difficulty.FIENDISH) >= 0)) {
                bDoneOne = method4(B) ;
                if (DiffNeeded.ordinal()<Difficulty.FIENDISH.ordinal())
                    DiffNeeded = Difficulty.FIENDISH ;
            }
        }
        // OK, if we've got here, it must be for one of the following cases:
        // 1. The puzzle is solved
        // 2. The puzzle can't be solved
        //  2a. There are no solutions
        //  2b. There is more than one solution
        //  2c. The solution needs a too difficult method
        // In either case 2, this is not a good outcome so we should reject
        if (B.isFinished())
            return DiffNeeded ;
        else
            return Difficulty.IMPOSSIBLE ;
    }
    
    //Method 1 If there is only one possible digit for a speciﬁc cell under the Sudoku rules, then
    //we may insert this digit.
    private static boolean method1(Board B) {
        boolean bDoneOne = false ;
        for (Cell cell: B) {
            if ((cell.Value == 0)
                    && (cell.countGuesses() == 1 ))
            {
                bDoneOne = true ;
                for(int k=1; k<10; k++ )
                    if (cell.Guesses[k]) 
                        B.setCell(cell, k) ;
            }
        }
        return bDoneOne ;
    }
    
    //Method 2 If in any row, column or box, there is only one possible cell for an unused digit,
    //then we may insert this digit.
    private static boolean method2(Board B) {
        int RowGuessesCount[] = new int[10] ;
        int ColGuessesCount[] = new int[10] ;
        
        boolean bDone = false ;
        // Now, go through each row(col) and see if there's guess in only one cell
        // Loop of row/col
        for (int i=0; i<9; i++) {
            
            // reset count of each pair
            for(int k=1; k<10; k++ ) {
                RowGuessesCount[k] = 0 ;
                ColGuessesCount[k] = 0 ;
            }
            
            // loop of cells in row/col
            for (int j=0; j<9; j++) {
                // Look at guesses in the cell to total up how many 
                // instances of each guess in the row/col
                for (int k=1; k<10; k++) 
                {
                    if ((B.getGuesses(i, j, k)) && (B.getCellValue(i, j) == 0))
                        RowGuessesCount[k] ++ ;
                    if ((B.getGuesses(j, i, k)) && (B.getCellValue(j, i) == 0))
                        ColGuessesCount[k] ++ ;
                }
            }
            
            // Now, see if there's any guess that appears exactly once
            for (int k=1; k<10; k++) {
                if (RowGuessesCount[k] == 1) {
                    for (int j=0; j<9; j++) {
                        if (B.getGuesses(i, j, k)) {
                            B.setCell(i, j, k);
                            bDone = true ;
                        }
                    }
                }
                if (ColGuessesCount[k] == 1) {
                    for (int j=0; j<9; j++) {
                        if (B.getGuesses(j, i, k)) {
                            B.setCell(j, i, k);
                            bDone = true ;
                        }
                    }
                }

            }
            
        }
        return bDone ;    
    }

    //Method 3 If two cells on a row have the same two candidates then any other cell on that
    //row cannot take those values, and we can therefore remove these possibilities from
    //their candidate list. This can also occur for more than two cells.        
    private static boolean method3(Board B) {
        boolean bDone = false ;
        int RowGuessesPairsCount[] = new int[100] ;
        int ColGuessesPairsCount[] = new int[100] ;
        
        // Now, go through each row(col) and see if there's a cell that only has two guesses.
        // If so, remember it to see if it's a pair that occurs in exactly 2 cells
        // Loop of row/col
        for (int i=0; i<9; i++) {
            
            // reset count of each pair
            for(int k=1; k<100; k++ ) {
                RowGuessesPairsCount[k] = 0 ;
                ColGuessesPairsCount[k] = 0 ;
            }
            
            // loop of cells in row/col
            for (int j=0; j<9; j++) {
                // Look at how many guesses in the cell, and if two total up how many 
                // instances of each guess pair in the row/col
                if (B.countGuesses(i, j) == 2) {
                    int k = B.getFirstGuessPair(i, j) ;
                    RowGuessesPairsCount[k] ++ ;
                }
                if (B.countGuesses(j, i) == 2) {
                    int k = B.getFirstGuessPair(j, i) ;
                    ColGuessesPairsCount[k] ++ ;
                }
            }

            // Now, see if there's any guess pairs that appear exactly twice
            for (int k=1; k<100; k++) {
                if (RowGuessesPairsCount[k] == 2) {
                    // OK, this pair appears exactly twice, so remove from all the other 
                    // cells where it appears
                    for (int j=0; j<9; j++) {
                        if (B.countGuesses(i, j) > 2) {
                            if (B.getGuesses(i, j, (k%10)) || B.getGuesses(i, j, (int)(k/10)))
                                bDone = true ;
                            B.setCellGuess(i, j, (k%10), false);
                            B.setCellGuess(i, j, (int)(k/10), false);
                        }
                    }
                }

                if (ColGuessesPairsCount[k] == 2) {
                    for (int j=0; j<9; j++) {
                        if (B.countGuesses(j, i) > 2) {
                            if (B.getGuesses(j, i, (k%10)) || B.getGuesses(j, i, (int)(k/10)))
                                bDone = true ;
                            B.setCellGuess(j, i, (k%10), false);
                            B.setCellGuess(j, i, (int)(k/10), false);
                        }
                    }
                }
            }
        }            
            
        return bDone ;    
    }
    
    //Method 4 Interactions between blocks, columns and rows can reduce the possible candidates for other cells. For example, 
    //in Figure, since 3 must occur in either position A or B, it cannot occur in any of the cells marked x.
    //
    // +---+---+---+
    // |   |x  |   |
    // |   |x  |   |
    // |   |x  |   |
    // +---+---+---+
    // |   |A52|   |
    // |   |   | 3 |
    // |   |B98|   |
    // +---+---+---+
    // |   |x  |   |
    // |   |x  |   |
    // |   |x  |   |
    // +---+---+---+
    private static boolean method4(Board B) {
        boolean bDone = false ;
            
        return bDone ;    
    }
    
    public static void setAllGuesses(Board B) {
        for (Cell cell: B) {
            if (cell.Value == 0) {
                cell.setAllGuesses(true);
            }
        }
        for (Cell cell: B) {
            if (cell.Value != 0) {
                B.setOtherCellGuesses(cell);
            }
        }
        
    }
        

    
}
