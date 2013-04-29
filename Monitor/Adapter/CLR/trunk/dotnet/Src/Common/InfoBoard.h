#pragma once

#include "../Common/CommonTypes.h"

class InfoBoard
{
public:
  static InfoBoard* theInstance();
  
  AppType AppType;

private:
  static InfoBoard* instance;

  InfoBoard();
};
