package edu.wpi.always.client;

public interface Keyboard {

   void showKeyboard (String prompt, boolean isNumeric);
   
   void hideKeyboard ();
   
   String getInputSoFar ();
   
   boolean isOverflow ();
   
   void setOverflow (boolean overflow);
}
