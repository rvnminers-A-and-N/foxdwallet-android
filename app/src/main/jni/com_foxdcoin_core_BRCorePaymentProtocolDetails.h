/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_foxdwallet_core_BRCorePaymentProtocolDetails */

#ifndef _Included_com_foxdwallet_core_BRCorePaymentProtocolDetails
#define _Included_com_foxdwallet_core_BRCorePaymentProtocolDetails
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_foxdwallet_core_BRCorePaymentProtocolDetails
 * Method:    createPaymentProtocolDetails
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_foxdwallet_core_BRCorePaymentProtocolDetails_createPaymentProtocolDetails
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     com_foxdwallet_core_BRCorePaymentProtocolDetails
 * Method:    serialize
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_foxdwallet_core_BRCorePaymentProtocolDetails_serialize
  (JNIEnv *, jobject);

/*
 * Class:     com_foxdwallet_core_BRCorePaymentProtocolDetails
 * Method:    disposeNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_foxdwallet_core_BRCorePaymentProtocolDetails_disposeNative
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
