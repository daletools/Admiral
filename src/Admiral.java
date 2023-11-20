/*
Name:       Dale Hendricks
Section:    02
Instructor: Sarah Foss
Date:       Nov, 2023
Description: Given a 10x10 board, plays battleship to the best of its ability.  Each turn it reads the board looking
    for a "wounded" ship, denoted by an 'X'.  If it finds one it fires at spaces adjacent to the 'X', until there are no
    more visible wounded ships.  If there are no wounded ships, it builds an array summing the number of possible
    placements that would put a ship in each cell, then targets the highest number.

*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Admiral {
    public static String makeGuess(char[][] board) {
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
        ArrayList<Integer[]> wounds = new ArrayList<>();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                char cell = board[row][col];
                if (cell == 'X') {
                    containsWoundedShip = true;
                    wounds.add(new Integer[]{row, col});
                } else if (Character.isDigit(cell)) {
                    fleet.remove(cell);
                }
            }
        }

        int[] shot;
        //Choose the hunt/kill method based on whether there is a wounded ship
        if (containsWoundedShip) {
            shot = kill(board, wounds);
        } else {
            shot = hunt(board, fleet);
        }

        //Format the int coordinates into a string for return
        return formatGuess(shot[0], shot[1]);
    }

    //try and finish off a wounded ship
    public static int[] kill(char[][] board, ArrayList<Integer[]> wounds) {
        int[] coords = {0, 0};

        String up = "";
        String down = "";
        String left = "";
        String right = "";

        //which cardinal directions goes the longest before terminating at a wall or other revealed square?
        for (Integer[] wound : wounds) {

            int row = wound[0];
            int col = wound[1];

            if (((row > 0 && board[row - 1][col] != '.') || row == 0) &&
                    ((row < board.length - 1 && board[row + 1][col] != '.') || row == board.length - 1) &&
                    ((col > 0 && board[row][col - 1] != '.') || col == 0) &&
                    (((col < board.length - 1 && board[row][col + 1] != '.')) || col == board.length - 1)) {
                continue;
            }

            if (row > 0) {
                for (int boardRow = row; boardRow >= 0; boardRow--) {
                    if (board[boardRow][col] == '.' || board[boardRow][col] == 'X' || up.isEmpty()) {
                        up += board[boardRow][col];
                    } else {
                        break;
                    }
                }
                //if there are no empty spaces in the string, zero it out
                //this happens when two ships are parallel, bordered by hits, like "OXXO"
                if (up.indexOf('.') == -1) up = "";
            }

            if (col < board.length - 1) {
                for (int boardCol = col; boardCol < board.length; boardCol++) {
                    if (board[row][boardCol] == '.' || board[row][boardCol] == 'X' || right.isEmpty()) {
                        right += board[row][boardCol];
                    } else {
                        break;
                    }
                }
                if (right.indexOf('.') == -1) right = "";
            }

            if (col > 0) {
                for (int boardCol = col; boardCol >= 0; boardCol--) {
                    if (board[row][boardCol] == '.' || board[row][boardCol] == 'X' || left.isEmpty()) {
                        left += board[row][boardCol];
                    } else {
                        break;
                    }
                }
                if (left.indexOf('.') == -1) left = "";
            }

            if (row < board.length - 1) {
                for (int boardRow = row; boardRow < board.length; boardRow++) {
                    if (board[boardRow][col] == '.' || board[boardRow][col] == 'X' || down.isEmpty()) {
                        down += board[boardRow][col];
                    } else {
                        break;
                    }
                }
                if (down.indexOf('.') == -1) down = "";
            }

            //Choose the longest string and return the first empty space
            if (up.length() > right.length() && up.length() > left.length() && up.length() > down.length()) {
                return new int[]{row - up.indexOf('.'), col};
            } else if (right.length() > down.length() && right.length() > left.length()) {
                return new int[]{row, col + right.indexOf('.')};
            } else if (down.length() > left.length()) {
                return new int[]{row + down.indexOf('.'), col};
            } else {
                return new int[]{row, col - left.indexOf('.')};
            }
        }

        //This should be unreachable, but is required to compile.
        return coords;
    }

    public static int[] hunt(char[][] board, HashMap<Character, Integer> ships) {
        int[][] probabilityBoard = new int[10][10];

        //This is a horror for big O notation.
        //For each ship, find all valid placements and increment the corresponding cells on the probability board.
        //We start with the largest ship and go down, experimenting with minimum thresholds to bother building the whole map.
        for (int ship : ships.values().stream().sorted(Comparator.reverseOrder()).toList()) {
            int shipLocations = 0;
            int[] currentSpace = new int[2];
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
                            //probabilityBoard[row][col + i] *= ship;
                        }
                        currentSpace[0] = row;
                        currentSpace[1] = col;
                        shipLocations++;
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
                            //probabilityBoard[row + i][col] *= ship;
                        }
                        currentSpace[0] = row;
                        currentSpace[1] = col;
                        shipLocations++;
                    }
                }
            }
            //If the current ship only has a few locations it can possibly be, guess one of those to eliminate it
            if (shipLocations <= 3) {
                return currentSpace;
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
