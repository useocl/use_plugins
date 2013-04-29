#include "JNIHelper.h"

CLRType* JNIHelper::GetCLRType(JNIEnv* env, const jobject clrType, const TypeInfoHelper& typeInfo)
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

  jlong typeId = env->CallNonvirtualLongMethod(clrType, typeClass, typeGetId);
 
  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  searchName = CString(strTypeName);
  clrTypeDef = (mdTypeDef)typeId;
  pClrType = typeInfo.GetType(clrTypeDef);

  // TODO: remove search name
  assert(pClrType);
  assert(pClrType->typeDefToken == clrTypeDef);
  assert(pClrType->name == searchName);

  // release
  env->ReleaseStringUTFChars((jstring)typeName, strTypeName);

  return pClrType;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return NULL;
}

CLRObject* JNIHelper::GetCLRObject(JNIEnv* env, const jobject clrObject, const ObjectInfoHelper& objectInfo)
{
  jthrowable ex = NULL;
  CORDB_ADDRESS address = 0;
  ObjectMap::const_iterator gotObject;

  // get address from given object
  jclass objectCLRClass = env->GetObjectClass(clrObject);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID objectCLRGetId = env->GetMethodID(objectCLRClass, "getIdCLR", "()J");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jlong objectCLRId = env->CallNonvirtualLongMethod(clrObject, objectCLRClass, objectCLRGetId);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  address = (CORDB_ADDRESS)objectCLRId;

  return objectInfo.GetCLRObject(address);

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

  jlong fieldId = env->CallNonvirtualLongMethod(clrField, fieldClass, fieldGetId);

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
  HRESULT hr = E_FAIL;

  jobject res = env->NewGlobalRef(NULL);
  jthrowable ex = NULL;
  jclass resClass = NULL;
  jmethodID resCtor = 0;

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
        jboolean jRes = (jboolean)value;

        resClass = env->FindClass("java/lang/Boolean");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(Z)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_CHAR:
    {
      char value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jchar jRes = (jchar)value;

        resClass = env->FindClass("java/lang/Character");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(C)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_I1:               // 8 bit signed integer
    {
      __int8 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jbyte jRes = (jbyte)value;

        resClass = env->FindClass("java/lang/Byte");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(B)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_U1:               // 8 bit unsigned integer
    {
      unsigned __int8 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jshort jRes = (jshort)value;

        resClass = env->FindClass("java/lang/Short");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(S)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_I2:               // 16 bit signed integer
    {
      __int16 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jshort jRes = (jshort)value;

        resClass = env->FindClass("java/lang/Short");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(S)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_U2:               // 16 bit unsigned integer
    {
      unsigned __int16 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jint jRes = (jint)value;

        resClass = env->FindClass("java/lang/Integer");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(I)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
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
        jint jRes = (jint)value;

        resClass = env->FindClass("java/lang/Integer");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(I)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
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
        jlong jRes = (jlong)value;

        resClass = env->FindClass("java/lang/Long");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(J)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_I8:               // 64 bit signed integer
    {
      __int64 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jlong jRes = (jlong)value;

        resClass = env->FindClass("java/lang/Long");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(J)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_U8:               // 64 bit unsigned integer
    {
      unsigned __int64 value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jlong jRes = (jlong)value;

        resClass = env->FindClass("java/lang/Long");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(J)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;


  case ELEMENT_TYPE_R4:               // 32 bit float
    {
      float value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jfloat jRes = (jfloat)value;

        resClass = env->FindClass("java/lang/Float");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(F)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  case ELEMENT_TYPE_R8:               // 64 bit float
    {
      double value;
      hr = debugValue->GetValue(&value);

      if(SUCCEEDED(hr))
      {
        jdouble jRes = (jdouble)value;

        resClass = env->FindClass("java/lang/Double");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        resCtor = env->GetMethodID(resClass, "<init>", "(D)V");
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;

        res = env->NewObject(resClass, resCtor, jRes);
        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }
    }
    break;

  default:
    break;
  }

  return res;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return res;
}
