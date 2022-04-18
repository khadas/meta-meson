FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append = "file://0001-Always-return-true-for-wait.patch \
    file://0002-fix-ls-segmentation-fault-issue.patch \
"
