/**
 * class puzzle
 *
 * @author Federico Cedolini
 * @version 1.0
 * @since 03/13/2021
 * Known issues: none
 * To be implemented:
 *    Low priority: GUI
 */

import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.stream.Collectors;

public class puzzle{
  static HashSet<String> visitedStates = new HashSet<String>();
  static HashMap<Character, pair> goalH2map = new HashMap<>();
  static String goalState;
  static puzzleNode goalNode;
  static int numVisited = 0;

  static final String PARITY_ZERO = "123456780";
  static final String PARITY_ONE = "123804765";


  public static void main(String[] args) {

    LinkedList<String> solution;

    if(args.length != 9){
      args = randomInitialState();
    }

    //Initial state setup
    String initialStateStr = Arrays.stream(args).collect(Collectors.joining(""));
    System.out.println("Initial state");
    printPuzzle(initialStateStr);
    puzzleNode initialState = setupInitialNode(initialStateStr);

    //Goal State setup
    goalState = determineGoal(args);
    System.out.println("Goal state");
    printPuzzle(goalState);
    setupGoalH2map();

    //perform BFS
    System.out.println("Performing BFS");
    goalNode = BFS(initialState);
    if(goalNode == null){
      System.out.println("ERROR finding solution in BFS.");
      System.exit(1);
    }
    //construct and print path
    solution = solutionPath(goalNode);
    printSolutionPathN(solution, 5);
    resetAll(); //reset all variable for next search

    //perform A* with h1
    System.out.println("Performing A* serach with h1");
    goalNode = ASearch(initialState, 1);
    if(goalNode == null){
      System.out.println("ERROR finding solution in A* h1.");
      System.exit(1);
    }
    //construct and print path
    solution = solutionPath(goalNode);
    printSolutionPathN(solution, 5);
    resetAll(); //reset all variable for next search

    //perform A* with h2
    System.out.println("Performing A* serach with h2");
    goalNode = ASearch(initialState, 2);
    if(goalNode == null){
      System.out.println("ERROR finding solution in A* h2.");
      System.exit(1);
    }
    //construct and print path
    solution = solutionPath(goalNode);
    printSolutionPathN(solution, 5);

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
      return PARITY_ZERO; //Parity 0
    return PARITY_ONE; //Parity 1
  }//determineGoal

  /**
   * BFS created initial node from starting state and perform search for the goal State
   * @param initialStateStr is the String with the initial state
   * @return puzzleNode goal node
   */
  public static puzzleNode BFS(puzzleNode initialState){
    puzzleNode workingNode;
    LinkedList<puzzleNode> frontier = new LinkedList<puzzleNode>();
    LinkedList<puzzleNode> possibleMoves;

    //check if node if goal
    if(isGoal(initialState))
      return initialState;

    visitedStates.add(initialState.getState());

    //perform search
    workingNode = initialState;
    do{
      possibleMoves = getAllMoves(workingNode);

      for(puzzleNode child: possibleMoves){
        if(child != null){
          numVisited++;
          if(isGoal(child))
            return child;
          frontier.add(child);
        }
      }

      possibleMoves.clear();
    }while(!frontier.isEmpty() && (workingNode = frontier.remove()) != null);
    System.out.println("ERROR");
    return null; //exit if null
  }//BFS


  /**
   * perform A* searchfrom initial node to goal node
   * @param initialStateStr is the String with the initial state
   * @return puzzleNode goal node
   */
  public static puzzleNode ASearch(puzzleNode initialState, int heuristic){
    puzzleNode workingNode;
    int rootDist = 0;

    Comparator<puzzleNode> valueSort = Comparator.comparing(puzzleNode::getHeapValue);
    PriorityQueue<puzzleNode> nodesQueue = new PriorityQueue<>( valueSort );
    LinkedList<puzzleNode> possibleMoves;

    //check if node if goal
    if(isGoal(initialState))
      return initialState;

    visitedStates.add(initialState.getState());

    //perform search
    workingNode = initialState;
    do{
      rootDist = workingNode.getHeapValue() + 1;
      possibleMoves = getAllMoves(workingNode);

      for(puzzleNode child: possibleMoves){
        if(child != null){
          numVisited++;
          if(isGoal(child))
            return child;
          if(heuristic == 1){
          child.setHeapValue(rootDist + heuristic1(child.getState()));
        } else {
          child.setHeapValue(rootDist + heuristic2(child.getState()));
        }
          nodesQueue.add(child);
        }
      }
      possibleMoves.clear();
    }while((workingNode = nodesQueue.poll()) != null);
    System.out.println("ERROR");
    return null; //exit if null
  }//ASearch

