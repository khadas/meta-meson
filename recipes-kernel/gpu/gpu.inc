#SRC_URI = "git://${AML_GIT_ROOT}/platform/hardware/arm/gpu.git;protocol=${AML_GIT_PROTOCOL};branch=mainline"

#For common patches
SRC_URI_append = " ${@get_patch_list_with_path('${AML_PATCH_PATH}/hardware/aml-4.9/arm/gpu')}"
