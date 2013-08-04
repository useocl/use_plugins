/** 
* @file ObjectInfoHelper.h
* This file declares the class ObjectInfoHelper, that manages the heap objects.
* @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
*/

#pragma once

#include <iostream>
#include "../Common/CommonTypes.h"
#include "../Common/DebugBuffer.h"
#include "../Common/CLRType.h"
#include "../Common/Settings.h"
#include "../Common/CLRDebugCore.h"
#include "../Common/CLRObject.h"
#include "../Common/CLRFieldBase.h"
#include "../Common/FieldValueHelper.h"
#include "../Common/TypeInfoHelper.h"

class ObjectInfoHelper
{
public:
  /**  
  * The constructor.
  * @param typeInfo The instance of the TypeInfoHelper.
  */
  ObjectInfoHelper(const TypeInfoHelper& typeInfo);

  /**  
  * The virtual destructor.
  */
  virtual ~ObjectInfoHelper();

  /**  
  * The method sarches the instances of the given type.
  * @param fields The type to find its instances.
  */
  void GetInstances(CLRType* type);

  /**  
  * The method returns the desired CLR heap object.
  * @param address The memory address of the desired object.
  */
  CLRObject* GetCLRObject(const CORDB_ADDRESS address) const;

  /** 
  * The method returns the field value.
  * @param type The type that declares the desired field.
  * @param object The memory address of the object that includes the field.
  * @param field The meta data token of the desired field.
  * @returns The value of the desired field.
  */
  CLRFieldBase* GetField(const CLRType* type, const CORDB_ADDRESS object, const mdFieldDef field);

  /** 
  * The method returns the number of loaded and cached instances.
  * @returns The number of loaded and cached instances.
  */
  unsigned int InstanceCount() const;

  /**
  * The method clares the loaded instances.
  */
  void Detach();

private:
  const TypeInfoHelper& typeInfo;   /**< The instance of the TypeInfoHelper. */
  ObjectMap loadedInstances;        /**< A cache for loded instances. */
  const bool inMemoryInstanceMap;   /**< Should instances be cached? Initialized by settings. */
  const bool cacheAtStartUp;        /**< Should all desired instances be cached at startup? Initialized by settings. */
  bool isCacheInitialized;          /**< Is the cache initialized. */

  /** 
  * The method checks the CLR heap.
  * @returns Is the heap valid and ready to iterate over it.
  */
  bool isHeapValid() const;

  /** 
  * The method checks the CLR heap.
  * @param type The type to find its instances.
  * @param init Should the cache be initialized with instances all desired types? (cacheAtStartUp == true)
  */
  void iterateOverHeap(CLRType* type, bool init = false);

  /** 
  * The method creates a CLRObject for a heap object.
  * @param currentObject The current heap object (from heap iteration).
  * @param type The type to find its instances.
  * @param init Should the cache be initialized with instances all desired types? (cacheAtStartUp == true)
  */
  void createCLRObject(const COR_HEAPOBJECT* currentObject, CLRType* type, bool init = false);

  /** 
  * The method creates a CLRObject for a heap object.
  * @param address The memory address of the desired object.
  * @returns A pointer to the created CLRObject.
  */
  CLRObject* createCLRObject(const CORDB_ADDRESS address) const;

  /** 
  * The method initializes the cache. (cacheAtStartUp == true)
  */
  void initializeCache();
};
