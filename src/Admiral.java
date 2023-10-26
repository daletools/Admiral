import java.util.Arrays;

public class Admiral {
    private static int[][] board;
    private static int[][] contacts;
    private static int[] ships;
    private static boolean[][] misses;

    public static void main(String[] args) {
        board = new int[10][10];
        misses = new boolean[10][10];
        contacts = new int[10][10];
        ships = new int[]{2, 3, 3, 4, 5};

        //BattleShipTools.randomBoard(board);

        radar(board, contacts, misses, ships);

        printBoard(board);
        System.out.println();
        printBoard(contacts);
        System.out.println();
        System.out.println(Arrays.toString(targetLock(contacts)));
    }

    public static void printBoard(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void radar(int[][] board, int[][] map, boolean[][] misses, int[] ships) {
        long time = System.currentTimeMillis();

        //for each live ship
        for (int ship : ships) {
            //for each row
            for (int row = 0; row < board.length; row++) {
                //for each column
                for (int col = 0; col < board[0].length; col++) {
                    //if the anchor square is empty
                    if (board[row][col] == 0) {
                        //if there is enough room to fit the ship on the board horizontally
                        if (col < board[0].length - ship) {
                            boolean valid = true;
                            for (int shipSpace = 0; shipSpace <= ship; shipSpace++) {
                                if (misses[row][col + shipSpace]) {
                                    valid = false;
                                    break;
                                }
                            }
                            //if all spaces are considered empty at this time, increment the ping on each one
                            if (valid) {
                                for (int shipSpace = 0; shipSpace <= ship; shipSpace++) {
                                    map[row][col + shipSpace] += 1;
                                }
                            }
                        }

                        //if there is enough room to fit the ship on the board vertically
                        if (row < board.length - ship) {
                            boolean valid = true;
                            for (int shipSpace = 0; shipSpace <= ship; shipSpace++) {
                                if (misses[row + shipSpace][col]) {
                                    valid = false;
                                    break;
                                }
                            }
                            //if all spaces are considered empty at this time, increment the ping on each one
                            if (valid) {
                                for (int shipSpace = 0; shipSpace <= ship; shipSpace++) {
                                    map[row + shipSpace][col] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - time) / 1000);
    }

    public static int[] targetLock(int[][] radar) {
        int[] coords = {0, 0};
        int max = 0;

        for (int row = 0; row < radar.length; row++) {
            for (int col = 0; col < radar[0].length; col++) {
                if (radar[row][col] > max) {
                    coords[0] = row;
                    coords[1] = col;
                    max = radar[row][col];
                }
            }
        }

        return coords;
    }

}