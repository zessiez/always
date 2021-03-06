package edu.wpi.always.srummy;

import java.util.List;
import edu.wpi.always.srummy.SrummyUI;

public interface SrummyUI {

   public String getCurrentAgentComment();
   public void triggerAgentPlayTimers();
   public void triggerAgentDiscardOrMeldLayoffDelay ();
   public void cancelHumanCommentingTimer();
   public void triggerHumanCommentingTimer();
   public void triggerNextStateTimer(SrummyUIListener listener);
   public void waitMoreForAgentDrawOptions(SrummyUIListener listener);
   public void waitMoreForAgentDiscardMeldLayoff(SrummyUIListener listener);
   public void cancelUpcomingTimersTillNextRound(SrummyUIListener listener);
   public void startPluginForTheFirstTime(SrummyUIListener listener);
   public void updatePlugin(SrummyUIListener listener);
   public void sendBackAgentMove ();
   String getCurrentAgentResponse (String humanChoosenComment);
   List<String> getCurrentHumanCommentOptionsAgentResponseForAMoveBy (int player);
   void prepareAgentCommentUserResponseForAMoveBy (int player);
   List<String> getCurrentHumanResponseOptions ();
   public void makeBoardPlayable();
   public void makeBoardUnplayable();
   public void setUpGame ();
   public void reset();
   public void setStartingPlayer (int playerIdentifier);

}
