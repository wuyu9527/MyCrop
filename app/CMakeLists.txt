# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 创建和命名一个图书馆，将其设置为静态或共享，并提供相对路径的源代码，你可以定义多个图书馆，和CMake建立了他们的你。Gradle自动包共享库与你的apk。
add_library( # Sets the name of the library.
             ucrop

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/uCrop.cpp )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

add_library(libpng STATIC IMPORTED)
set_target_properties( libpng
                       PROPERTIES
                       IMPORT_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libpng.a )



target_link_libraries( # Specifies the target library.
                       ucrop
                       android
                       # Links the target library to the log library
                       # included in the NDK.
                       ${libpng}
                       log )