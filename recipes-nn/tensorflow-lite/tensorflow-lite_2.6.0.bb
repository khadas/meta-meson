DESCRIPTION = "TensorFlow’s lightweight solution for mobile and embedded devices"
AUTHOR = "Google Inc. and Yuan Tang"
HOMEPAGE = "https://www.tensorflow.org/lite"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7e17cca1ef4230861fb7868e96c387e"

SHA256SUM_EIGEN = "2f25d7d0279c57ce7c533bc71ba78af9c24a0a0aac4102bfeb28c2b5737499d1"
SHA256SUM_ABSL = "35f22ef5cb286f09954b7cc4c85b5a3f6221c9d4df6b8c4a1e9d399555b366ee"
SHA256SUM_GEMMLOWP = "43146e6f56cb5218a8caaab6b5d1601a083f1f31c06ff474a4378a7d35be9cfb"
SHA256SUM_RUY = "fa9a0b9041095817bc3533f7b125c3b4044570c0b3ee6c436d2d29dae001c06b"
SHA256SUM_NEON2SSE = "213733991310b904b11b053ac224fee2d4e0179e46b52fe7f8735b8831e04dcc"
SHA256SUM_FARMHASH = "6560547c63e4af82b0f202cb710ceabb3f21347a4b996db565a411da5b17aba0"
SHA256SUM_FLATBUFFER = "62f2223fb9181d1d6338451375628975775f7522185266cd5296571ac152bc45"
SHA256SUM_FP16 = "e66e65515fa09927b348d3d584c68be4215cfe664100d01c9dbc7655a5716d70"
SHA256SUM_CLOG = "3f2dc1970f397a0e59db72f9fca6ff144b216895c1d606f6c94a507c1e53a025"
SHA256SUM_CPUINFO = "2a160c527d3c58085ce260f34f9e2b161adc009b34186a2baf24e74376e89e6d"
SHA256SUM_FXDIV = "ab7dfb08829bee33dca38405d647868fb214ac685e379ec7ef2bebcd234cd44d"
SHA256SUM_GBMARK = "bdefa4b03c32d1a27bd50e37ca466d8127c1688d834800c38f3c587a396188ee"
SHA256SUM_GTEST = "fcfac631041fce253eba4fc014c28fd620e33e3758f64f8ed5487cc3e1840e3d"
SHA256SUM_PTPOOL = "8461f6540ae9f777ce20d1c0d1d249e5e61c438744fb390c0c6f91940aa69ea3"
SHA256SUM_FFT2D = "5f4dabc2ae21e1f537425d58a49cdca1c49ea11db0d6271e2a4b27e9697548eb"
SHA256SUM_XNNPACK = "e1fee5a16e4a06d3bd77ab33cf87b1c6d826715906248a308ab790486198d3c9"
SHA256SUM_PSIMD = "dc615342bcbe51ca885323e51b68b90ed9bb9fa7df0f4419dbfa0297d5e837b7"

DEPENDS = "zlib"
SRC_URI = " \
    git://github.com/tensorflow/tensorflow;destsuffix=git/;branch=r2.6;rev=919f693420e35d00c8d0a42100837ae3718f7927;protocol=https \
    https://gitlab.com/libeigen/eigen/-/archive/7b35638ddb99a0298c5d3450de506a8e8e0203d3/eigen-7b35638ddb99a0298c5d3450de506a8e8e0203d3.tar.gz;sha256sum=${SHA256SUM_EIGEN} \
    https://github.com/abseil/abseil-cpp/archive/997aaf3a28308eba1b9156aa35ab7bca9688e9f6.tar.gz;sha256sum=${SHA256SUM_ABSL} \
    https://github.com/google/gemmlowp/archive/fda83bdc38b118cc6b56753bd540caa49e570745.zip;sha256sum=${SHA256SUM_GEMMLOWP} \
    https://github.com/google/ruy/archive/e6c1b8dc8a8b00ee74e7268aac8b18d7260ab1ce.zip;sha256sum=${SHA256SUM_RUY} \
    https://github.com/intel/ARM_NEON_2_x86_SSE/archive/1200fe90bb174a6224a525ee60148671a786a71f.tar.gz;sha256sum=${SHA256SUM_NEON2SSE} \
    https://github.com/google/farmhash/archive/816a4ae622e964763ca0862d9dbd19324a1eaf45.tar.gz;sha256sum=${SHA256SUM_FARMHASH} \
    https://github.com/google/flatbuffers/archive/v1.12.0.tar.gz;sha256sum=${SHA256SUM_FLATBUFFER} \
    https://github.com/Maratyszcza/FP16/archive/0a92994d729ff76a58f692d3028ca1b64b145d91.zip;sha256sum=${SHA256SUM_FP16} \
    https://github.com/pytorch/cpuinfo/archive/d5e37adf1406cf899d7d9ec1d317c47506ccb970.tar.gz;sha256sum=${SHA256SUM_CLOG} \
    https://github.com/pytorch/cpuinfo/archive/5916273f79a21551890fd3d56fc5375a78d1598d.zip;sha256sum=${SHA256SUM_CPUINFO} \
    https://github.com/Maratyszcza/FXdiv/archive/b408327ac2a15ec3e43352421954f5b1967701d1.zip;sha256sum=${SHA256SUM_FXDIV} \
    https://github.com/google/benchmark/archive/v1.5.3.zip;sha256sum=${SHA256SUM_GBMARK} \
    https://github.com/google/googletest/archive/5a509dbd2e5a6c694116e329c5a20dc190653724.zip;sha256sum=${SHA256SUM_GTEST} \
    https://github.com/Maratyszcza/pthreadpool/archive/545ebe9f225aec6dca49109516fac02e973a3de2.zip;sha256sum=${SHA256SUM_PTPOOL} \
    https://storage.googleapis.com/mirror.tensorflow.org/github.com/petewarden/OouraFFT/archive/v1.0.tar.gz;sha256sum=${SHA256SUM_FFT2D} \
    https://github.com/google/XNNPACK/archive/476eb84d6a8e6f8249d5584d30759c6fbdbf791d.zip;sha256sum=${SHA256SUM_XNNPACK} \
    https://github.com/Maratyszcza/psimd/archive/072586a71b55b7f8c584153d223e95687148a900.zip;sha256sum=${SHA256SUM_PSIMD} \
    file://fix-to-cmake.patch \
    file://tensorflow2-lite.pc.in \
    file://tensorflow-lite.pc.in \
"

