#!/usr/bin/env python3
"""Unit tests for tools/srcversion.py.

Run:  python3 -m unittest discover -s tests -p 'test_*.py'
Expected values are `modinfo -F srcversion` of modules modpost built from this
tree (ULTRA-kernel v6, clang r510928, docs/ultrakernel/LAKE-OSS-AUDITORIA.md).
"""
import os
import sys
import tempfile
import unittest

HERE = os.path.dirname(__file__)
sys.path.insert(0, os.path.join(HERE, '..', 'tools'))
import srcversion as sv

COMMON = os.path.join(HERE, '..', '..', 'common')


class TestSrcversion(unittest.TestCase):
    def test_matches_modpost_on_real_modules(self):
        for rel, want in (('drivers/char/ttyprintk.c', 'DD5AC3162BF38EEBEE12295'),
                          ('mm/zsmalloc.c', '7159363137F692924211B69'),
                          ('drivers/bluetooth/btusb.c', '0CD65B4126A7DB19FB1B4AC')):
            with self.subTest(rel=rel):
                self.assertEqual(sv.srcversion([os.path.join(COMMON, rel)]), want)

    def test_whitespace_and_block_comments_do_not_count(self):
        with tempfile.TemporaryDirectory() as d:
            a, b = os.path.join(d, 'a.c'), os.path.join(d, 'b.c')
            with open(a, 'wb') as fh:
                fh.write(b'int x = 1; /* note */\n// kept\nchar *s = "a  b";\n')
            with open(b, 'wb') as fh:
                fh.write(b'int  x=1;\\\n\n// kept\nchar*s="a  b";')
            self.assertEqual(sv.srcversion([a]), sv.srcversion([b]))
            with open(b, 'wb') as fh:
                fh.write(b'int x=1;// kept\nchar*s="a b";')
            self.assertNotEqual(sv.srcversion([a]), sv.srcversion([b]))

    def test_same_dir_header_counts_other_dir_does_not(self):
        with tempfile.TemporaryDirectory() as d:
            os.mkdir(os.path.join(d, 'inc'))
            c = os.path.join(d, 'm.c')
            with open(c, 'wb') as fh:
                fh.write(b'#include "local.h"\n#include "inc/far.h"\nint m;\n')
            for name, body in (('local.h', b'int l;'), ('inc/far.h', b'int f;')):
                with open(os.path.join(d, name), 'wb') as fh:
                    fh.write(body)
            before = sv.srcversion([c])
            with open(os.path.join(d, 'inc/far.h'), 'wb') as fh:
                fh.write(b'int changed;')
            self.assertEqual(sv.srcversion([c]), before)
            with open(os.path.join(d, 'local.h'), 'wb') as fh:
                fh.write(b'int changed;')
            self.assertNotEqual(sv.srcversion([c]), before)


if __name__ == '__main__':
    unittest.main()
