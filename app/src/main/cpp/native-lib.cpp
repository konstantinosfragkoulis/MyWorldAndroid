#include <jni.h>
#include <string>
#include "h3/include/h3api.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_konstantinos_myworld_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    H3Index myIdx;
    LatLng g = {27.0, 30.0};
    latLngToCell(&g, 8, &myIdx);
    char myStr[17];
    h3ToString(myIdx, myStr, sizeof(myStr));
    return env->NewStringUTF(myStr);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_konstantinos_myworld_LocationService_latLngToCell(
        JNIEnv* env,
        jobject /* this */,
        jdouble lat,
        jdouble lng,
        jint res) {
    LatLng g = {.lat = degsToRads(lat), .lng = degsToRads(lng)};
    H3Index h;
    latLngToCell(&g, res, &h);
    return static_cast<jlong>(h);
}
