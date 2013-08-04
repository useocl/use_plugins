/** 
* @file InfoBoard.h
* This file declares a singleton class, which provides global variables.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include "../Common/CommonTypes.h"

class InfoBoard
{
public:
  /** 
  * The static method returns the singleton instance of InfoBoard.
  * @return The singleton instance of InfoBoard.
  */
  static InfoBoard* theInstance();
  
  AppType AppType;             /**< The app type of the application (debugger or adapter). */

private:
  static InfoBoard* instance;  /**< The singleton instance of InfoBoard. */

  /** 
  * The private default constructor.
  */
  InfoBoard();
};