  /**
   * heuristic1 calculates the number of characters out of order in the current state
   * @param state is the current state for which we need to calculate the difference
   * @return the number of character our of order
   */
  public static int heuristic1(String state){
    char c;
    int counter = 0;
    for(int i = 0; i < 9; i++){
      c = state.charAt(i);
      if(c != '0'){
        if(c != goalState.charAt(i))
          counter++;
      }
    }
    return counter;
  }//heuristic1

  /**
   * heuristic2 calculates each Manhattan distance and returns the total
   * @param state is the current state for which we need to calculate the difference
   * @return sum of all Manhattan distance
   */
  public static int heuristic2(String state){
    int counter = 0;
    pair goalVal;
    for(int i = 0; i < 9; i++){
      goalVal = goalH2map.get(state.charAt(i));
      counter = counter + Math.abs(goalVal.mod - (i%3) + Math.abs(goalVal.div - (i/3)));
      }
    return counter;
  }//heuristic2

  /**
   * setupGoalH2map populates goal map for H2 constant time access
   */
  public static void setupGoalH2map(){
    for(int i = 0; i < 9; i++){
      goalH2map.put(goalState.charAt(i), new pair(i%3, i/3));
    }
  }//setupGoalH2map


  public static LinkedList<puzzleNode> getAllMoves(puzzleNode workingNode){
    String workingState = workingNode.getState();
    int zeroPos = workingNode.getZeroPos();
    LinkedList<puzzleNode> movesList = new LinkedList<puzzleNode>();
    //visitedStates.add(workingState);

    //Work on children, sides first
    switch(zeroPos % 3){
      case 0: //left side, check right
              movesList.add(getChildren(workingNode, zeroPos, zeroPos+1));
              break;
      case 1: //center, check left and right
              movesList.add(getChildren(workingNode, zeroPos, zeroPos+1));
              movesList.add(getChildren(workingNode, zeroPos, zeroPos-1));
              break;
      case 2: //right side, check left
              movesList.add(getChildren(workingNode, zeroPos, zeroPos-1));
              break;
    }

    //work on children up and down
    if(zeroPos - 3 > -1){//up
      movesList.add(getChildren(workingNode, zeroPos, zeroPos-3));
    }
    if(zeroPos + 3 < 9){//down
      movesList.add(getChildren(workingNode, zeroPos, zeroPos+3));
    }

    return movesList;
  }//getAllMoves

  /**
   * setupInitialNode creates initial state node
   * @param initialStateStr is the state of the initial node
   * @return initial state node
   */
  public static puzzleNode setupInitialNode(String initialStateStr){
    int zeroPos;
    for(zeroPos = 0; zeroPos < initialStateStr.length(); zeroPos++){
      if(initialStateStr.charAt(zeroPos) == '0')
        break;
    }
    return new puzzleNode(initialStateStr, null, zeroPos);
  }//setupInitialNode

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
  public static puzzleNode getChildren(puzzleNode workingNode, int zeroPos, int newZeroPos){
    String childrenStr = swapChars(workingNode.getState(), zeroPos, newZeroPos);
    if(visitedStates.contains(childrenStr))
      return null;
    puzzleNode childrenNode = new puzzleNode(childrenStr, workingNode, newZeroPos);
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
    int movesNumber = -1;
    do{
      sol.addFirst(workingNode.getState());
      workingNode = workingNode.getParent();
      movesNumber++;
    }while(workingNode != null);
    System.out.println("Total number of moves required: " + movesNumber);

    return sol;
  }//solutionPath

  /**
   * printSolutionPath prints the path created by solutionPath
   * @param solutionPath is the list created by solutionPath()
   */
  public static void printSolutionPath(LinkedList<String> solutionPath){
    System.out.println("Printing all steps. (including initial and goal)");
    for(String state: solutionPath){
      printPuzzle(state);
    }
  }//printSolutionPath

  /**
   * printSolutionPathN prints the first n elements in the path created by solutionPath
   * @param solutionPath is the list created by solutionPath()
   * @param n the number of steps to print
   */
  public static void printSolutionPathN(LinkedList<String> solution, int n){
    System.out.println("Number of nodes visited: " + numVisited);
    if(n > solution.size()){
      System.out.println("Solution has less than " + n + " steps.");
      printSolutionPath(solution);
      }
    else {
      System.out.println("Printing first " + n + " steps.");
      for(int i = 1; i <= n; i++)
        printPuzzle(solution.get(i));
    }
  }//printSolutionPathN

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

  /**
   * randomInitialState creates a random initial state when user doesn't provide one
   * @return array of strings that represent initial state
   */
  public static String[] randomInitialState(){
    String[] initialArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};
     List<String> initialList = Arrays.asList(initialArray);
     Collections.shuffle(initialList);
     return initialList.toArray(initialArray);
  }//randomInitialState

  /**
   * resetAll resets all objects for reuse of other searches
   */
  public static void resetAll(){
    visitedStates.clear();
    goalNode = null;
    numVisited = 0;
  }//resetAll
}//puzzle class
