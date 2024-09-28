import java.util.HashMap;

public class Ship2025 {
    static char[][] historical;
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



        return "";
    }
}
