cmake_minimum_required(VERSION 3.4.1)

option(OCR_LITE_OPENMP "OcrLite Enable OpenMP" ON)

# import ncnn library
add_library(ncnn STATIC IMPORTED)
set_target_properties(ncnn PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../staticLibs/${ANDROID_ABI}/libncnn.a)

## opencv 库
set(OpenCV_DIR "${CMAKE_SOURCE_DIR}/../../sdk/native/jni")
find_package(OpenCV REQUIRED)
if (OpenCV_FOUND)
    message(STATUS "OpenCV_LIBS: ${OpenCV_LIBS}")
    message(STATUS "OpenCV_INCLUDE_DIRS: ${OpenCV_INCLUDE_DIRS}")
else ()
    message(FATAL_ERROR "opencv Not Found!")
endif (OpenCV_FOUND)

# openmp
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -fopenmp")

if (DEFINED ANDROID_NDK_MAJOR AND ${ANDROID_NDK_MAJOR} GREATER 20)
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -static-openmp")
endif ()

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fomit-frame-pointer -fstrict-aliasing -ffast-math")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fomit-frame-pointer -fstrict-aliasing -ffast-math")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -fvisibility-inlines-hidden")

# disable rtti and exceptions
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti -fno-exceptions")

include_directories(include)
file(GLOB OCR_SRC src/*.cpp)
set(OCR_COMPILE_CODE ${OCR_SRC})

add_library(OcrLite SHARED ${OCR_COMPILE_CODE})

if (OCR_LITE_OPENMP)
    target_compile_definitions(OcrLite PRIVATE __OPENMP__)
endif ()

find_library( # Sets the name of the path variable.
        log-lib
        log)

find_library(
        android-lib
        android
)

target_link_libraries(
        OcrLite
        ncnn
        ${OpenCV_LIBS}
        android
        z
        ${log-lib}
        ${android-lib}
        jnigraphics)
