#include <cassert>
#include <cerrno>
#include <cstdint>
#include <iostream>
#include <string>
#include "app_profile_fill.h"

static void validUid() {
    app_profile profile{};
    assert(fill_app_profile(profile, 10123, "dev.example.app", false));
    assert(profile.version == KSU_APP_PROFILE_VER);
    assert(profile.curr_uid == 10123);
    assert(profile.allow_su == false);
    assert(std::string(profile.key) == "dev.example.app");
}

static void negativeUidRejected() {
    app_profile profile{};
    assert(!fill_app_profile(profile, -1, "dev.example.app", false));
}

static void nullKeyRejected() {
    app_profile profile{};
    assert(!fill_app_profile(profile, 10123, nullptr, false));
}

static void emptyKeyRejected() {
    app_profile profile{};
    assert(!fill_app_profile(profile, 10123, "", false));
}

static void key255Accepted() {
    const std::string key(255, 'k');
    app_profile profile{};
    assert(fill_app_profile(profile, 10123, key.c_str(), false));
    assert(profile.key[254] == 'k');
    assert(profile.key[255] == '\0');
}

static void key256Rejected() {
    const std::string key(256, 'k');
    app_profile profile{};
    assert(!fill_app_profile(profile, 10123, key.c_str(), false));
}

static void allowSuUsesMinimalDefaultRootProfile() {
    app_profile profile{};
    assert(fill_app_profile(profile, 10123, "dev.example.app", true));
    assert(profile.allow_su == true);
    assert(profile.rp_config.use_default == true);
    assert(profile.rp_config.profile.groups_count == 0);
    assert(std::string(profile.rp_config.profile.selinux_domain) == "u:r:ksu:s0");
}

static void allowSuFalseMarksRevoke() {
    app_profile profile{};
    assert(fill_app_profile(profile, 10123, "dev.example.app", false));
    assert(profile.allow_su == false);
}

static void ioctlSuccessIsZero() {
    errno = EPERM;
    assert(ioctl_status(0) == 0);
}

static void ioctlFailureReturnsEbadf() {
    errno = EBADF;
    assert(ioctl_status(-1) == -EBADF);
}

static void ioctlFailureReturnsEinval() {
    errno = EINVAL;
    assert(ioctl_status(-1) == -EINVAL);
}

static void ioctlPositiveReturnIsPassedThrough() {
    errno = EBADF;
    assert(ioctl_status(1) == 1);
}

#define RUN_TEST(F) do { F(); std::cout << #F << " PASS" << std::endl; } while (false)

int main() {
    RUN_TEST(validUid);
    RUN_TEST(negativeUidRejected);
    RUN_TEST(nullKeyRejected);
    RUN_TEST(emptyKeyRejected);
    RUN_TEST(key255Accepted);
    RUN_TEST(key256Rejected);
    RUN_TEST(allowSuUsesMinimalDefaultRootProfile);
    RUN_TEST(allowSuFalseMarksRevoke);
    RUN_TEST(ioctlSuccessIsZero);
    RUN_TEST(ioctlFailureReturnsEbadf);
    RUN_TEST(ioctlFailureReturnsEinval);
    RUN_TEST(ioctlPositiveReturnIsPassedThrough);
}