inherit cmake

S = "${WORKDIR}/git"
TFLT_LOCAL_DEPEND_CACHE = "${S}/tensorflow/lite/tfdependce_local_cache"

do_cp_downloaded_build_deps() {
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/eigen   #not same
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/abseil-cpp
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/gemmlowp
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/ruy
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/neon2sse
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/farmhash
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/flatbuffers
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/FP16-source
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/fft2d
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/googletest
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/pthreadpool-source
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/cpuinfo-source
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/FXdiv-source
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/xnnpack
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/clog-source
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/clog/clog-build
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/benchmark
     mkdir -p ${TFLT_LOCAL_DEPEND_CACHE}/psimd-source

     cp -rf ${WORKDIR}/eigen-7b35638ddb99a0298c5d3450de506a8e8e0203d3/*               ${TFLT_LOCAL_DEPEND_CACHE}/eigen/
     cp -rf ${WORKDIR}/abseil-cpp-997aaf3a28308eba1b9156aa35ab7bca9688e9f6/*          ${TFLT_LOCAL_DEPEND_CACHE}/abseil-cpp/
     cp -rf ${WORKDIR}/gemmlowp-fda83bdc38b118cc6b56753bd540caa49e570745/*            ${TFLT_LOCAL_DEPEND_CACHE}/gemmlowp/
     cp -rf ${WORKDIR}/ruy-e6c1b8dc8a8b00ee74e7268aac8b18d7260ab1ce/*                 ${TFLT_LOCAL_DEPEND_CACHE}/ruy/
     cp -rf ${WORKDIR}/cpuinfo-5916273f79a21551890fd3d56fc5375a78d1598d/*             ${TFLT_LOCAL_DEPEND_CACHE}/ruy/third_party/cpuinfo/
     cp -rf ${WORKDIR}/googletest-5a509dbd2e5a6c694116e329c5a20dc190653724/*          ${TFLT_LOCAL_DEPEND_CACHE}/ruy/third_party/googletest/
     cp -rf ${WORKDIR}/ARM_NEON_2_x86_SSE-1200fe90bb174a6224a525ee60148671a786a71f/*  ${TFLT_LOCAL_DEPEND_CACHE}/neon2sse/
     cp -rf ${WORKDIR}/farmhash-816a4ae622e964763ca0862d9dbd19324a1eaf45/*            ${TFLT_LOCAL_DEPEND_CACHE}/farmhash/
     cp -rf ${WORKDIR}/flatbuffers-1.12.0/*                                           ${TFLT_LOCAL_DEPEND_CACHE}/flatbuffers/
     cp -rf ${WORKDIR}/FP16-0a92994d729ff76a58f692d3028ca1b64b145d91/*                ${TFLT_LOCAL_DEPEND_CACHE}/FP16-source/
     cp -rf ${WORKDIR}/OouraFFT-1.0/*                                                 ${TFLT_LOCAL_DEPEND_CACHE}/fft2d/
     cp -rf ${WORKDIR}/googletest-5a509dbd2e5a6c694116e329c5a20dc190653724/*          ${TFLT_LOCAL_DEPEND_CACHE}/googletest/
     cp -rf ${WORKDIR}/pthreadpool-545ebe9f225aec6dca49109516fac02e973a3de2/*         ${TFLT_LOCAL_DEPEND_CACHE}/pthreadpool-source/
     cp -rf ${WORKDIR}/cpuinfo-5916273f79a21551890fd3d56fc5375a78d1598d/*             ${TFLT_LOCAL_DEPEND_CACHE}/cpuinfo-source/
     cp -rf ${WORKDIR}/FXdiv-b408327ac2a15ec3e43352421954f5b1967701d1/*               ${TFLT_LOCAL_DEPEND_CACHE}/FXdiv-source/
     cp -rf ${WORKDIR}/XNNPACK-476eb84d6a8e6f8249d5584d30759c6fbdbf791d/*             ${TFLT_LOCAL_DEPEND_CACHE}/xnnpack/
     cp -rf ${WORKDIR}/cpuinfo-d5e37adf1406cf899d7d9ec1d317c47506ccb970/*             ${TFLT_LOCAL_DEPEND_CACHE}/clog-source/
     cp -rf ${WORKDIR}/benchmark-1.5.3/*                                              ${TFLT_LOCAL_DEPEND_CACHE}/benchmark/
     cp -rf ${WORKDIR}/psimd-072586a71b55b7f8c584153d223e95687148a900/*               ${TFLT_LOCAL_DEPEND_CACHE}/psimd-source/
}
addtask do_cp_downloaded_build_deps after do_unpack before do_patch

# -DTFLITE_BUILD_SHARED_LIB=on

EXTRA_OECMAKE = "\
    -DTFLITE_ENABLE_XNNPACK=on \
    -DTFLITE_ENABLE_RUY=on \
    -DTFLITE_ENABLE_GPU=off \
    ${S}/tensorflow/lite/ \
"

EXTRA_OECMAKE += "\
    -DFETCHCONTENT_SOURCE_DIR_ABSEIL-CPP=${TFLT_LOCAL_DEPEND_CACHE}/abseil-cpp \
    -DFETCHCONTENT_SOURCE_DIR_RUY=${TFLT_LOCAL_DEPEND_CACHE}/ruy \
    -DFETCHCONTENT_SOURCE_DIR_GEMMLOWP=${TFLT_LOCAL_DEPEND_CACHE}/gemmlowp \
    -DFETCHCONTENT_SOURCE_DIR_FARMHASH=${TFLT_LOCAL_DEPEND_CACHE}/farmhash \
    -DFETCHCONTENT_SOURCE_DIR_FFT2D=${TFLT_LOCAL_DEPEND_CACHE}/fft2d \
    -DFETCHCONTENT_SOURCE_DIR_EIGEN=${TFLT_LOCAL_DEPEND_CACHE}/eigen \
    -DFETCHCONTENT_SOURCE_DIR_XNNPACK=${TFLT_LOCAL_DEPEND_CACHE}/xnnpack \
    -DFETCHCONTENT_SOURCE_DIR_NEON2SSE=${TFLT_LOCAL_DEPEND_CACHE}/neon2sse \
    -DPTHREADPOOL_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/pthreadpool-source \
    -DFLATBUFFERS_PREFIX=${TFLT_LOCAL_DEPEND_CACHE}/flatbuffers-flatc \
    -DPSIMD_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/psimd-source \
    -DFXDIV_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/FXdiv-source \
    -DFP16_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/FP16-source \
    -DCLOG_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/clog-source \
    -DCPUINFO_SOURCE_DIR=${TFLT_LOCAL_DEPEND_CACHE}/cpuinfo-source \
"

do_install() {
    # install libraries
    install -d ${D}${libdir}
    install -m 0644 ${B}/libtensorflow-lite-bundled.a ${D}${libdir}/libtensorflow2-lite.a
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/tensorflow2-lite.pc.in ${D}${libdir}/pkgconfig/tensorflow2-lite.pc

    # install header files
    install -d ${D}${includedir}/tensorflow/lite
    cd ${S}/tensorflow/lite
    cp --parents \
        $(find . -name "*.h*") \
        ${D}${includedir}/tensorflow/lite

    # install version.h from core
    install -d ${D}${includedir}/tensorflow/core/public
    cp ${S}/tensorflow/core/public/version.h ${D}${includedir}/tensorflow/core/public

    sed -i 's:@version@:${PV}:g
        s:@libdir@:${libdir}:g
        s:@includedir@:${includedir}:g' ${D}${libdir}/pkgconfig/tensorflow2-lite.pc

    # flatbuffers
    install -d  ${D}${includedir}/flatbuffers
    install -m 0644 ${B}/flatbuffers/include/flatbuffers/*.h ${D}${includedir}/flatbuffers/
}

ALLOW_EMPTY:${PN} = "1"
