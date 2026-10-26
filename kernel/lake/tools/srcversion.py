#!/usr/bin/env python3
"""srcversion.py — recompute modpost's srcversion without building.

usage: srcversion.py [-I<dir> ...] <obj1.c> [<obj2.c> ...]
       objects in the module's .mod order (the Makefile <mod>-y order).

Mirrors kernel/common/scripts/mod/sumversion.c: for each object, the source
file plus the headers in the *same directory* it pulls in, in first-include
order; whitespace, backslash-newline and /* */ comments dropped; strings kept
whole. sumversion's MD4 loads data words with ntohl and prints htonl(state),
so it is not textbook MD4.

shortcut: include order is a textual DFS that ignores #if; a conditional
include can change the result. A match proves identical sources; a mismatch
on a file with same-dir headers or several objects does not prove a
difference (single file, no same-dir header: it does). Upgrade to the real
.o.cmd deps list when a build tree is available.
"""
import os
import re
import struct
import sys

INC = re.compile(rb'^\s*#\s*include\s*([<"])([^>"]+)[>"]', re.M)
C_SPACE = b' \t\n\v\f\r'


def _rol(v, s):
    v &= 0xffffffff
    return ((v << s) | (v >> (32 - s))) & 0xffffffff


def md4_modpost(data: bytes) -> str:
    """sumversion.c md4: big-endian data words, native length words."""
    bitlen = len(data) * 8
    data += b'\x80' + b'\x00' * ((55 - len(data)) % 64)
    blocks = [list(struct.unpack('>16I', data[o:o + 64]))
              for o in range(0, len(data) - 56, 64)]
    blocks.append(list(struct.unpack('>14I', data[-56:]))
                  + [bitlen & 0xffffffff, bitlen >> 32])
    h = [0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476]
    rounds = (
        (lambda x, y, z: (x & y) | (~x & z), 0,
         range(16), (3, 7, 11, 19)),
        (lambda x, y, z: (x & y) | (x & z) | (y & z), 0x5a827999,
         (0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15), (3, 5, 9, 13)),
        (lambda x, y, z: x ^ y ^ z, 0x6ed9eba1,
         (0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15), (3, 9, 11, 15)),
    )
    for X in blocks:
        s = list(h)  # a, b, c, d
        for fn, k, order, shifts in rounds:
            for j, i in enumerate(order):
                a, b, c, d = (s[(-j) % 4], s[(1 - j) % 4],
                              s[(2 - j) % 4], s[(3 - j) % 4])
                s[(-j) % 4] = _rol(a + fn(b, c, d) + X[i] + k, shifts[j % 4])
        h = [(x + y) & 0xffffffff for x, y in zip(h, s)]
    return struct.pack('<4I', *h).hex().upper()


def strip(buf: bytes) -> bytes:
    """parse_file(): the byte stream sumversion feeds to md4."""
    out = bytearray()
    i, n = 0, len(buf)
    while i < n:
        c = buf[i]
        if c == 0x5c and i + 1 < n and buf[i + 1] == 0x0a:
            i += 2
        elif c in C_SPACE:
            i += 1
        elif c == 0x22:
            j = i + 1
            while j < n and not (buf[j] == 0x22 and buf[j - 1] != 0x5c):
                j += 1
            out += buf[i:j + 1]
            i = j + 1
        elif c == 0x2f and i + 1 < n and buf[i + 1] == 0x2a:
            j = i + 2
            while j < n and not (buf[j - 1] == 0x2a and buf[j] == 0x2f):
                j += 1
            i = j + 1
        else:
            out.append(c)
            i += 1
    return bytes(out)


def deps(src, idirs, seen):
    """Headers reachable by #include, in first-open order (fixdep order)."""
    with open(src, 'rb') as fh:
        text = fh.read()
    for kind, name in INC.findall(text):
        name = name.decode()
        cands = ([os.path.dirname(src)] if kind == b'"' else []) + idirs
        for d in cands:
            p = os.path.normpath(os.path.join(d, name))
            if os.path.isfile(p):
                if p not in seen:
                    seen.append(p)
                    deps(p, idirs, seen)
                break


def srcversion(objs, idirs=()):
    acc = bytearray()
    for src in objs:
        src = os.path.normpath(src)
        d = os.path.dirname(src)
        seen = []
        deps(src, [d] + list(idirs), seen)
        for p in [src] + [p for p in seen if os.path.dirname(p) == d]:
            with open(p, 'rb') as fh:
                acc += strip(fh.read())
    return md4_modpost(bytes(acc))[:23]


if __name__ == '__main__':
    args = sys.argv[1:]
    objs = [a for a in args if not a.startswith('-I')]
    if not objs:
        sys.exit(__doc__)
    print(srcversion(objs, [a[2:] for a in args if a.startswith('-I')]))
