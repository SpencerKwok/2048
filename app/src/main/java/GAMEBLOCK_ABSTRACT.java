package lab4_201_03.uwaterloo.ca.lab4_201_03;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * GAMEBLOCK_ABSTRACT contains the basic components of a game block such
 * as the TextView attached to the block, its default image properties,
 * the block's coordinates, and its movement functions
 */

abstract class GAMEBLOCK_ABSTRACT extends AppCompatImageView{
    //Default image properties
    protected float SCALING = 1;
    protected int PADDING = 0;
    protected int TEXTVIEW_WIDTH = 100;
    protected int TEXTVIEW_TEXT_SIZE = 15;
    protected TextView valueLabel;

    //X,Y coordinates of block
    protected X_COORDINATE X = new X_COORDINATE();
    protected Y_COORDINATE Y = new Y_COORDINATE();

    //Movement properties of block
    protected boolean finishedMoving = false;
    protected boolean finishedMerging = false;

    //fixes X component of block + label to X_COORDINATE
    private void fixX(){
        setX(X.getCurrentValue());
        valueLabel.setX(X.getCurrentValue());
        finishedMoving = true;
    }

    //fixes Y component of block + label to Y_COORDINATE
    private void fixY(){
        setY(Y.getCurrentValue());
        valueLabel.setY(Y.getCurrentValue() + PADDING);
        finishedMoving = true;
    }

    //updates X component of block + label with velocity + direction
    private void updateX(boolean left0right1){
        int v = (left0right1) ? VELOCITY.getVelocity() : VELOCITY.getVelocity()*-1;
        setX(getX() + v);
        valueLabel.setX(valueLabel.getX() + v);
    }

    //updates Y component of block + label with velocity + direction
    private void updateY(boolean down0up1){
        int v = (down0up1) ? VELOCITY.getVelocity()*-1 : VELOCITY.getVelocity();
        setY(getY() + v);
        valueLabel.setY(valueLabel.getY() + v);
    }

    //sets movement for each block
    public void moveBlockRightDirection(){
        VELOCITY.updateVelocity();
        if (getX() + VELOCITY.getVelocity() > X.getCurrentValue())
            fixX();
        else if(!finishedMoving)
            updateX(true);
    }
    public void moveBlockLeftDirection(){
        VELOCITY.updateVelocity();
        if (getX() - VELOCITY.getVelocity() < X.getCurrentValue())
            fixX();
        else if(!finishedMoving)
            updateX(false);
    }
    public void moveBlockUpDirection(){
        VELOCITY.updateVelocity();
        if (getY() - VELOCITY.getVelocity() < Y.getCurrentValue())
            fixY();
        else if(!finishedMoving){
            updateY(true);
        }
    }
    public void moveBlockDownDirection(){
        VELOCITY.updateVelocity();
        if (getY() + VELOCITY.getVelocity() > Y.getCurrentValue())
            fixY();
        else if(!finishedMoving){
            updateY(false);
        }
    }

    //abstract functions to be implemented to set up image properly
    protected abstract void setUpImage();

    protected abstract void setUpTextView(Context context);

    protected abstract void addToView(RelativeLayout rl);

    //default constructor for AppCompatImageView
    GAMEBLOCK_ABSTRACT(Context context) {super(context);}
}
