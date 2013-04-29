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
