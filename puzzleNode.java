/**
 * class puzzleNode
 *
 * @author Federico Cedolini
 * @version 1.0
 * @since 03/02/2021
 * Known issues: none
 */
public class puzzleNode{
  String state;
  puzzleNode parent;
  int zeroPos;
  int heapValue;

  /*
   * constructor for puzzleNode object
   * @param state string containing the state of the puzzle
   * @param parent is the parent node of this new object
   * @param zeroPos is the position of the zero in state
   */
  public puzzleNode(String state, puzzleNode parent, int zeroPos){
    super();
    this.state = state;
    this.parent = parent;
    this.zeroPos = zeroPos;
    this.heapValue = 0; //not used
  }//puzzleNode

  public puzzleNode(String state, puzzleNode parent, int zeroPos, int heapValue){
    this.state = state;
    this.parent = parent;
    this.zeroPos = zeroPos;
    this.heapValue = heapValue;
  }

  /*
   * getState is accessor for state string
   */
  public String getState(){
    return state;
  }//getState

  /*
   * puzzleNode is accessor for the parent node, null for initial state
   */
  public puzzleNode getParent(){
    return parent;
  }//getParent

  /*
   * getZeroPos is accessor for zeroPos
   */
  public int getZeroPos(){
    return zeroPos;
  }//getZeroPos

  /*
   * getHeapValue is accessor for heapValue
   */
  public int getHeapValue(){
    return heapValue;
  }//getheapValue

  /*
   * setHeapValue changes the value for heapValue
   */
  public void setHeapValue(int value){
    heapValue = value;
  }//getheapValue
}
