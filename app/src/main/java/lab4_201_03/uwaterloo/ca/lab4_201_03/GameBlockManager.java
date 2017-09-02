package lab4_201_03.uwaterloo.ca.lab4_201_03;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Random;

/**
 * GameBlockManager is the main component of the GameLoop. It contains
 * the 4x4 grid and manages where blocks need to go such as their
 * X and Y positions and their accelerations, as well as the removal of
 * blocks upon merging.
 */

class GameBlockManager {
    //4 x 4 game board
    private GameBlock blockGrid[] = new GameBlock[16];

    //Properties needed to add GameBlocks to the UI
    private final RelativeLayout main_layout;
    private final Context main_context;

    /*
     * The game board is split into 4 different parts
     * upon moving the blocks in a direction (either 4 rows
     * or 4 columns). rowColumnX contains the 4 indexes
     * of the blockGrid array, starting from the bottom for DOWN,
     * top for UP, right for RIGHT and left for UP. BlockExistenceOrder
     * contains a boolean value for each index in rowColumnX, true
     * being that the block exists, and false being there is no block
     * at that location. Move Block Order is essential the order
     * in which the blocks will be moved based on their location. It
     * is in the same order as rowColumnX. toRowOrder contains the
     * destination locations of each block given the direction the
     * blocks will be travelling on. The values in toRowOrder are
     * directly related to the switch cases in the X_COORDINATE and
     * Y_COORDINATE class
     */
    private int rowColumn1[];
    private int rowColumn2[];
    private int rowColumn3[];
    private int rowColumn4[];
    private boolean blockExistenceOrder[] = new boolean[4];
    private GameBlock moveBlockOrder[] = new GameBlock[4];
    private int toRowOrder[] = new int[3];

    //This contains the maximum value the user has achieved during
    //this session. If it is above 256, then the player has won
    private int maxValue = 2;

    //RNG for random block placement
    Random rand = new Random();

    //GameBlockManager constructor sets up UI elements
    GameBlockManager(RelativeLayout rl, Context c){
        main_layout = rl;
        main_context = c;
    }

    //returns maxValue
    int getMaxValue(){return maxValue;}

    //Adds a new GameBlock to a random location on the grid
    boolean addNewBlock(){

        //Add all the free spaces in the game board to an ArrayList
        ArrayList<Integer> availableSpotsBuffer = new ArrayList<Integer>(16);
        for(int i = 0; i < blockGrid.length; ++i){
            if(blockGrid[i] == null) availableSpotsBuffer.add(i);
        }

        //Adds a new block to the grid
        if(availableSpotsBuffer.size() >= 1){
            int location = (availableSpotsBuffer.size() == 1) ? availableSpotsBuffer.remove(0) : availableSpotsBuffer.remove(rand.nextInt(availableSpotsBuffer.size()-1));
            blockGrid[location] = new GameBlock(main_context,convertIndexToColumn(location),convertIndexToRow(location),main_layout);
        }

        //Returns false if and only if the user has lost
        return availableSpotsBuffer.size() != 0 || validMove(AccelerometerSensor.DIRECTION.UP) || validMove(AccelerometerSensor.DIRECTION.RIGHT);
    }

    //Runs the move functions for all the rows/columns
    boolean runShifts(AccelerometerSensor.DIRECTION d){
        //shiftsMade is false if and only if the blocks cannot move anymore in a particular direction
        boolean shiftsMade = (!empty(0,d) && (spaceToMove(0,d) || mergingPossible(0,d))) ||
                (!empty(1,d) && (spaceToMove(1,d) || mergingPossible(1,d))) ||
                (!empty(2,d) && (spaceToMove(2,d) || mergingPossible(2,d))) ||
                (!empty(3,d) && (spaceToMove(3,d) || mergingPossible(3,d)));

        //Executes shifts based on the desired direction
        switch(d){
            case DOWN:
            case UP:
                executeShifts(new int[]{0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15},d);
                break;
            case LEFT:
            case RIGHT:
                executeShifts(new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15},d);
                break;
            default:
                break;
        }

