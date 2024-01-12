
2024.1.12 modification record:
1. Refer to the approach of RDK Westeros to modify the weston bb file approach, so that all patches in the weston folder can be automatically loaded
2. Organize some similar function modifications and merge them into one patch.
3. Put optional patch in file weston/optional

details:
1)
0003-10.0.0-punch-video-hole-for-meson-dri-video-dmabuff-on-vide.patch
0004-8.0.0-don-t-touch-the-occluded-region-when-glrenderer.patch
0006-10.0.0-video-plane-src-axis-should-match-the-real.patch
Merge the above three patches into one:0003-10.0.0-punch-video-hole-for-meson-dri-video-dmabuff-on-vide.patch

2)
0020-10.0.0-define_EGL_DRM_RENDER_NODE_FILE_EXT.patch
0021-10.0.0-MOD_INVALID-to-MOD_LINEAR-for-low-dmabuf-version.patch
0022-10.0.0-revert_gl-renderer_Dont_require_buffer_age_when_using_partial_update.patch
Merge the above three patches into one:0020-10.0.0-add-define-for-egl.patch

3)Merge the modification for function "debug_scene_view_print" in "0024-10.0.0-modify-keyboard-focus-change-flow.patch" and "0025-10.0.0-play-video-in-video-layer.patch"
in "0025-10.0.0-play-video-in-video-layer.patch"

4）Temporarily unused patch files are placed in path:meta-meson\recipes-graphics\wayland\weston_backup.

