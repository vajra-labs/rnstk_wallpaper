#include <jni.h>
#include "WallpaperOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::wallpaper::initialize(vm);
}
