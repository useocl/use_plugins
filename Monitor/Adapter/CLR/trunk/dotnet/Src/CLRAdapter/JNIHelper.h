#pragma once

#include <assert.h>
#include <jni.h>
#include "../Common/CommonTypes.h"
#include "../Common/CLRType.h"
#include "../Common/CLRObject.h"
#include "../Common/InfoBoard.h"

class JNIHelper
{
public:
  static CLRType* GetCLRType(JNIEnv* env, const jobject clrType);

  static CLRObject* GetCLRObject(JNIEnv* env, const jobject clrObject);

  static mdFieldDef GetFieldDef(JNIEnv* env, const jobject clrField);

  static jobject GetFieldPValue(JNIEnv* env, ICorDebugGenericValue* debugValue, CorElementType type);
};

