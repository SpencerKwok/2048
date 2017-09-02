package lab4_201_03.uwaterloo.ca.lab4_201_03;

import android.util.Log;

/*
 * Contains the Y coordinate of a game block, which extends to the COORDINATE_ABSTRACT_CLASS
 *
 * 0 = First Row
 * 1 = Second Row
 * 2 = Third Row
 * 3 = Fourth Row
 * 4+ = Default to 0 + debug statement
 *
 */
final class Y_COORDINATE extends COORDINATE_ABSTRACT_CLASS{
    void setCurrentValue(int v){
        switch(v){
            case 0:
                value = GRID_BOUNDARIES_Y.FIRST_ROW;
                break;
            case 1:
                value = GRID_BOUNDARIES_Y.SECOND_ROW;
                break;
            case 2:
                value = GRID_BOUNDARIES_Y.THIRD_ROW;
                break;
            case 3:
                value = GRID_BOUNDARIES_Y.FOURTH_ROW;
                break;
            default:
                Log.wtf("Y_COORDINATE","ILLEGAL X COORDINATE ASSIGNMENT");
                break;
        }
    }
}