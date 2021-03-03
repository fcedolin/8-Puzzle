/**
 * class puzzle
 *
 * @author Federico Cedolini
 * @version 1.0
 * @since 03/02/2021
 * Known issues: none
 * To be implemented:
 *    High priority: A* search
 *    Low priority: GUI
 */

import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.stream.Collectors;

public class puzzle{
  static HashSet<String> visitedStates = new HashSet<String>();
  static String goalState;

  public static void main(String[] args) {

    //Initial state setup
    String initialStateStr = Arrays.stream(args).collect(Collectors.joining(""));
    System.out.println("Initial state");
    printPuzzle(initialStateStr);
    LinkedList<String> solution;

    //Goal State setup
    goalState = determineGoal(args);
    System.out.println("Goal state");
    printPuzzle(goalState);
    //Find goal node
    puzzleNode goalNode = BFS(initialStateStr);
    //construct path
    solution = solutionPath(goalNode);
    //print path
    printSolutionPath(solution);

  }//main

  /*
   * This method determines the goal state to be used
   * @param initialState is the array with the initial state
   * @return string containing the goal state
   */
  public static String determineGoal(String[] initialState){
    int sum = 0;
    int[] initialStateInt = Arrays.stream(initialState).mapToInt(Integer::parseInt).toArray();

    for (int i = 0; i < initialStateInt.length; i++){
      if(initialStateInt[i] == 0)
        continue;
      for (int j = i; j < initialStateInt.length; j++){
        if(initialStateInt[j] == 0)
          continue;
        if(initialStateInt[i] > initialStateInt[j])
          sum++;
      }
    }
    if(sum % 2 == 0)
      return "123456780"; //Parity 0
    return "123804765"; //Parity 1
  }//determineGoal

  /**
   * BFS created initial node from starting state and perform search for the goal State
   * @param initialStateStr is the String with the initial state
   * @return puzzleNode goal node
   */
  public static puzzleNode BFS(String initialStateStr){
    int zeroPos;
    puzzleNode workingNode;
    LinkedList<puzzleNode> frontier = new LinkedList<puzzleNode>();
    puzzleNode children;
    String childrenStr;
    String workingState;

    //create initial node TODO: MOVE TO OTHER FUNCTION
    for(zeroPos = 0; zeroPos < initialStateStr.length(); zeroPos++){
      if(initialStateStr.charAt(zeroPos) == '0')
        break;
    }
    puzzleNode initialState = new puzzleNode(initialStateStr, null, zeroPos);

    //check if node if goal
    if(isGoal(initialState))
      return initialState;

    //perform search
    workingNode = initialState;
    do{
      workingState = workingNode.getState();

      zeroPos = workingNode.getZeroPos();
      visitedStates.add(workingState);
      //Work on children, sides first
      if(zeroPos % 3 == 0){//left side, check right
        children = getChildren(frontier, workingNode, zeroPos, zeroPos+1);
        if(isGoal(children))
          return children;
      }
      if(zeroPos % 3 == 1){//center, check left and right
        children = getChildren(frontier, workingNode, zeroPos, zeroPos+1);
        if(isGoal(children))
          return children;

        children = getChildren(frontier, workingNode, zeroPos, zeroPos-1);
        if(isGoal(children))
          return children;
      }
      if(zeroPos % 3 == 2){//right side, check left
        children = getChildren(frontier, workingNode, zeroPos, zeroPos-1);
        if(isGoal(children))
          return children;
      }
      //work on children up and down
      if(zeroPos - 3 > -1){//up
        children = getChildren(frontier, workingNode, zeroPos, zeroPos-3);
        if(isGoal(children))
          return children;
      }
      if(zeroPos + 3 < 9){//down
        children = getChildren(frontier, workingNode, zeroPos, zeroPos+3);
        if(isGoal(children))
          return children;
      }
    }while(!frontier.isEmpty() && (workingNode = frontier.remove()) != null); //review statement
    return null;
  }//BFS

  /**
   * isGoal determines if the node has the goal state
   * @param node is the node to be compared with the goal state
   * @return true if the node has the goal state, false otherwise
   */
  public static boolean isGoal(puzzleNode node){
    if(node == null)
      return false;
    return goalState.equals(node.getState());
  }//isGoal

  /**
   * getChildren calculates the children based on parameters provided
   * @param frontier contains the nodes to be visited
   * @param workingNode is the parent node of the child to be created
   * @param zeroPos is the position of the zero in the parent nodes
   * @param newZeroPos is the position that zero will have in the child
   * @return the new child node
   */
  public static puzzleNode getChildren(LinkedList<puzzleNode> frontier, puzzleNode workingNode, int zeroPos, int newZeroPos){
    String childrenStr = swapChars(workingNode.getState(), zeroPos, newZeroPos);
    if(visitedStates.contains(childrenStr))
      return null;
    puzzleNode childrenNode = new puzzleNode(childrenStr, workingNode, newZeroPos);
    frontier.add(childrenNode);
    visitedStates.add(childrenStr);
    return childrenNode;
  }//getChildren

  /**
   * swapChars creates a new string by changing the order of 2 characters
   * @param stateStr the original string
   * @param i the position of one character
   * @param j the position of the other character
   * @return a new string with the characters moved
   */
  public static String swapChars(String stateStr, int i, int j){
    StringBuilder sb = new StringBuilder(stateStr);
    sb.setCharAt(i, stateStr.charAt(j));
    sb.setCharAt(j, stateStr.charAt(i));
    return sb.toString();
  }//swapChars

  /**
   * solutionPath traces back the solution from the goal node
   * @param state is the goal node
   * @return the list with the path from the initial node to the goal node
   */
  public static LinkedList<String> solutionPath(puzzleNode state){
    puzzleNode workingNode = state; //may not need new node. TO BE checked
    LinkedList<String> sol = new LinkedList<String>();
    do{
      sol.addFirst(workingNode.getState());
      workingNode = workingNode.getParent();
    }while(workingNode != null);

    return sol;
  }//solutionPath

  /**
   * printSolutionPath prints the path created by solutionPath
   * @param solutionPath is the list created by solutionPath()
   */
  public static void printSolutionPath(LinkedList<String> solutionPath){
    for(String state: solutionPath){
      printPuzzle(state);
    }
  }//printSolutionPath

  /**
   * printPuzzle prints individual states
   * @param state to be printed
   */
  public static void printPuzzle(String state){
    for(int i = 0; i < 9; i++){
      if(i % 3 == 0)
        System.out.println("");
      System.out.printf("%c ", state.charAt(i));
    }
    System.out.println("\n");
  }//printPuzzle

}//puzzle
