/*
Name:       Dale Hendricks
Section:    02
Instructor: Sarah Foss
Date:       Nov, 2023
Description: Given a 10x10 board, plays battleship to the best of its ability.  Each turn it reads the board looking
    for a "wounded" ship, denoted by an 'X'.  If it finds one it fires at spaces adjacent to the 'X', until there are no
    more visible wounded ships.  If there are no wounded ships, it builds an array summing the number of possible
    placements that would put a ship in each cell, then targets the highest number.

    TODO()
    - New hunting algorithm, can we look ahead at possibilities and determine the 'delta certainty' of each shot?
    - Certainty meaning how sure we are any given space is either occupied or not.
    - Is it always best to shoot at the highest valued cell, or is it sometimes more valuable to increase our
    overall board certainty?

*/

import java.util.Arrays;
import java.util.HashMap;

public class Admiral {
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

        //Check for ships that have been hit but not sunk.
        boolean containsWoundedShip = false;

        //Assemble fleet
        HashMap<Character, Integer> fleet = new HashMap<>();
        fleet.put('1', 2);
        fleet.put('2', 3);
        fleet.put('3', 3);
        fleet.put('4', 4);
        fleet.put('5', 5);

        //Remove any sunk ships fromm the fleet
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') {
                    containsWoundedShip = true;
                    break;
                } else if (Character.isDigit(cell)) {
                    fleet.remove(cell);
                }
            }
        }

        int[] shot;
        //Choose the hunt/kill method based on whether there is a wounded ship
        if (containsWoundedShip) {
            shot = kill(board);
        } else {
            shot = radar(board, fleet);
        }

        //Format the int coordinates into a string for return
        return formatGuess(shot[0], shot[1]);
    }


    //try and finish off a wounded ship
    public static int[] kill(char[][] board) {
        int[] coords = {0, 0};
        int[][] wounds = new int[15][2];
        int index = 0;

        //Gather coordinates of any hits to fire around.
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 'X') {
                    wounds[index][0] = row;
                    wounds[index][1] = col;
                    index++;
                }
            }
        }

        String up = "";
        String down = "";
        String left = "";
        String right = "";

        //which cardinal directions goes the longest before terminating at a wall or other revealed square?
        for (int[] wound : wounds) {

            int row = wound[0];
            int col = wound[1];

            for (int[] otherWound : wounds) {
                int otherRow = otherWound[0];
                int otherCol = otherWound[1];

                //if wound and otherWound are the same, skip
                if (row - otherRow == 0 && col - otherCol == 0) {
                    continue;

                //if they are within one of each other horizontally OR vertically
                } else if (Math.abs(row - otherRow) == 1 ^ Math.abs(col - otherCol) == 1) {
                    //On same row and adjacent
                    if (Math.abs(row - otherRow) == 1) {
                        if (row > otherRow && row + 1 < board.length) {
                            if (board[row + 1][col] == '.') {
                                return new int[] {row + 1, col};
                            } else if (otherRow > 0 && board[otherRow - 1][col] == '.') {
                                return new int[] {otherRow - 1, col};
                            }
                        } else if (row < otherRow && row > 0) {
                            if (board[row - 1][col] == '.') {
                                return new int[] {row - 1, col};
                            } else if (otherRow + 1 < board.length && board[otherRow + 1][col] == '.') {
                                return new int[] {otherRow + 1, col};
                            }
                        }
                    } else {
                        if (col > otherCol && col + 1 < board.length) {
                            if (board[row][col + 1] == '.') {
                                return new int[] {row, col + 1};
                            } else if (otherCol > 0 && board[row][otherCol - 1] == '.') {
                                return new int[] {row, otherCol - 1};
                            }
                        } else if (col < otherCol && col > 0) {
                            if (board[row][col - 1] == '.') {
                                return new int[] {row, col - 1};
                            } else if (otherCol + 1 < board.length && board[row][otherCol + 1] == '.') {
                                return new int[] {row, otherCol + 1};
                            }
                        }
                    }
                    //System.out.println("two adjacent but no enhanced shot?");
                }
            }

            if (row == 0 && col == 0 && !Arrays.equals(wounds[0], new int[]{0, 0})) {
                continue;
            }

            if (((row > 0 && board[row - 1][col] != '.') || row == 0) &&
                    ((row < board.length - 1 && board[row + 1][col] != '.') || row == board.length - 1) &&
                    ((col > 0 && board[row][col - 1] != '.') || col == 0) &&
                    (((col < board.length - 1 && board[row][col + 1] != '.')) || col == board.length - 1)) {
                continue;
            }

            if (row > 0) {
                for (int boardRow = row; boardRow >= 0; boardRow--) {
                    if (board[boardRow][col] == '.' || up.isEmpty()) {
                        up += board[boardRow][col];
                    } else {
                        break;
                    }
                }
            }

            if (col < board.length - 1) {
                for (int boardCol = col; boardCol < board.length; boardCol++) {
                    if (board[row][boardCol] == '.' || right.isEmpty()) {
                        right += board[row][boardCol];
                    } else {
                        break;
                    }
                }
            }

            if (col > 0) {
                for (int boardCol = col; boardCol >= 0; boardCol--) {
                    if (board[row][boardCol] == '.' || left.isEmpty()) {
                        left += board[row][boardCol];
                    } else {
                        break;
                    }
                }
            }


            if (row < board.length - 1) {
                for (int boardRow = row; boardRow < board.length; boardRow++) {
                    if (board[boardRow][col] == '.' || down.isEmpty()) {
                        down += board[boardRow][col];
                    } else {
                        break;
                    }
                }
            }


            if (up.length() > right.length() && up.length() > left.length() && up.length() > down.length()) {
                return new int[]{row - 1, col};
            } else if (right.length() > down.length() && right.length() > left.length()) {
                return new int[]{row, col + 1};
            } else if (down.length() > left.length()) {
                return new int[]{row + 1, col};
            } else {
                return new int[]{row, col - 1};
            }
        }

        return coords;
    }


    public static int[] radar(char[][] board, HashMap<Character, Integer> ships) {
        int[][] probabilityBoard = new int[10][10];


        //This is a horror for big O notation.
        //For each ship, find all valid placements and increment the corresponding cells on the probability board.
        for (int ship : ships.values()) {
            for (int row = 0; row < board.length; row++) {
                //Just join to string each row and filter out the bits we don't want
                String fullRow = Arrays.toString(board[row]).replaceAll("[, \\[\\]]", "");

                for (int col = 0; col < board[row].length - ship + 1; col++) {
                    //looking for substrings that are only comprised of '.', of the length of our ship.
                    String space = fullRow.substring(col, col + ship);

                    if (space.matches("^\\.*$")) {
                        for (int i = 0; i < ship; i++) {
                            //increment each space the ship would cover
                            probabilityBoard[row][col + i]++;
                        }
                    }
                }
            }

            //do it all again but vertically
            for (int col = 0; col < board.length; col++) {
                String fullCol = "";
                //little trickier to get the column string, but all the rest is the same
                for (int row = 0; row < board.length; row++) {
                    fullCol += board[row][col];
                }
                for (int row = 0; row < board.length - ship + 1; row++) {
                    String space = fullCol.substring(row, row + ship);
                    if (space.matches("^\\.*$")) {
                        for (int i = 0; i < ship; i++) {
                            probabilityBoard[row + i][col]++;
                        }
                    }
                }
            }
        }

        int[] coords = {0, 0};

        int max = 0;
        //iterate over each space, looking for the highest number, and assign that to our coordinates
        for (int row = 0; row < probabilityBoard.length; row++) {
            for (int col = 0; col < probabilityBoard[row].length; col++) {
                if (probabilityBoard[row][col] > max) {
                    max = probabilityBoard[row][col];
                    coords[0] = row;
                    coords[1] = col;
                }
            }
        }

        return coords;
    }


    //change indices into proper guess format
    public static String formatGuess(int row, int col) {
        //just changes my x,y zero-indexed coordinates into corresponding battleship ones
        String guess = "";
        guess += (char) ('A' + row);
        guess += String.valueOf(col + 1);
        return guess;
    }
}
