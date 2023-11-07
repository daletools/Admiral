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
    - Refine the kill() method to target more likely cells first
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

        //For each hit, shoot at any unknown squares around it.
        for (int[] wound : wounds) {
            //check above
            if (wound[0] > 0 && board[wound[0] - 1][wound[1]] == '.') {
                coords[0] = wound[0] - 1;
                coords[1] = wound[1];
                break;

                //check right
            } else if (wound[1] < board[0].length - 1 && board[wound[0]][wound[1] + 1] == '.') {
                coords[0] = wound[0];
                coords[1] = wound[1] + 1;
                break;

                //check down
            } else if (wound[0] < board.length - 1 && board[wound[0] + 1][wound[1]] == '.') {
                coords[0] = wound[0] + 1;
                coords[1] = wound[1];
                break;

                //check left
            } else if (wound[1] > 0 && board[wound[0]][wound[1] - 1] == '.') {
                coords[0] = wound[0];
                coords[1] = wound[1] - 1;
                break;
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
                //Just join to string each row and filter out the bits we dont want
                String fullRow = Arrays.toString(board[row]).replaceAll("[, \\[\\]]", "");

                for (int col = 0; col < board[row].length - ship + 1; col++) {
                    //looking for substrings that are only comprised of '.', of the length of our ship.
                    String space = fullRow.substring(col, col + ship);

                    if (space.matches("^\\.*$")) {
                        for (int i = 0; i < ship; i++) {
                            //increment each space the ship would cover
                            if (board[row][col + i] == '.') {
                                probabilityBoard[row][col + i]++;
                            } else {
                                System.out.println("horizontal error here");
                            }
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
                            if (board[row + i][col] == '.') {
                                probabilityBoard[row + i][col]++;
                            } else {
                                System.out.println("vertical error here");
                            }
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
