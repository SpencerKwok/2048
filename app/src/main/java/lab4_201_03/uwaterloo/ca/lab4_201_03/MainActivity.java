package lab4_201_03.uwaterloo.ca.lab4_201_03;

//Fundamental imports to run application

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

final class MainActivity extends AppCompatActivity {
    //the memory location of the app's layout
    private RelativeLayout mainLayout;
    //the memory location of the sensor services
    private SensorManager sensorManager;
    //elements related to read/represent acceleration data
    private Sensor accelerationSensor;
    private SensorEventListener accelerationEventManager;
    //UI elements
    private TextView directionLabel;
    private TextView gameOverLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set mainLayout
        setMainLayout();

        //sets up items on mainLayout
        setItemsOnMainLayout();

        //sets up sensorManager
        setSensorManager();

        //sets up sensor components
        setSensorComponents();

        //sets up sensor event listeners
        setSensorEventListeners();

        //connects sensors to sensorManager
        connectSensors();

        setUpGame();
    }

    //set mainLayout to main_layout (id in XML file)
    private void setMainLayout(){
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mainLayout.getLayoutParams().width=1050;
        mainLayout.getLayoutParams().height=1050;
        mainLayout.setBackgroundResource(R.drawable.gameboard);
    }

    //creates and adds TextView to mainLayout
    //returns the memory location of the TextView created
    private TextView createNewTextView(int x, int y, int size) {
        TextView view = new TextView(getApplicationContext());
        view.setX(x);
        view.setY(y);
        view.setTextSize(size);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.addView(view);
        return view;
    }

    //Adds items to mainLayout
    private void setItemsOnMainLayout() {
        directionLabel = createNewTextView(-100,-100,10);
        gameOverLabel = createNewTextView(210,160,90);
    }

    //gets handle for the sensor services on the android device
    private void setSensorManager(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    //sets up accelerometer sensor
    private void setSensorComponents(){
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //sets up Sensor Event Managers to handle readings from the sensors
    private void setSensorEventListeners(){
        accelerationEventManager = new AccelerometerSensor(directionLabel);
    }

    //register listeners to the sensorManager
    private void connectSensors() {
        sensorManager.registerListener(accelerationEventManager, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    //sets up game items
    private void setUpGame(){
        GameLoop gameHandler = new GameLoop(this, mainLayout, getApplicationContext(), accelerationEventManager,gameOverLabel);
        Timer timer = new Timer();
        timer.schedule(gameHandler,0,20);
    }
}