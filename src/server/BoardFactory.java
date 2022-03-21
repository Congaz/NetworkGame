package server;

public class BoardFactory {

    // Required board size
    private final static int NUM_OF_COLS = 20;
    private final static int NUM_OF_ROWS = 20;
    // Index range of boards created by random.
    private final static int RANDOM_MIN = 0;
    private final static int RANDOM_MAX = 2;

    public BoardFactory() {
    }

    /**
     * Verifies integrity of all boards.
     * Throws IllegalStateException on board fail.
     *
     * @throws IllegalStateException
     */
    public static void verfifyAll() throws IllegalStateException {
        boolean finished = false;
        int index = 0;
        int end = 99999;
        while (index < end && !finished) {
            try {
                createBoardByIndex(index++);
            }
            catch (IllegalArgumentException e) {
                // Thrown when index is out of bounds = No more boards to check.
                finished = true;
            }
            catch (IllegalStateException e) {
                // Thrown when board failed size requirements.
                throw e; // Re-trhow.
            }
        }
    }

    /**
     * Converts board to string where each row is seperated by comma.
     * Resulting string starts and ends without comma.
     * No integrity check of board is made.
     * @param board
     * @return
     */
    public static String convertBoard2String(String[] board) {
        StringBuilder sb = new StringBuilder();
        for (String row : board) {
            if (sb.length() > 0) sb.append(",");
            sb.append(row);
        }
        return sb.toString();
    }

    /**
     * Creates and returns a board chosen by random.
     * Throws IllegalArgumentException if index is out of range.
     * Throws IllegalStateException if created board is invalid.
     *
     * @return
     */
    public static String[] createRandomBoard() throws RuntimeException {
        return createBoardByIndex(getRandomSignedInt(RANDOM_MIN, RANDOM_MAX));
    }

    /**
     * Creates and returns board by passed index.
     * Throws IllegalArgumentException if index is out of range.
     * Throws IllegalStateException if created board is invalid.
     *
     * @param boardIndex
     * @return
     * @throws RuntimeException
     */
    public static String[] createBoardByIndex(int boardIndex) throws RuntimeException {
        String[] board = createBoardByIndexHelper(boardIndex);
        verifyBoardSize(board, boardIndex);
        return board;
    }

    /**
     * Creates and returns board by passed index.
     * Throws IllegalArgumentException if index is out of range.
     *
     * @param boardIndex
     * @return
     * @throws IllegalArgumentException
     */
    private static String[] createBoardByIndexHelper(int boardIndex) throws IllegalArgumentException {
        // A board must be 20x20 cols/rows in size.
        switch (boardIndex) {
            case 0:
                return new String[]{
                    "wwwwwwwwwwwwwwwwwwww",
                    "w        ww        w",
                    "w w  w  www w  w  ww",
                    "w w  w   ww w  w  ww",
                    "w  w               w",
                    "w w w w w w w  w  ww",
                    "w w     www w  w  ww",
                    "w w     w w w  w  ww",
                    "w   w w  w  w  w   w",
                    "w     w  w  w  w   w",
                    "w ww ww        w  ww",
                    "w  w w    w    w  ww",
                    "w        ww w  w  ww",
                    "w         w w  w  ww",
                    "w        w     w  ww",
                    "w  w              ww",
                    "w  w www  w w  ww ww",
                    "w w      ww w     ww",
                    "w   w   ww  w      w",
                    "wwwwwwwwwwwwwwwwwwww"
                };
            case 1:
                return new String[]{
                    "wwwwwwwwwwwwwwwwwwww",
                    "w                  w",
                    "w w    wwwwww    w w",
                    "w  w            w  w",
                    "w   w    ww    w   w",
                    "w        ww        w",
                    "w     w      w     w",
                    "w      w    w      w",
                    "w  w    w  w   w   w",
                    "w  ww    ww   ww   w",
                    "w  ww    ww   ww   w",
                    "w  w    w  w   w   w",
                    "w      w    w      w",
                    "w     w      w     w",
                    "w        ww        w",
                    "w   w    ww    w   w",
                    "w  w            w  w",
                    "w w    wwwwww    w w",
                    "w                  w",
                    "wwwwwwwwwwwwwwwwwwww"
                };
             case 2:
                return new String[]{
                    "wwwwwwwwwwwwwwwwwwww",
                    "w                  w",
                    "w     wwwwww       w",
                    "w     w      w     w",
                    "w     w       w    w",
                    "w     w      w     w",
                    "w     wwwww w      w",
                    "w                  w",
                    "w     wwwwwwww     w",
                    "w        ww        w",
                    "w        ww        w",
                    "w     wwwwwwww     w",
                    "w                  w",
                    "w      wwwwww      w",
                    "w     w            w",
                    "w     wwwwwwww     w",
                    "w            w     w",
                    "w      wwwwww      w",
                    "w                  w",
                    "wwwwwwwwwwwwwwwwwwww"
                };

            case 999:
                // Template
                return new String[]{
                    "wwwwwwwwwwwwwwwwwwww",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "w                  w",
                    "wwwwwwwwwwwwwwwwwwww"
                };
            default:
                throw new IllegalArgumentException("Board index out of range.");
        }
    }

    /**
     * Verifies board size.
     *
     * @param board
     * @throws IllegalStateException
     */
    private static void verifyBoardSize(String[] board, int boardIndex) throws IllegalStateException {
        if (board.length != NUM_OF_ROWS) {
            throw new IllegalStateException(
                    "Board (index #" + boardIndex + ") is invalid. " +
                            "Rows required: " + NUM_OF_ROWS + " Rows present: " + board.length
            );
        } else {
            // Check length of each row
            boolean passed = true;
            int i = 0;
            while (i < board.length && passed) {
                if (board[i].length() != NUM_OF_COLS) {
                    passed = false;
                } else {
                    i++;
                }
            }
            // Check if board passed col inspection.
            if (!passed) {
                throw new IllegalStateException(
                        "Board (index #" + boardIndex + ") is invalid. " +
                                "Cols required: " + NUM_OF_COLS + " " +
                                "Cols present in row(" + (i + 1) + "): " + board[i].length()
                );
            }
        }
    }

    /**
     * Returns random signed integer.
     * Pre: max is larger than min.
     *
     * @param min Signed minimum value (inclusive).
     * @param max Signed maximum value (inclusive).
     * @return
     */
    private static int getRandomSignedInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }


}
