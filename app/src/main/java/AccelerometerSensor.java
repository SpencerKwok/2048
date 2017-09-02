package lab4_201_03.uwaterloo.ca.lab4_201_03;

//Hardware imports
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

//UI elements imports
import android.util.Log;
import android.widget.TextView;

//FSM
final class AccelerometerSensor implements SensorEventListener {
    //where the directions will be displayed
    private TextView textView;

    //contains the filter constant
    private final int filterConstant = 10;

    //contains the change constant (used to determine if a movement has been triggered)
    private final double change = 2.5;

    //contains the constant required to reset the FSM
    private final double reset = 0.4;

    //used to calibrate movements
    private final float calibrateX = -0.200f;
    private final float calibrateY = 9.8f;

    //holds the unfiltered data (used for low pass filter)
    private float[] unfilteredData = {0,0,0};

    //holds the filtered data
    private float[] filteredData = {0,0,0};

    //directions (up is positive z, down is negative z, right is positive x, left is negative x)
    enum DIRECTION{UP,DOWN,LEFT,RIGHT,UNDETERMINED}

    //states of the FSM
    private enum STATE{WAIT,RISE_X,FALL_X,RISE_Y,FALL_Y,STABLE,DETERMINED,RESET}

    //holds current direction
    private DIRECTION currentDirection = DIRECTION.UNDETERMINED;

    //holds previous direction
    private DIRECTION previousDirection = DIRECTION.UNDETERMINED;

    //holds current state
    private STATE currentState = STATE.WAIT;

    //constructor for sensors that will not be graphed
    AccelerometerSensor(TextView d){
        textView = d;
    }

    //implemented to fulfill the implementation of
    //SensorEventListener interface
    public void onAccuracyChanged(Sensor s, int i){}

    //function called when a sensor reading has changed
    public void onSensorChanged(SensorEvent se){
        //if the sensor changed is the accelerometer, get its data
        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //get x and z components
            if(se.values.length >= 3){
                //set unfilteredData
                unfilteredData[0] = se.values[0] - calibrateX;
                unfilteredData[1] = se.values[1] - calibrateY;
                unfilteredData[2] = se.values[2];
            }
            else{
                //prints out an error if the sensor data is corrupt
                Log.d("ACCELEROMETER_SENSOR", "Sensor data is corrupted");
            }
        }
    }

    //resets state of FSM, called when currentState = RESET;
    private void resetFSM(){
        currentState = STATE.WAIT;
        currentDirection = DIRECTION.UNDETERMINED;
    }

    //filters input data and returns the filtered data
    private float[] lowPassFilter( float[] input, float[] output ) {
        if (output == null || currentState == STATE.STABLE) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] += (input[i] - output[i]) / filterConstant;
            if(Math.abs(output[i]) < reset) output[i] = 0;
        }
        return output;
    }

    //activates FSM
    void activateFSM(){
        filteredData = lowPassFilter(unfilteredData,filteredData);

        //runs functions based on what state the accelerometer is currently in
        switch(currentState){
            //checks to see if a direction has been triggered
            case WAIT:
                //checks if UP is triggered
                if(filteredData[1] > change){currentState = STATE.RISE_Y;}
                //checks if DOWN is triggered
                else if(filteredData[1]  < change*-1){currentState = STATE.FALL_Y;}
                //checks if RIGHT is triggered
                else if(filteredData[0] > change){currentState = STATE.RISE_X;}
                //checks if LEFT is triggered
                else if(filteredData[0] < change*-1){currentState = STATE.FALL_X;}
                break;
            //RISE_X, FALL_X, RISE_Z, FALL_Z change state to determined and set their respective directions
            case RISE_X:
                currentState = STATE.DETERMINED;
                currentDirection = DIRECTION.RIGHT;
                break;
            case FALL_X:
                currentState = STATE.DETERMINED;
                currentDirection = DIRECTION.LEFT;
                break;
            case RISE_Y:
                currentState = STATE.DETERMINED;
                currentDirection = DIRECTION.UP;
                break;
            case FALL_Y:
                currentState = STATE.DETERMINED;
                currentDirection = DIRECTION.DOWN;
                break;
            //waits till the phone is relatively still before resetting the FSM
            case STABLE:
                //resetFSM is called based on which direction was triggered
                switch(previousDirection){
                    //if the absolute value of filteredData is less than the reset value,
                    //reset the FSM
                    case LEFT:
                        if(Math.abs(filteredData[0]) < reset)resetFSM();
                        break;
                    case RIGHT:
                        if(Math.abs(filteredData[0]) < reset)resetFSM();
                        break;
                    case UP:
                        if(Math.abs(filteredData[1]) < reset)resetFSM();
                        break;
                    case DOWN:
                        if(Math.abs(filteredData[1]) < reset)resetFSM();
                        break;
                }
                break;
            //once a direction has been determined, change the text view to the direction value
            case DETERMINED:
                //change currentState to stable state
                currentState = STATE.STABLE;
                //update textView
                switch(currentDirection){
                    case UP:
                        previousDirection = DIRECTION.UP;
                        textView.setText(R.string.UP);
                        break;
                    case DOWN:
                        previousDirection = DIRECTION.DOWN;
                        textView.setText(R.string.DOWN);
                        break;
                    case RIGHT:
                        previousDirection = DIRECTION.RIGHT;
                        textView.setText(R.string.RIGHT);
                        break;
                    case LEFT:
                        previousDirection = DIRECTION.LEFT;
                        textView.setText(R.string.LEFT);
                        break;
                    //if an unknown direction was found, stop the FSM
                    default:
                        Log.d("ACCELEROMETER_SENSOR","An unknown state has been reached in the DETERMINED state");
                        currentState = STATE.RESET;
                }

                break;
            //resetFSM after a direction has been determined
            case RESET:
                resetFSM();
                break;
            //An unknown error has occurred, stop FSM
            default:
                Log.d("ACCELEROMETER_SENSOR","Current state is an invalid state");
                break;
        }
    }

    //returns current direction that the block is going
    DIRECTION getPreviousDirection(){return previousDirection;}
    void resetPreviousDirection(){
        previousDirection = DIRECTION.UNDETERMINED;
        filteredData = new float[]{0,0,0};
        resetFSM();
    }
}
