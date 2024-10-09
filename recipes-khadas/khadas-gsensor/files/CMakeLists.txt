cmake_minimum_required (VERSION 3.0)
project(gsensor_sample_demo)

set(SRC_LIST ./src/gsensor_sample_demo.c)
add_executable(gsensor_sample_demo ${SRC_LIST})

install(TARGETS gsensor_sample_demo DESTINATION ${CMAKE_INSTALL_PREFIX}/bin)
