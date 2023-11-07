import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Basic {
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
        HashMap<Character, Integer> liveShips = new HashMap<>();
        liveShips.put('1', 2);
        liveShips.put('2', 3);
        liveShips.put('3', 3);
        liveShips.put('4', 4);
        liveShips.put('5', 5);

        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') {
                    containsWoundedShip = true;
                    break;
                } else if (Character.isDigit(cell)) {
                    liveShips.remove(cell);
                }
            }
        }

        int[] shot;

        if (containsWoundedShip) {
            shot = kill(board);
        } else {
            shot = radar(board, liveShips);
        }

        return formatGuess(shot[0], shot[1]);
    }


    //try and finish off a wounded ship
    public static int[] kill(char[][] board) {
        int[] coords = {0, 0};
        int[][] wounds = new int[15][2];
        int index = 0;


        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 'X') {
                    wounds[index][0] = row;
                    wounds[index][1] = col;
                    index++;
                }
            }
        }

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

    public static int[] hunt(char[][] board, HashMap<Character, Integer> deadShips) {
        int[] coords = {0, 0};

        int smallestShip = (int) Collections.min(deadShips.values());
        smallestShip = 2;
        //TODO(fix this)

        while (board[coords[0]][coords[1]] != '.') {
            if (coords[1] + smallestShip <= 9) {
                coords[1] += smallestShip;
            } else {
                coords[0]++;
                coords[1] = coords[0] % 2;
            }

            //we may end up with unchecked vertical strips
            //currently unreachable because the smallestShip is broken
            if (coords[1] > 9 || coords[0] > 9) {
                coords[0] = 0;
                coords[1] = 0;

                while (board[coords[0]][coords[1]] != '.') {
                    if (coords[0] + smallestShip <= 9) {
                        coords[0] += smallestShip;
                    } else {
                        coords[1]++;
                        coords[0] = coords[1] % 2;
                    }
                }

            }

        }

        return coords;
    }

    public static int[] radar(char[][] board, HashMap<Character, Integer> ships) {
        int[][] probabilityBoard = new int[10][10];

        for (int ship : ships.values()) {

            for (int row = 0; row < board.length; row++) {
                String fullRow = Arrays.toString(board[row]).replaceAll("[, \\[\\]]", "");
                for (int col = 0; col < board[row].length - ship + 1; col++) {
                    String space = fullRow.substring(col, col + ship);
                    if (space.matches("^\\.*$")) {

                        for (int i = 0; i < ship; i++) {
                            if (board[row][col + i] == '.') {
                                probabilityBoard[row][col + i]++;
                            } else {
                                System.out.println("horizontal error here");
                            }
                        }
                    }
                }
            }

            for (int col = 0; col < board.length; col++) {
                String fullCol = "";
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

        for (int row = 0; row < probabilityBoard.length; row++) {
            for (int col = 0; col < probabilityBoard[row].length; col++) {
                if (probabilityBoard[row][col] > max) {
                    max = probabilityBoard[row][col];
                    coords[0] = row;
                    coords[1] = col;
                }
            }
        }

        if (board[coords[0]][coords[1]] != '.') {
            return hunt(board, ships);
        }

        return coords;
    }


    //change indices into proper guess format
    public static String formatGuess(int row, int col) {
        String guess = "";
        guess += (char) ('A' + row);
        guess += String.valueOf(col + 1);
        return guess;
    }
}
