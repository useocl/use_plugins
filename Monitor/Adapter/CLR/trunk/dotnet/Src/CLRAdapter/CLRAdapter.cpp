/** 
* @file CLRAdapter.cpp
* This file implements a the machine generated JNI header. Methods are declared in the Java class CLRAdapter.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#include "CLRAdapter.h"

#include "JNIHelper.h"
#include "../Common/CLRDebugCore.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/CLRFieldValue.h"
#include "../Common/CLRFieldReference.h"
#include "../Common/CLRFieldList.h"
#include "../Common/CLRDebugCallback.h"
#include "../Common/TypeInfoHelper.h"
#include "../Common/ObjectInfoHelper.h"

TypeInfoHelper typeInfo;                /**< The instance of TypeInfoHelper for the entire adapter. */
ObjectInfoHelper objectInfo(typeInfo);  /**< The instance of ObjectInfoHelper for the entire adapter. */

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_attachToCLR
  (JNIEnv* env, jobject adapter, jlong pid)
{
  CLRDebugCore::theInstance()->InitializeProcessesByPid((DWORD)pid, new CLRDebugCallback(typeInfo));

  return CLRDebugCore::theInstance()->pDebugProcess ? 0 : -1;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_resumeCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  hr = CLRDebugCore::theInstance()->pDebugProcess->Continue(FALSE);
  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_suspendCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  hr = CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_stopCLR
  (JNIEnv* env, jobject adapter)
{
  HRESULT hr = E_FAIL;
  CLRDebugCore::theInstance()->pDebugProcess->Stop(0);
  hr = CLRDebugCore::theInstance()->pDebugProcess->Detach();

  objectInfo.Detach();
  typeInfo.Detach();

  CLRDebugCore::theInstance()->Release();

  if(FAILED(hr))
    return -1;
  else
    return 0;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfInstances
  (JNIEnv* env, jobject adapter)
{
  jint res = (jint)objectInfo.InstanceCount();
  return res;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfTypes
  (JNIEnv* env, jobject adapter)
{
  jint res = (jint)typeInfo.TypeCount();
  return res;
}

JNIEXPORT jint JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getNumOfModules
  (JNIEnv* env, jobject adapter)
{
  jint res = (jint)typeInfo.ModuleCount();
  return res;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getCLRType
  (JNIEnv* env, jobject adapter, jstring name)
{
  jboolean isCopyName = JNI_FALSE;
  const char* typeName = env->GetStringUTFChars(name, &isCopyName);
  CString searchName(typeName);
  jobject clrType = env->NewGlobalRef(NULL);
  jthrowable ex = NULL;

  jclass typeClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRType");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID constructorId = env->GetMethodID(typeClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;JLjava/lang/String;)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  //search type
  CLRType* type = typeInfo.GetType(searchName);

  if(type)
  {
    jlong typeId = type->typeDefToken;
    jobject res = env->NewObject(typeClass, constructorId, adapter, typeId, name);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    clrType = res;
  }

  // free memory
  if(isCopyName)
    env->ReleaseStringUTFChars(name, typeName);

  return clrType;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return clrType;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getInstances
  (JNIEnv* env, jobject adapter, jobject clrType)
{
  jthrowable ex = NULL;
  jobject hashSet = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;

  // create java HashSet to return
  jclass setClass = env->FindClass("java/util/HashSet");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setConstructor = env->GetMethodID(setClass, "<init>", "()V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  hashSet = env->NewObject(setClass, setConstructor);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setAdd = env->GetMethodID(setClass, "add", "(Ljava/lang/Object;)Z");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // get type information
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return hashSet;

  // get instances
  jobject clrObject = env->NewGlobalRef(NULL);
  jclass clrObjectClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRObject");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID clrObjectConstructor = env->GetMethodID(clrObjectClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;Lorg/tzi/use/plugins/monitor/vm/mm/VMType;J)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  objectInfo.GetInstances(pClrType);

  for(std::vector<CORDB_ADDRESS>::const_iterator obj = pClrType->instances.begin(); obj != pClrType->instances.end(); ++obj)
  {
    jlong jAddress = *obj;
    clrObject = env->NewObject(clrObjectClass, clrObjectConstructor, adapter, clrType, jAddress);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;
    else
    {
      env->CallBooleanMethod(hashSet, setAdd, clrObject);
      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;
    }
  }

  return hashSet;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return hashSet;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getFieldByName
  (JNIEnv* env, jobject adapter, jobject clrType, jstring name)
{
  jobject clrField = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;
  CLRMetaField* clrMField = NULL;
  jboolean isCopyName = JNI_FALSE;
  const char* typeName = env->GetStringUTFChars(name, &isCopyName);
  CString searchName(typeName);
  jthrowable ex = NULL;

  jclass clrFieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRField");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID clrFieldConstructor = env->GetMethodID(clrFieldClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;Ljava/lang/String;J)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // find given type
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return clrField;

  // find field def token
  clrMField = pClrType->GetField(searchName);
  if(!clrMField)
    return clrField;

  // create new CLRField
  jlong token = (jlong)clrMField->fieldDef;
  clrField = env->NewObject(clrFieldClass, clrFieldConstructor, adapter, name, token);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // release
  if(isCopyName)
    env->ReleaseStringUTFChars(name, typeName);

  return clrField;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return clrField;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getWrappedField
  (JNIEnv* env, jobject adapter, jobject clrType, jobject clrObject, jobject clrField)
{
  jthrowable ex = NULL;
  jobject wrapper = env->NewGlobalRef(NULL);
  jobject res = env->NewGlobalRef(NULL);
  CLRObject* pClrObject = NULL;
  CLRFieldBase* pField = NULL;
  CLRType* pClrType = NULL;
  mdFieldDef fieldToken = 0;

  // find given type
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return wrapper;

  // get pointer to given clr object
  pClrObject = JNIHelper::GetCLRObject(env, clrObject, objectInfo);
  if(!pClrObject)
    return wrapper;

  // get field token for given field
  fieldToken = JNIHelper::GetFieldDef(env, clrField);
  if(!fieldToken)
    return wrapper;

  // get desired field
  pField = objectInfo.GetField(pClrType, pClrObject->address, fieldToken);
  if(!pField)
    return wrapper;

  // if null value return
  if(pField->isNull)
    return wrapper;

  // get wrapper class information
  switch (pField->type)
  {
  case VALUE:
    {
      CLRFieldValue* pFieldV = static_cast<CLRFieldValue*>(pField);

      // get value
      if(pFieldV->corType == ELEMENT_TYPE_STRING)
      {
        CStringA str(pFieldV->valueAsString);
        res = env->NewStringUTF((const char*)str);
      }
      else
        res = JNIHelper::GetFieldPValue(env, pFieldV->genericDebugValue, pFieldV->corType);

      // create value field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapValue");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(JLjava/lang/Object;)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = pFieldV->fieldDefToken;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token, res);

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      break;
    }
  case REFERENCE:
    {
      CLRFieldReference* pFieldR = static_cast<CLRFieldReference*>(pField);

      // create reference field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapReference");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(JJ)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = (jlong)pFieldR->fieldDefToken;
      jlong address = (jlong)pFieldR->address;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token, address);

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      break;
    }
  case ARRAY:
    {
      CLRFieldList* pFieldL = static_cast<CLRFieldList*>(pField);

      // create array field wrapper
      jclass fieldClass = env->FindClass("org/tzi/use/plugins/monitor/vm/wrap/clr/CLRFieldWrapRefArray");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jmethodID fieldConstructor = env->GetMethodID(fieldClass, "<init>", "(J)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      jlong token = pFieldL->fieldDefToken;

      wrapper = env->NewObject(fieldClass, fieldConstructor, token);


      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      // put addresses into array field wrapper
      jmethodID fieldAdd = env->GetMethodID(fieldClass, "addReference", "(J)V");

      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;

      for(std::vector<CORDB_ADDRESS>::const_iterator refit = pFieldL->references.begin(); refit != pFieldL->references.end(); ++refit) 
      {
        jlong address = *refit;

        env->CallVoidMethod(wrapper, fieldAdd, address);

        if((ex = env->ExceptionOccurred()) != NULL)
          goto exception_handler;
      }

      break;
    }
  default:
    return wrapper;
  }

  return wrapper;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return wrapper;
}

JNIEXPORT jboolean JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_isCLRAdapterInitialized
  (JNIEnv *, jobject)
{
  return typeInfo.IsInitialized();
}

JNIEXPORT jboolean JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_isCLRClassType
  (JNIEnv* env, jobject adapter, jobject clrType)
{
  CLRType* pClrType = NULL;

  // get type information
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return false;

  return typeInfo.GetTypeInfo(pClrType->typeAttr) == TypeInfo::NClass;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getCLRSuperClasses
  (JNIEnv* env, jobject adapter, jobject clrType)
{
  jthrowable ex = NULL;
  jobject hashSet = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;

  // create java HashSet to return
  jclass setClass = env->FindClass("java/util/HashSet");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setConstructor = env->GetMethodID(setClass, "<init>", "()V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  hashSet = env->NewObject(setClass, setConstructor);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setAdd = env->GetMethodID(setClass, "add", "(Ljava/lang/Object;)Z");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // get type information
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return hashSet;

  std::wcerr << _T("Baseclass of ") << (const wchar_t*)pClrType->name << std::endl;

  // create type of baseclass if available
  if(pClrType->baseClass)
  {
    jclass typeClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRType");

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    jmethodID constructorId = env->GetMethodID(typeClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;JLjava/lang/String;)V");

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    jlong typeId = pClrType->baseClass->typeDefToken;
    CStringA str(pClrType->baseClass->name);
    jstring name = env->NewStringUTF((const char*)str);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    jobject clrType = env->NewObject(typeClass, constructorId, adapter, typeId, name);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;
    else
    {
      env->CallBooleanMethod(hashSet, setAdd, clrType);
      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;
    }
  }

  return hashSet;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return hashSet;
}

JNIEXPORT jobject JNICALL Java_org_tzi_use_monitor_adapter_clr_CLRAdapter_getCLRSubClasses
  (JNIEnv* env, jobject adapter, jobject clrType)
{
  jthrowable ex = NULL;
  jobject hashSet = env->NewGlobalRef(NULL);
  CLRType* pClrType = NULL;

  // create java HashSet to return
  jclass setClass = env->FindClass("java/util/HashSet");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setConstructor = env->GetMethodID(setClass, "<init>", "()V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  hashSet = env->NewObject(setClass, setConstructor);

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID setAdd = env->GetMethodID(setClass, "add", "(Ljava/lang/Object;)Z");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  // get type information
  pClrType = JNIHelper::GetCLRType(env, clrType, typeInfo);
  if(!pClrType)
    return hashSet;

  // create type of subclasses if available
  jclass typeClass = env->FindClass("org/tzi/use/plugins/monitor/vm/mm/clr/CLRType");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  jmethodID constructorId = env->GetMethodID(typeClass, "<init>", "(Lorg/tzi/use/monitor/adapter/clr/CLRAdapter;JLjava/lang/String;)V");

  if((ex = env->ExceptionOccurred()) != NULL)
    goto exception_handler;

  for(std::vector<CLRType*>::const_iterator subType = pClrType->subClasses.begin(); subType != pClrType->subClasses.end(); ++subType) 
  {
    jlong typeId = (*subType)->typeDefToken;
    CStringA str((*subType)->name);
    jstring name = env->NewStringUTF((const char*)str);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;

    jobject clrType = env->NewObject(typeClass, constructorId, adapter, typeId, name);

    if((ex = env->ExceptionOccurred()) != NULL)
      goto exception_handler;
    else
    {
      env->CallBooleanMethod(hashSet, setAdd, clrType);
      if((ex = env->ExceptionOccurred()) != NULL)
        goto exception_handler;
    }
  }

  return hashSet;

exception_handler:
  env->ExceptionDescribe();
  env->ExceptionClear();
  return hashSet;
}
