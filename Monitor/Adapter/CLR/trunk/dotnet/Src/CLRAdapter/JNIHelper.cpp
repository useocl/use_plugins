#include "JNIHelper.h"

CLRType* JNIHelper::GetCLRType(JNIEnv* env, const jobject clrType)
{
  jthrowable ex = NULL;
  mdTypeDef clrTypeDef = 0;
  CString searchName(_T(""));
  CLRType* pClrType = NULL;
  TypeMap::const_iterator gotType;

  jclass typeClass = env->GetObjectClass(clrType);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID typeGetName = env->GetMethodID(typeClass, "getName", "()Ljava/lang/String;");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID typeGetId = env->GetMethodID(typeClass, "getIdCLR", "()J");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jobject typeName = env->CallObjectMethod(clrType, typeGetName);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  const char* strTypeName = env->GetStringUTFChars((jstring)typeName, NULL);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jlong typeId = env->CallLongMethod(clrType, typeGetId);
 
  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  searchName = CString(strTypeName);
  clrTypeDef = (mdTypeDef)typeId;
  gotType = InfoBoard::theInstance()->loadedTypes.find(searchName);

  if(gotType != InfoBoard::theInstance()->loadedTypes.end())
  {
    pClrType = (*gotType).second;
    assert(pClrType->typeDefToken == clrTypeDef);
  }
  else
  {
    //TODO: throw exception
    return NULL;
  }

  // release
  env->ReleaseStringUTFChars((jstring)typeName, strTypeName);

  return pClrType;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return NULL;
}

CLRObject* JNIHelper::GetCLRObject(JNIEnv* env, const jobject clrObject)
{
  jthrowable ex = NULL;
  CORDB_ADDRESS address = 0;
  CLRObject* pClrObject = NULL;
  ObjectMap::const_iterator gotObject;

  // get address from given object
  jclass objectCLRClass = env->GetObjectClass(clrObject);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID objectCLRGetId = env->GetMethodID(objectCLRClass, "getId", "()J");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // TODO: FIXME!
  jlong objectCLRId = env->CallLongMethod(objectCLRClass, objectCLRGetId);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  address = (CORDB_ADDRESS)objectCLRId;

  // search given object in loaded ones
  gotObject = InfoBoard::theInstance()->currentObjects.find(address);

  if(gotObject != InfoBoard::theInstance()->currentObjects.end())
    pClrObject = (*gotObject).second;

  return pClrObject;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return NULL;
}

mdFieldDef JNIHelper::GetFieldDef(JNIEnv* env, const jobject clrField)
{
  jthrowable ex = NULL;
  mdFieldDef fieldToken = 0;

  // get token from given field
  jclass fieldClass = env->GetObjectClass(clrField);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID fieldGetId = env->GetMethodID(fieldClass, "getIdCLR", "()J");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jlong fieldId = env->CallLongMethod(fieldClass, fieldGetId);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  fieldToken = (mdFieldDef)fieldId;

  return fieldToken;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return fieldToken;
}

jobject JNIHelper::GetFieldPValue(JNIEnv* env, ICorDebugGenericValue* debugValue, CorElementType type)
{
  jobject res = env->NewGlobalRef(NULL);
  HRESULT hr = E_FAIL;

  if(!debugValue)
    return res;

  switch(type)
  {
  case ELEMENT_TYPE_BOOLEAN:
    {
      bool value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jboolean jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_CHAR:
    {
      char value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jchar jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_I1:               // 8 bit signed integer
    {
      __int8 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jbyte jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_U1:               // 8 bit unsigned integer
    {
      unsigned __int8 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jshort jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_I2:               // 16 bit signed integer
    {
      __int16 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jshort jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_U2:               // 16 bit unsigned integer
    {
      unsigned __int16 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jint jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_I4:               // 32 bit signed integer
  case ELEMENT_TYPE_I:
    {
      __int32 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jint jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_U4:               // 32 bit unsigned integer
  case ELEMENT_TYPE_U:
    {
      unsigned __int32 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jlong jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_I8:               // 64 bit signed integer
    {
      __int64 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jlong jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

  case ELEMENT_TYPE_U8:               // 64 bit unsigned integer
    {
      unsigned __int64 value;

      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jlong jRes = value;
        res = (jobject)jRes;
      }
    }
    break;

    //TODO: FIXME!

  //case ELEMENT_TYPE_R4:               // 32 bit float
  //  {
  //    float value;

  //    hr = debugValue->GetValue(&value);

  //    if(SUCCEEDED(hr))
  //    {
  //      jfloat jRes = value;
  //      res = (jobject)jRes;
  //    }
  //  }
  //  break;

  //case ELEMENT_TYPE_R8:               // 64 bit float
  //  {
  //    double value;

  //    hr = debugValue->GetValue(&value);

  //    if(SUCCEEDED(hr))
  //    {
  //      jdouble jRes = value;
  //      res = (jobject)jRes;
  //    }
  //  }
  //  break;

  default:
    break;
  }

  return res;
}
