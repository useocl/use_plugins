/** 
* @file InfoBoard.cpp
* This file implements a singleton class, which provides global variables.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "../Common/InfoBoard.h"

InfoBoard::InfoBoard() : 
  AppType(ADAPTER)
{ }

InfoBoard* InfoBoard::instance = 0;

InfoBoard* InfoBoard::theInstance()
{
  if(!InfoBoard::instance)
    InfoBoard::instance = new InfoBoard();
  return InfoBoard::instance;
}
