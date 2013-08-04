/** 
* @file FieldValueHelper.h
* This file declares the class FieldValueHelper, that provides functionality to get the
* field value of a given field.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/
#pragma once

#include <atlstr.h>
#include <iostream>
#include "../Common/InfoBoard.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CommonTypes.h"
#include "../Common/CLRObject.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/CLRFieldValue.h"
#include "../Common/CLRFieldReference.h"
#include "../Common/CLRFieldList.h"
#include "../Common/CLRDebugCore.h"
#include "../Common/CLRType.h"


class FieldValueHelper
{
public:
  /** 
  * The method returns the field value.
  * @param type The type that declares the desired field.
  * @param object The object that includes the field.
  * @param field The meta data token of the desired field.
  * @returns The value of the desired field.
  */
  static CLRFieldBase* GetField(const CLRType* type, const CLRObject* object, const mdFieldDef field);

private:
  /** 
  * The method creates a specific field value object and returns it.
  * @param name The name of the desired field.
  * @param debugValue A pointer to the ICorDebugValue representation of the desired field.
  * @param fieldDefToken The meta data token of the desired field.
  * @returns A pointer to a concrete field object.
  */
  static CLRFieldBase* CreateSpecificField(CString name, ICorDebugValue* debugValue, mdFieldDef fieldDefToken);

  /** 
  * The method sets the value for a given field.
  * @param field A pointer to the field.
  */
  static void SetFieldValue(CLRFieldBase* field);
};
