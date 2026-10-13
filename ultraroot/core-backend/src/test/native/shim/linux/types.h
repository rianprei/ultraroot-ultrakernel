#ifndef H8_SHIM_LINUX_TYPES_H
#define H8_SHIM_LINUX_TYPES_H
#include <cstdint>
typedef std::uint8_t __u8;
typedef std::uint16_t __u16;
typedef std::uint32_t __u32;
typedef std::uint64_t __u64;
typedef std::int32_t __s32;
typedef std::int64_t __s64;
typedef __u16 __le16;
typedef __u32 __le32;
typedef __u64 __le64;
typedef __u16 __be16;
typedef __u32 __be32;
typedef __u64 __be64;
typedef __u64 __aligned_u64 __attribute__((aligned(8)));
#endif
