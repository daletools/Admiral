import java.util.Arrays;

public class Admiral {

    //higher number = more possible ways a ship can be on a given square

    private static int[][] contacts = new int[10][10];
    private static int[][] board = new int[10][10];

    private static boolean[][] misses = new boolean[10][10];

    private static int[] ships = new int[]{2, 3, 3, 4, 5};


    public static void main(String[] args) {
        //this is our board where we store hits
        //store misses as 'false' values
        //ship lengths, should be Zeroed out when destroyed
        //certainties
        double[][] certainties = new double[10][10];

        //BattleShipTools.randomBoard(board);

        //misses[4][4] = true;
        //misses[5][5] = true;
        //misses[6][6] = true;
        //misses[4][6] = true;

        //radar(board, contacts, misses, ships);

        printBoard(contacts);
        System.out.println("Highest value target");
        System.out.println(Arrays.toString(targetLock(contacts)));
        System.out.println("Boardsum = " + boardSum(contacts));
    }

    public static String fire(char[][] board) {
        /*
            ‘.’ – no guess yet
            ‘O’ – miss
            ‘X’ – hit
            ‘1’ – Patrol Boat, has length 2, and has been sunk
            ‘2’ – Submarine, has length 3, and has been sunk
            ‘3’ – Destroyer, has length 3, and has been sunk
            ‘4’ – Battleship, has length 4, and has been sunk
            ‘5’ – Aircraft carrier, has length 5, and has been sunk
         */

        boolean containsWoundedShip = false;
        boolean[] deadShips = new boolean[5];

        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') {
                    containsWoundedShip = true;
                    break;
                } else if (Character.isDigit(cell)) {
                    deadShips[cell - 1] = true;
                }
            }
        }


        int[] shot;

        if (containsWoundedShip) {
            shot = kill(board, deadShips);
        } else {
            shot = hunt(board, deadShips);
        }

        return formatGuess(shot[0], shot[1]);
    }

    //if not wounded ships, search for one
    public static int[] hunt(char[][] board, boolean[] deadShips) {
        return new int[]{0, 0};
    }

    //try and finish off a wounded ship
    public static int[] kill(char[][] board, boolean[] deadShips) {
        return new int[]{0, 0};
    }


    //change indices into proper guess format
    public static String formatGuess(int row, int col) {
        String guess = "";
        guess += (char) ('A' + row - 1);
        guess += String.valueOf(col);
        return guess;
    }




    public static void printBoard(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    //updates the array of contacts with all valid ship positions
    public static void radarUpdate(int[][] board, int[][] contacts, boolean[][] misses, int[] ships) {
        long time = System.currentTimeMillis();

        //for each live ship
        for (int ship : ships) {
            if (ship == 0) continue;
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
                                    contacts[row][col + shipSpace] += 1;
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
                                    contacts[row + shipSpace][col] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - time) / 1000);
        permute(ships, board, new int[]{1, 1});
    }

    public static int[] targetLock(int[][] radar) {
        radarUpdate(board, contacts, misses, ships);
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


    public static double odds(int length, int value) {

        return 0.0;
    }

    public static int boardSum(int[][] board){
        int sum = 0;
        for (int[] row : board) {
            for (int val : row) {
                sum += val;
            }
        }
        return sum;
    }

    public static double certainty (double odds) {
        //quadratic equivalent 0.4 * x^2 - 4x + 100, if a curve ends up fitting better
        return 2.0 * Math.abs((100 * odds) - 50);
    }




    //experimental
    public static void permute(int[] ships, int[][] board, int[] coordinate) {
        int sum = 0;
        for (int firstShip = 0; firstShip < ships.length; firstShip++) {
            if (ships[firstShip] == 0) {
                continue;
            }
            for (int secondShip = 0; secondShip < ships.length; secondShip++) {
                if (secondShip == firstShip || ships[secondShip] == 0) {
                    continue;
                }
                for (int thirdShip = 0; thirdShip < ships.length; thirdShip++) {
                    if (thirdShip == secondShip || thirdShip == firstShip || ships[thirdShip] == 0) {
                        continue;
                    }
                    for (int fourthShip = 0; fourthShip < ships.length; fourthShip++) {
                        if (fourthShip == thirdShip || fourthShip == secondShip ||
                                fourthShip == firstShip || ships[fourthShip] == 0) {
                            continue;
                        }
                        for (int fifthShip = 0; fifthShip < ships.length; fifthShip++) {
                            if (fifthShip == fourthShip || fifthShip == thirdShip ||
                                    fifthShip == secondShip || fifthShip == firstShip ||
                                    ships[fifthShip] == 0) {
                                continue;
                            }
                            sum++;
                        }
                    }
                }
            }
        }
        System.out.print(sum);
    }
}