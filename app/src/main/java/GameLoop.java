package lab4_201_03.uwaterloo.ca.lab4_201_03;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TimerTask;

/*
 * GameLoop extends TimerTask to constantly run the Accelerometer FSM and
 * update GameBlockManager when a hand gesture has been triggered
 */

class GameLoop extends TimerTask {

    //Manages the game
    private AccelerometerSensor directionHandler;
    private GameBlockManager gameBlockManager;

    //Used to run the runnable on the UI thread
    private Activity myActivity;

    //Used to alert the user if they have won/lost the session
    private TextView gameOverTextView;

    //Used to prevent the user from multiple gesture inputs
    //while the blocks are still moving
    private boolean turnOngoing = true;

    //Used to determine whether or not the blocks actually
    //moved before adding a new block to the grid
    private boolean movementOccurred = false;

    //Stops the Accelerometer FSM when the user has won/lost the session
    private boolean gameStatusReached = false;

    //Contains the previous direction of the Accelerometer Sensor
    private AccelerometerSensor.DIRECTION previousDirection = AccelerometerSensor.DIRECTION.UNDETERMINED;

    //GameLoop constructor
    GameLoop(Activity thisActivity, RelativeLayout myRL, Context thisContext, SensorEventListener se, TextView g)
    {
        directionHandler = (AccelerometerSensor) se;
        gameOverTextView = g;
        myActivity = thisActivity;
        gameBlockManager = new GameBlockManager(myRL, thisContext);
        gameBlockManager.addNewBlock();
    }

    //Runs game
    public void run(){
        //runs function in main thread
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Runs game if the game is not over
                if(!gameStatusReached){
                    //Run FSM if movement is not occurring
                    if(!movementOccurred){
                        directionHandler.activateFSM();
                        previousDirection = directionHandler.getPreviousDirection();
                    }
                    //Update gameBlockManager, turnOngoing, and movementOccurred if a hand gesture has occurred.
                    switch (previousDirection){
                        case UP:
                            gameBlockManager.assignRowColumns(new int[]{0,4,8,12},new int[]{1,5,9,13},new int[]{2,6,10,14},new int[]{3,7,11,15});
                            turnOngoing = gameBlockManager.runShifts(AccelerometerSensor.DIRECTION.UP);
                            if(turnOngoing) movementOccurred = true;
                            break;
                        case DOWN:
                            gameBlockManager.assignRowColumns(new int[]{12,8,4,0},new int[]{13,9,5,1},new int[]{14,10,6,2},new int[]{15,11,7,3});
                            turnOngoing = gameBlockManager.runShifts(AccelerometerSensor.DIRECTION.DOWN);
                            if(turnOngoing) movementOccurred = true;
                            break;
                        case LEFT:
                            gameBlockManager.assignRowColumns(new int[]{0,1,2,3},new int[]{4,5,6,7},new int[]{8,9,10,11},new int[]{12,13,14,15});
                            turnOngoing = gameBlockManager.runShifts(AccelerometerSensor.DIRECTION.LEFT);
                            if(turnOngoing) movementOccurred = true;
                            break;
                        case RIGHT:
                            gameBlockManager.assignRowColumns(new int[]{3,2,1,0},new int[]{7,6,5,4},new int[]{11,10,9,8},new int[]{15,14,13,12});
                            turnOngoing = gameBlockManager.runShifts(AccelerometerSensor.DIRECTION.RIGHT);
                            if(turnOngoing) movementOccurred = true;
                        default:
                            break;
                    }
                    //if the turn is over and a movement has occurred, reset
                    //the Accelerometer FSM, velocity, merging properties,
                    //turn reset properties, and add a block to the blockGrid
                    if(!turnOngoing && movementOccurred){
                        directionHandler.resetPreviousDirection();
                        VELOCITY.resetVelocity();
                        gameBlockManager.resetMerging();
                        //If the user has reached 256, let user know and end the game
                        if(gameBlockManager.getMaxValue() >= 256){
                            gameOverTextView.setTextColor(Color.BLUE);
                            gameOverTextView.setText(R.string.YOU_WIN);
                            gameOverTextView.bringToFront();
                            gameStatusReached = true;
                        }
                        //If the user can no longer move and did not reach 256, let the user know and end the game
                        else if(!gameBlockManager.addNewBlock()){
                            gameOverTextView.setTextColor(Color.RED);
                            gameOverTextView.setText(R.string.GAME_OVER);
                            gameOverTextView.bringToFront();
                            gameStatusReached = true;
                        }
                        turnOngoing = true;
                        movementOccurred = false;
                        previousDirection = AccelerometerSensor.DIRECTION.UNDETERMINED;
                    }
                }
            }
        });
    }
}
