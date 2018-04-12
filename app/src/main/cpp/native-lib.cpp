#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_whxcs_mycrop_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject jobject1,jstring jstring1/* this */) {
    std::string test = env->GetStringUTFChars(jstring1,0);
    std::string hello = "Hello from C++"+test;
    return env->NewStringUTF(hello.c_str());
}
