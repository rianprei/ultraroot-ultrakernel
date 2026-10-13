#ifndef ULTRAROOT_APP_PROFILE_FILL_H
#define ULTRAROOT_APP_PROFILE_FILL_H

#include <cerrno>

#include "uapi/app_profile.h"

// Maps the ioctl() return to a status: 0, or -errno on failure.
static inline int ioctl_status(int ret) {
    return ret == 0 ? 0 : (ret < 0 ? -errno : ret);
}

static_assert(sizeof(root_profile) == 248, "root_profile size changed");
static_assert(__builtin_offsetof(root_profile, uid) == 0, "root_profile.uid offset changed");
static_assert(__builtin_offsetof(root_profile, gid) == 4, "root_profile.gid offset changed");
static_assert(__builtin_offsetof(root_profile, groups_count) == 8, "root_profile.groups_count offset changed");
static_assert(__builtin_offsetof(root_profile, groups) == 12, "root_profile.groups offset changed");
static_assert(__builtin_offsetof(root_profile, capabilities) == 144, "root_profile.capabilities offset changed");
static_assert(__builtin_offsetof(root_profile, selinux_domain) == 168, "root_profile.selinux_domain offset changed");
static_assert(__builtin_offsetof(root_profile, namespaces) == 232, "root_profile.namespaces offset changed");
static_assert(__builtin_offsetof(root_profile, flags) == 240, "root_profile.flags offset changed");

static_assert(sizeof(non_root_profile) == 1, "non_root_profile size changed");
static_assert(__builtin_offsetof(non_root_profile, umount_modules) == 0, "non_root_profile.umount_modules offset changed");

static_assert(sizeof(app_profile) == 784, "app_profile size changed");
static_assert(__builtin_offsetof(app_profile, version) == 0, "app_profile.version offset changed");
static_assert(__builtin_offsetof(app_profile, key) == 4, "app_profile.key offset changed");
static_assert(__builtin_offsetof(app_profile, curr_uid) == 260, "app_profile.curr_uid offset changed");
static_assert(__builtin_offsetof(app_profile, allow_su) == 264, "app_profile.allow_su offset changed");
static_assert(__builtin_offsetof(app_profile, rp_config) == 272, "app_profile.rp_config offset changed");
static_assert(__builtin_offsetof(app_profile, rp_config.use_default) == 272, "rp_config.use_default offset changed");
static_assert(__builtin_offsetof(app_profile, rp_config.template_name) == 273, "rp_config.template_name offset changed");
static_assert(__builtin_offsetof(app_profile, rp_config.profile) == 536, "rp_config.profile offset changed");
static_assert(__builtin_offsetof(app_profile, rp_config.profile.selinux_domain) == 704,
              "rp_config.profile.selinux_domain offset changed");
static_assert(__builtin_offsetof(app_profile, nrp_config) == 272, "app_profile.nrp_config offset changed");
static_assert(__builtin_offsetof(app_profile, nrp_config.use_default) == 272, "nrp_config.use_default offset changed");
static_assert(__builtin_offsetof(app_profile, nrp_config.profile.umount_modules) == 273,
              "nrp_config.profile.umount_modules offset changed");
static_assert(sizeof("u:r:ksu:s0") <= KSU_SELINUX_DOMAIN, "default SELinux domain does not fit");

static inline bool fill_app_profile(app_profile &profile, int32_t uid, const char *key, bool allow_su) {
    if (key == nullptr || uid < 0) {
        return false;
    }

    int key_length = 0;
    while (key_length < KSU_MAX_PACKAGE_NAME && key[key_length] != '\0') {
        ++key_length;
    }
    if (key_length == 0 || key_length >= KSU_MAX_PACKAGE_NAME) {
        return false;
    }

    profile = {};
    profile.version = KSU_APP_PROFILE_VER;
    profile.curr_uid = uid;
    profile.allow_su = allow_su;
    for (int i = 0; i < key_length; ++i) {
        profile.key[i] = key[i];
    }

    if (allow_su) {
        // Use KernelSU's default root profile; its validator still requires a non-empty domain.
        profile.rp_config.use_default = true;
        const char default_domain[] = "u:r:ksu:s0";
        for (int i = 0; default_domain[i] != '\0'; ++i) {
            profile.rp_config.profile.selinux_domain[i] = default_domain[i];
        }
    }

    return true;
}

#endif
