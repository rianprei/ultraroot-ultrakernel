cmake_minimum_required(VERSION 3.22.1)
project(ultraroot)

add_library(ultraroot SHARED engine.cc)
target_include_directories(ultraroot PRIVATE ${CMAKE_SOURCE_DIR})
target_compile_options(ultraroot PRIVATE -fno-exceptions -fno-rtti -ffunction-sections -fdata-sections)
target_link_options(ultraroot PRIVATE -Wl,--gc-sections -Wl,--strip-all)

find_library(log-lib log)
target_link_libraries(ultraroot ${log-lib})
