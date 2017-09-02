package lab4_201_03.uwaterloo.ca.lab4_201_03;
import android.content.Context;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

/*
 * GameBlock extends GAMEBLOCK_ABSTRACT to get the basic block functions
 * as well as creating its own functions that are specialized for 2048
 */

final class GameBlock extends GAMEBLOCK_ABSTRACT {
    //String used to convert int to string
    private final String stringFormatDigit = "%d";

    //RNG and value of block
    private Random rng = new Random();
    private int value = (rng.nextInt(2)+1)*2;

    //List of get/set functions that will be used by GameBlockManager
    public int getValue(){return value;}
    public boolean getFinishedMoving(){return finishedMoving;}
    public int getYCoordinate(){return Y.getCurrentValue();}
    public int getXCoordinate(){return X.getCurrentValue();}
    public TextView getValueLabel(){return valueLabel;}
    public boolean getFinishedMerging(){return finishedMerging;}
    public void setFinishedMerging(boolean m){finishedMerging = m;}

    public void setValue(int v){
        value = v;
        valueLabel.setText(String.format(Locale.CANADA,stringFormatDigit,value));
    }

    public void setColumn(int nextColumn){
        X.setCurrentValue(nextColumn);
        //sets finishedMoving to false so the block will move
        finishedMoving = false;
    }
    public void setRow(int nextRow){
        Y.setCurrentValue(nextRow);
        //sets finishedMoving to false so the block will move
        finishedMoving = false;
    }

    //sets up the image with a particular scaling from resources
    protected void setUpImage(){
        setImageResource(R.drawable.gameblock);
        setScaleX(SCALING);
        setScaleY(SCALING);
        setX(X.getCurrentValue());
        setY(Y.getCurrentValue());
    }

    //sets up the TextView with a particular padding from resources
    protected void setUpTextView(Context context){
        valueLabel = new TextView(context);
        valueLabel.setText(String.format(Locale.CANADA,stringFormatDigit, value));
        valueLabel.setX(X.getCurrentValue());
        valueLabel.setY(Y.getCurrentValue() + PADDING);
        valueLabel.setTextSize(TEXTVIEW_TEXT_SIZE);
        valueLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        valueLabel.setWidth(TEXTVIEW_WIDTH);
        valueLabel.bringToFront();
    }

    //adds block and its TextView to layout
    protected void addToView(RelativeLayout rl){
        rl.addView(this);
        rl.addView(valueLabel);
    }

    //GameBlock constructor
    public GameBlock(Context context, int x, int y, RelativeLayout rl) {
        super(context);

        PADDING = 115;
        SCALING = 0.65f;
        TEXTVIEW_WIDTH = 340;
        TEXTVIEW_TEXT_SIZE = 30;

        X.setCurrentValue(x);
        Y.setCurrentValue(y);

        setUpImage();
        setUpTextView(context);

        addToView(rl);
    }
}
