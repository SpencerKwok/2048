package lab4_201_03.uwaterloo.ca.lab4_201_03;

import android.util.Log;

/*
 * Contains the X coordinate of a game block, which extends to the COORDINATE_ABSTRACT_CLASS
 *
 * 0 = First Column
 * 1 = Second Column
 * 2 = Third Column
 * 3 = Fourth Column
 * 4+ = Default to 0 + debug statement
 *
 */

final class X_COORDINATE extends COORDINATE_ABSTRACT_CLASS{
    void setCurrentValue(int v){
        switch(v){
            case 0:
                value = GRID_BOUNDARIES_X.FIRST_COLUMN;
                break;
            case 1:
                value = GRID_BOUNDARIES_X.SECOND_COLUMN;
                break;
            case 2:
                value = GRID_BOUNDARIES_X.THIRD_COLUMN;
                break;
            case 3:
                value = GRID_BOUNDARIES_X.FOURTH_COLUMN;
                break;
            default:
                value = 0;
                Log.d("X_COORDINATE","ILLEGAL X COORDINATE ASSIGNMENT");
                break;
        }
    }
}
