#include <jni.h>

#include <android/log.h>
#include <dirent.h>
#include <sys/prctl.h>
#include <unistd.h>

#include <cerrno>
#include <climits>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>

#include "uapi/supercall.h"
#include "app_profile_fill.h"

// Driver fd name and ioctl numbers are the KernelSU engine contract.
// This file does not draw UI.

static int driver_fd = -1;

static int scan_driver_fd() {
    DIR *dir = opendir("/proc/self/fd");
    if (dir == nullptr) {
        return -1;
    }
    int found = -1;
    struct dirent *de;
    while ((de = readdir(dir)) != nullptr) {
        if (de->d_name[0] == '.') {
            continue;
        }
        char *end = nullptr;
        long value = strtol(de->d_name, &end, 10);
        if (end == de->d_name || *end != '\0' || value < 0 || value > INT_MAX) {
            continue;
        }
        char path[64];
        snprintf(path, sizeof(path), "/proc/self/fd/%s", de->d_name);
        char target[PATH_MAX];
        ssize_t n = readlink(path, target, sizeof(target) - 1);
        if (n < 0) {
            continue;
        }
        target[n] = '\0';
        const char *base = strrchr(target, '/');
        base = base != nullptr ? base + 1 : target;
        if (strstr(base, "[ksu_driver]") != nullptr) {
            found = static_cast<int>(value);
            break;
        }
    }
    closedir(dir);
    return found;
}

template <typename T>
static int ksuctl(unsigned long op, T *arg) {
    if (driver_fd < 0) {
        driver_fd = scan_driver_fd();
    }
    return ioctl(driver_fd, op, arg);
}

static ksu_get_info_cmd g_info{};

static ksu_get_info_cmd read_info() {
    if (g_info.version == 0) {
        if (ksuctl(KSU_IOCTL_GET_INFO, &g_info) < 0) {
            ksuctl(KSU_IOCTL_GET_INFO_LEGACY, &g_info);
            g_info.uapi_version = 0;
        }
    }
    return g_info;
}

static bool get_allow_list(ksu_new_get_allow_list_cmd *cmd) {
    return ksuctl(KSU_IOCTL_NEW_GET_ALLOW_LIST, cmd) == 0;
}

static int get_app_profile(app_profile *profile) {
    ksu_get_app_profile_cmd cmd{};
    cmd.profile = *profile;
    int ret = ksuctl(KSU_IOCTL_GET_APP_PROFILE, &cmd);
    *profile = cmd.profile;
    return ret;
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_superroot_core_EngineNatives_kernelVersion(JNIEnv *, jobject) {
    return static_cast<jint>(read_info().version);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_superroot_core_EngineNatives_kernelUapi(JNIEnv *, jobject) {
    return static_cast<jint>(read_info().uapi_version);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_superroot_core_EngineNatives_managerUapi(JNIEnv *, jobject) {
    return static_cast<jint>(KERNEL_SU_UAPI_VERSION);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_superroot_core_EngineNatives_kernelFlags(JNIEnv *, jobject) {
    return static_cast<jint>(read_info().flags);
}

extern "C" JNIEXPORT jintArray JNICALL
Java_dev_superroot_core_EngineNatives_allowListUids(JNIEnv *env, jobject) {
    ksu_new_get_allow_list_cmd probe{};
    probe.count = 0;
    if (!get_allow_list(&probe)) {
        return nullptr;
    }
    const uint16_t total = probe.total_count;
    if (total == 0) {
        return env->NewIntArray(0);
    }
    const size_t bytes = sizeof(ksu_new_get_allow_list_cmd) + sizeof(uint32_t) * total;
    auto *buf = static_cast<ksu_new_get_allow_list_cmd *>(calloc(1, bytes));
    if (buf == nullptr) {
        return nullptr;
    }
    buf->count = total;
    jintArray out = nullptr;
    if (get_allow_list(buf)) {
        const uint16_t n = buf->count;
        out = env->NewIntArray(n);
        if (out != nullptr && n > 0) {
            auto *uids = reinterpret_cast<uint32_t *>(
                reinterpret_cast<unsigned char *>(buf) + offsetof(ksu_new_get_allow_list_cmd, uids));
            auto *tmp = static_cast<jint *>(malloc(sizeof(jint) * n));
            if (tmp == nullptr) {
                env->DeleteLocalRef(out);
                out = nullptr;
            } else {
                for (uint16_t i = 0; i < n; ++i) {
                    tmp[i] = static_cast<jint>(uids[i]);
                }
                env->SetIntArrayRegion(out, 0, n, tmp);
                free(tmp);
            }
        }
    }
    free(buf);
    return out;
}

static jstring bounded_string(JNIEnv *env, const char *src, size_t cap) {
    char tmp[KSU_MAX_PACKAGE_NAME + 1];
    if (cap > KSU_MAX_PACKAGE_NAME) {
        cap = KSU_MAX_PACKAGE_NAME;
    }
    memcpy(tmp, src, cap);
    tmp[cap] = '\0';
    return env->NewStringUTF(tmp);
}

extern "C" JNIEXPORT jobject JNICALL
Java_dev_superroot_core_EngineNatives_readProfile(JNIEnv *env, jobject, jint uid) {
    app_profile profile{};
    profile.version = KSU_APP_PROFILE_VER;
    profile.curr_uid = uid;
    profile.key[0] = '\0';
    if (get_app_profile(&profile) != 0) {
        return nullptr;
    }

    jstring context = env->NewStringUTF("");
    jint profile_uid = 0;
    jint profile_gid = 0;
    jlong flags = 0;
    if (profile.allow_su) {
        if (context != nullptr) {
            env->DeleteLocalRef(context);
        }
        context = bounded_string(env, profile.rp_config.profile.selinux_domain, KSU_SELINUX_DOMAIN);
        profile_uid = profile.rp_config.profile.uid;
        profile_gid = profile.rp_config.profile.gid;
        flags = static_cast<jlong>(profile.rp_config.profile.flags);
    }
    jstring name = bounded_string(env, profile.key, KSU_MAX_PACKAGE_NAME);
    jclass cls = env->FindClass("dev/superroot/core/EngineProfile");
    if (cls == nullptr || context == nullptr || name == nullptr) {
        return nullptr;
    }
    jmethodID ctor = env->GetMethodID(
        cls, "<init>", "(ZIILjava/lang/String;JLjava/lang/String;)V");
    if (ctor == nullptr) {
        return nullptr;
    }
    return env->NewObject(
        cls,
        ctor,
        static_cast<jboolean>(profile.allow_su),
        profile_uid,
        profile_gid,
        context,
        flags,
        name);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_superroot_core_EngineNatives_setAppProfile(
    JNIEnv *env, jobject, jint uid, jstring packageName, jboolean allowSu) {
    if (packageName == nullptr) {
        return -EINVAL;
    }

    const char *key = env->GetStringUTFChars(packageName, nullptr);
    if (key == nullptr) {
        return -ENOMEM;
    }

    app_profile profile{};
    const bool valid = fill_app_profile(
        profile, static_cast<int32_t>(uid), key, allowSu == JNI_TRUE);
    env->ReleaseStringUTFChars(packageName, key);
    if (!valid) {
        return -EINVAL;
    }

    ksu_set_app_profile_cmd cmd{};
    cmd.profile = profile;
    const int ret = ksuctl(KSU_IOCTL_SET_APP_PROFILE, &cmd);
    return ioctl_status(ret);
}