        return shiftsMade;
    }

    //Runs shifts of blocks for each column/row
    private void executeShifts(int[] a, AccelerometerSensor.DIRECTION d){
        shift(blockGrid[a[0]],blockGrid[a[1]],blockGrid[a[2]],blockGrid[a[3]],rowColumn1,d);
        shift(blockGrid[a[4]],blockGrid[a[5]],blockGrid[a[6]],blockGrid[a[7]],rowColumn2,d);
        shift(blockGrid[a[8]],blockGrid[a[9]],blockGrid[a[10]],blockGrid[a[11]],rowColumn3,d);
        shift(blockGrid[a[12]],blockGrid[a[13]],blockGrid[a[14]],blockGrid[a[15]],rowColumn4,d);
    }

    //Assigns rowColumnX
    void assignRowColumns(int[] a, int[] b, int[] c, int[] d){
        rowColumn1 = a;
        rowColumn2 = b;
        rowColumn3 = c;
        rowColumn4 = d;
    }

    //Resets merging property to false for all blocks in blockGrid
    void resetMerging(){
        for(GameBlock b : blockGrid){
            if(b != null) b.setFinishedMerging(false);
        }
    }

    //Checks to see if merging is possible when the game board is full
    private boolean validMove(AccelerometerSensor.DIRECTION d){
        return mergingPossible(0,d) || mergingPossible(1,d) || mergingPossible(2,d) || mergingPossible(3,d);
    }

    //returns true if and only if the index is valid and the GameBlock at index i of blockGrid exists
    @Contract(pure = true)
    private boolean getGameBlockExists(int i){return i >= 0 && i < blockGrid.length && blockGrid[i] != null;}

    //returns -1 if there is no value, else return the GameBlock's value at index i
    private int getGameBlockValue(int i){
        if(getGameBlockExists(i)) return blockGrid[i].getValue();
        else return -1;
    }

    //returns true if and only if merging is possible on the board, checks based on the direction of movement
    private boolean mergingPossible(int rowColumn,AccelerometerSensor.DIRECTION d){
        switch(d){
            case UP:
            case DOWN: {
                //contains the 4 values in a row/column
                int oneValue = getGameBlockValue(rowColumn);
                int twoValue = getGameBlockValue(rowColumn + 4);
                int threeValue = getGameBlockValue(rowColumn + 8);
                int fourValue = getGameBlockValue(rowColumn + 12);

                //contains the 4 blocks have already merged (defaults to true if the block does not exist)
                boolean hasMerged1 = (oneValue == -1) || blockGrid[rowColumn].getFinishedMerging();
                boolean hasMerged2 = (twoValue == -1) || blockGrid[rowColumn + 4].getFinishedMerging();
                boolean hasMerged3 = (threeValue == -1) || blockGrid[rowColumn + 8].getFinishedMerging();
                boolean hasMerged4 = (fourValue == -1) || blockGrid[rowColumn + 12].getFinishedMerging();

                return (oneValue == twoValue && !hasMerged1 && !hasMerged2) ||
                        (twoValue == threeValue && !hasMerged2 && !hasMerged3) ||
                        (threeValue == fourValue && !hasMerged3 && !hasMerged4);
            }
            case LEFT:
            case RIGHT: {
                //gets start index of the row based on the row number
                int row = convertRowToStartIndex(rowColumn);

                //contains the 4 values in a row/column
                int oneValue = getGameBlockValue(row);
                int twoValue = getGameBlockValue(row + 1);
                int threeValue = getGameBlockValue(row + 2);
                int fourValue = getGameBlockValue(row + 3);

                //contains the 4 blocks have already merged (defaults to true if the block does not exist)
                boolean hasMerged1 = (oneValue == -1) || blockGrid[row].getFinishedMerging();
                boolean hasMerged2 = (twoValue == -1) || blockGrid[row + 1].getFinishedMerging();
                boolean hasMerged3 = (threeValue == -1) || blockGrid[row + 2].getFinishedMerging();
                boolean hasMerged4 = (fourValue == -1) || blockGrid[row + 3].getFinishedMerging();

                return (oneValue == twoValue && !hasMerged1 && !hasMerged2) ||
                        (twoValue == threeValue && !hasMerged2 && !hasMerged3) ||
                        (threeValue == fourValue && !hasMerged3 && !hasMerged4);
            }
            default:
                //Reports an error if merge is called with an UNDETERMINED direction
                Log.d("ERROR","INVALID MERGE POSSIBLE CALLED");
                return false;
        }
    }

    //Returns true if and only if the row/column contains no blocks
    private boolean empty(int targetRowColumn, AccelerometerSensor.DIRECTION d){
        switch(d){
            case UP:
            case DOWN:
                return(getGameBlockValue(targetRowColumn) == -1 && getGameBlockValue(targetRowColumn+4) == -1 && getGameBlockValue(targetRowColumn+8) == -1 && getGameBlockValue(targetRowColumn+12) == -1);
            case LEFT:
            case RIGHT:
                int row = convertRowToStartIndex(targetRowColumn);
                return(getGameBlockValue(row) == -1 && getGameBlockValue(row+1) == -1 && getGameBlockValue(row+2) == -1 && getGameBlockValue(row+3) == -1);
            default:
                //Reports an error if empty is called with an UNDETERMINED direction
                Log.d("ERROR","INVALID EMPTY CALL");
                return true;
        }
    }

    //Returns true if and only if there are spaces for the existing blocks to move to
    @Contract(value = "false, _, _, _ -> true; true, false, true, _ -> true; true, false, false, true -> true; true, true, false, true -> true; true, true, true, _ -> false; true, true, false, false -> false; true, false, false, false -> false", pure = true)
    private boolean checkSpaces(boolean exists1, boolean exists2, boolean exists3, boolean exists4){
        return !exists1 || (!exists2 && (exists3  || exists4)) || (!exists3 && exists4);
    }

    //Uses direction to determine the parameters for checkSpaces and return its results
    private boolean spaceToMove(int targetRowColumn, AccelerometerSensor.DIRECTION d){
        switch(d){
            case UP:
                return checkSpaces(getGameBlockExists(targetRowColumn),getGameBlockExists(targetRowColumn+4),getGameBlockExists(targetRowColumn+8),getGameBlockExists(targetRowColumn+12));
            case DOWN:
                return checkSpaces(getGameBlockExists(targetRowColumn+12),getGameBlockExists(targetRowColumn+8),getGameBlockExists(targetRowColumn+4),getGameBlockExists(targetRowColumn));
            case LEFT: {
                int row = convertRowToStartIndex(targetRowColumn);
                return checkSpaces(getGameBlockExists(row),getGameBlockExists(row+1),getGameBlockExists(row+2),getGameBlockExists(row+3));
            }
            case RIGHT: {
                int row = convertRowToStartIndex(targetRowColumn)+3;
                return checkSpaces(getGameBlockExists(row),getGameBlockExists(row-1),getGameBlockExists(row-2),getGameBlockExists(row-3));
            }
            default:
                Log.d("ERROR","INVALID SPACE TO MOVE CALLED");
                return false;
        }
    }

    //Assignment functions for the "order" arrays
    private void assignMoveBlockOrder(GameBlock a, GameBlock b, GameBlock c, GameBlock d){
        moveBlockOrder[0] = a;
        moveBlockOrder[1] = b;
        moveBlockOrder[2] = c;
        moveBlockOrder[3] = d;
    }

    private void assignBlockExistence(boolean a, boolean b, boolean c, boolean d){
        blockExistenceOrder[0] = a;
        blockExistenceOrder[1] = b;
        blockExistenceOrder[2] = c;
        blockExistenceOrder[3] = d;
    }

    private void assignToRowOrder(int a, int b, int c){
        toRowOrder[0] = a;
        toRowOrder[1] = b;
        toRowOrder[2] = c;
    }

    //Shifts blocks based on where the blocks need to be by the end of the turn
    //Manages both the front end and back end information simultaneously
    private void shift(GameBlock block1, GameBlock block2, GameBlock block3, GameBlock block4, int[] blockLocationOrder, AccelerometerSensor.DIRECTION d){

        //Assigns the "order" values based on the direction of movement
        //RIGHT and DOWN are grouped because they are the positive directions on the UI
        //UP and LEFT are grouped because they are the negative directions on the UI
        switch(d){
            case RIGHT:
            case DOWN:
                assignBlockExistence(block4 != null,block3 != null,block2 != null,block1 != null);
                assignMoveBlockOrder(block4,block3,block2,block1);
                assignToRowOrder(3,2,1);
                break;
            case UP:
            case LEFT:
                assignBlockExistence(block1 != null,block2 != null,block3 != null,block4 != null);
                assignMoveBlockOrder(block1,block2,block3,block4);
                assignToRowOrder(0,1,2);
                break;

        }

        //Runs indexToIndex shifts, starting from the closest index from the
        //direction of motion to the furthest one
        indexToIndexShift(1,0,d,blockLocationOrder);
        indexToIndexShift(2,1,d,blockLocationOrder);
        indexToIndexShift(3,2,d,blockLocationOrder);
    }

    //Determines the actions of the moving GameBlock based on what the still GameBlock is.
    private void indexToIndexShift(int movingBlockIndex, int stillBlockIndex, AccelerometerSensor.DIRECTION d, int[] blockLocationOrder){
        //If there is a block at this location
        if(blockExistenceOrder[movingBlockIndex]){
            //if there is no block in the stillBlockIndex, shift the movingBlockIndex to that index
            if(!blockExistenceOrder[stillBlockIndex]) {
                moveBlock(moveBlockOrder[movingBlockIndex],toRowOrder[stillBlockIndex],d);
                blockHasReachedDestination(moveBlockOrder[movingBlockIndex], blockLocationOrder[movingBlockIndex], blockLocationOrder[stillBlockIndex]);
            }
            //if the two blocks have matching values and they have not merged yet, merge them.
            else if(moveBlockOrder[movingBlockIndex].getValue() == moveBlockOrder[stillBlockIndex].getValue() && !moveBlockOrder[movingBlockIndex].getFinishedMerging() && !moveBlockOrder[stillBlockIndex].getFinishedMerging()){
                moveBlock(moveBlockOrder[movingBlockIndex],toRowOrder[stillBlockIndex],d);
                mergeBlocks(moveBlockOrder[movingBlockIndex], moveBlockOrder[stillBlockIndex], blockLocationOrder[movingBlockIndex],d);
            }
        }
    }

    //Sorts out which direction to move the blocks
    private void moveBlock(GameBlock block, int location, AccelerometerSensor.DIRECTION d){
        switch(d){
            case DOWN:
                block.setRow(location);
                block.moveBlockDownDirection();
                break;
            case UP:
                block.setRow(location);
                block.moveBlockUpDirection();
                break;
            case LEFT:
                block.setColumn(location);
                block.moveBlockLeftDirection();
                break;
            case RIGHT:
                block.setColumn(location);
                block.moveBlockRightDirection();
                break;
        }
    }

    //Checks to see if 2 merging blocks are at the same location.
    //Runs completeMerge if they are
    private void mergeBlocks(GameBlock movingBlock, GameBlock stillBlock, int arrayLocation, AccelerometerSensor.DIRECTION d){
        switch(d){
            case UP:
                if(movingBlock.getY() <= stillBlock.getYCoordinate())
                    completeMerge(stillBlock,arrayLocation);
                break;
            case DOWN:
                if(movingBlock.getY() >= stillBlock.getYCoordinate())
                    completeMerge(stillBlock,arrayLocation);
                break;
            case RIGHT:
                if(movingBlock.getX() >= stillBlock.getXCoordinate())
                    completeMerge(stillBlock,arrayLocation);
                break;
            case LEFT:
                if(movingBlock.getX() <= stillBlock.getXCoordinate())
                    completeMerge(stillBlock,arrayLocation);
                break;
        }
    }

    //Merges 2 blocks together, deleting the moving block and doubling
    //the value of the stillBlock
    private void completeMerge(GameBlock stillBlock, int arrayLocation){
        main_layout.removeView(blockGrid[arrayLocation]);
        main_layout.removeView(blockGrid[arrayLocation].getValueLabel());
        blockGrid[arrayLocation] = null;

        int value = stillBlock.getValue()*2;

        stillBlock.setValue(value);
        stillBlock.setFinishedMerging(true);

        //Updates maxValue on each merge
        maxValue = (value > maxValue) ? value : maxValue;
    }

    //Move a block from one index of the game board to another as it changes position
    private void blockHasReachedDestination(GameBlock block, int oldArrayLocation, int newArrayLocation){
        if(block.getFinishedMoving()){
            blockGrid[newArrayLocation] = block;
            blockGrid[oldArrayLocation] = null;
        }
    }

    //Back end conversions to change row/column to a blockGrid index and vice versa
    @Contract(pure = true)
    private int convertRowToStartIndex(int row){return row*4;}
    @Contract(pure = true)
    private int convertIndexToColumn(int index){
        return index%4;
    }
    @Contract(pure = true)
    private int convertIndexToRow(int index){return index/4;}
}
