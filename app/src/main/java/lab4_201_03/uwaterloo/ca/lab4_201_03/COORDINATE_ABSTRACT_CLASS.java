package lab4_201_03.uwaterloo.ca.lab4_201_03;

/*
 * COORDINATE_ABSTRACT_CLASS contains simple functions for retrieving coordinate values.
 * Extended to by X_COORDINATE and Y_COORDINATE
 */

abstract class COORDINATE_ABSTRACT_CLASS {
    int value;
    abstract void setCurrentValue(int v);
    int getCurrentValue(){return value;}
}
