package lab4_201_03.uwaterloo.ca.lab4_201_03;

/*
 * Velocity class contains the velocity of all game blocks.
 * This is universal because all blocks should be accelerating at the
 * same rate on each turn.
 */

final class VELOCITY {
    //Variables used to adjust velocity values;
    private final static int MAX_VELOCITY = 200;
    private final static int ACCELERATION = 5;
    private final static int INITIAL_SPEED = 50;
    private static int currentVelocity = INITIAL_SPEED;

    //resets velocity to the initial speed
    static void resetVelocity(){currentVelocity = INITIAL_SPEED;}

    //returns the current velocity
    static int getVelocity(){return currentVelocity;}

    //Increases velocity
    static void updateVelocity(){
        currentVelocity += ACCELERATION;
        if(currentVelocity > MAX_VELOCITY)
            currentVelocity = MAX_VELOCITY;
    }
}
