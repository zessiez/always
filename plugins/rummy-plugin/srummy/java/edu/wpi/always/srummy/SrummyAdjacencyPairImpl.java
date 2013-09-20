package edu.wpi.always.srummy;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class SrummyAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<SrummyStateContext>
implements SrummyUIListener {

   public SrummyAdjacencyPairImpl(
         String message, SrummyStateContext context) {
      super(message, context);
   }

   public SrummyAdjacencyPairImpl (String message,
         SrummyStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   public void skipTo (AdjacencyPair nextAdjacencyPair) {
      setNextState(nextAdjacencyPair);
   }
   
   @Override
   public void receivedNewState(){
      newStateReceived();
   }
   protected void newStateReceived(){}
   
   @Override
   public void receivedHumanMove() {
      humanMoveReceived();
   }
   protected void humanMoveReceived(){}
   
   @Override
   public void receivedAgentMoveOptions(String moveType) {
      agentMoveOptionsReceived(moveType);
   }
   protected void agentMoveOptionsReceived(String moveType){}
   
   @Override
   public void humanCommentTimeOut(){
      afterTimeOut();
   }
   protected void afterTimeOut(){}

   @Override 
   public void agentDrawDelayOver(){
      aferAgentDrawDelay();
   }
   protected void aferAgentDrawDelay(){}
   
   @Override
   public void agentDiscardDelayOver(){
      afterAgentDiscardDelay();
   }
   protected void afterAgentDiscardDelay(){}
   
   @Override
   public void agentPlayDelayOver(){
      afterDrawAfterGazeAfterThinkingDelay();
   }
   protected void afterDrawAfterGazeAfterThinkingDelay(){}
   
   @Override
   public void agentPlayingGazeDelayOver () {
      afterAgentPlayingGazeDelay();
   }
   protected void afterAgentPlayingGazeDelay(){}
   
   @Override
   public void nextState () {
      goToNextState();      
   }
   public void goToNextState(){}

}