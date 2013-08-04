/** 
* @file JNIHelper.h
* This file declares a class that provides helping methods for the work with JNI.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include <assert.h>
#include <jni.h>
#include "../Common/CommonTypes.h"
#include "../Common/CLRType.h"
#include "../Common/CLRObject.h"
#include "../Common/InfoBoard.h"
#include "../Common/TypeInfoHelper.h"
#include "../Common/ObjectInfoHelper.h"

class JNIHelper
{
public:
  /** 
  * The method searches the CLR CLRType to a given Java CLRType object.
  * @param env The JNI environment.
  * @param clrType The Java object, that represents the Type.
  * @param typeInfo A reference to the instance of the TypeInfoHelper.
  * @returns A Pointer to the CLR CLRType object.
  */
  static CLRType* GetCLRType(JNIEnv* env, const jobject clrType, const TypeInfoHelper& typeInfo);

  /** 
  * The method searches the CLR CLRObject to a given Java CLRObject instance.
  * @param env The JNI environment.
  * @param clrObject The Java object, that represents the instance.
  * @param objectInfo A reference to the instance of the ObjectInfoHelper.
  * @returns A Pointer to the CLR CLRObject instance.
  */
  static CLRObject* GetCLRObject(JNIEnv* env, const jobject clrObject, const ObjectInfoHelper& objectInfo);

  /** 
  * The method reads out the field definition token.
  * @param env The JNI environment.
  * @param clrField The Java CLRField object.
  * @returns The field definition token.
  */
  static mdFieldDef GetFieldDef(JNIEnv* env, const jobject clrField);

  /** 
  * The method returns the value of a given field representing a primitive value.
  * @param env The JNI environment.
  * @param debugValue An ICorDebugGenericValue interface to get the field value.
  * @param type The CorElementType of the field.
  * @returns A Java object containing the field value.
  */
  static jobject GetFieldPValue(JNIEnv* env, ICorDebugGenericValue* debugValue, CorElementType type);
};
