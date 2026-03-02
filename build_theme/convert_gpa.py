#!/usr/bin/env python3
"""
Convert Gpick .gpa palette files to .gpl (GIMP) or .txt format.

Usage:
  python3 gpa_convert.py input.gpa output.gpl
  python3 gpa_convert.py input.gpa output.txt
"""

import struct
import sys
import os


def parse_gpa(path):
    with open(path, 'rb') as f:
        data = f.read()

    if not data.startswith(b'GPA version '):
        raise ValueError("Not a valid GPA file")

    colors = []
    pos = 0
    marker = b'\x05\x00\x00\x00color\x10\x00\x00\x00'

    while pos < len(data):
        idx = data.find(marker, pos)
        if idx == -1:
            break

        float_start = idx + len(marker)
        r, g, b, a = struct.unpack_from('<ffff', data, float_start)
        ri, gi, bi = int(round(r * 255)), int(round(g * 255)), int(round(b * 255))

        # Look for a name field shortly after the color data
        name = ''
        name_idx = data.find(b'\x04\x00\x00\x00name', float_start)
        if name_idx != -1 and name_idx < float_start + 50:
            name_str_pos = name_idx + 8
            length = struct.unpack_from('<I', data, name_str_pos)[0]
            name = data[name_str_pos + 4:name_str_pos + 4 + length].decode('utf-8', errors='replace')

        colors.append((ri, gi, bi, name))
        pos = float_start + 16

    return colors


def write_gpl(colors, path, palette_name=None):
    if palette_name is None:
        palette_name = os.path.splitext(os.path.basename(path))[0]
    with open(path, 'w') as f:
        f.write("GIMP Palette\n")
        f.write(f"Name: {palette_name}\n")
        f.write("#\n")
        for r, g, b, name in colors:
            f.write(f"{r:3d} {g:3d} {b:3d}    {name}\n")


def write_txt(colors, path):
    with open(path, 'w') as f:
        for r, g, b, name in colors:
            hex_color = f"#{r:02X}{g:02X}{b:02X}"
            line = f"{hex_color}  {name}" if name else hex_color
            f.write(line + "\n")


def main():
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} input.gpa output.[gpl|txt]")
        sys.exit(1)

    in_path, out_path = sys.argv[1], sys.argv[2]
    ext = os.path.splitext(out_path)[1].lower()

    if ext not in ('.gpl', '.txt'):
        print(f"Unsupported output format '{ext}'. Use .gpl or .txt")
        sys.exit(1)

    colors = parse_gpa(in_path)
    print(f"Parsed {len(colors)} color(s) from {in_path}")

    if ext == '.gpl':
        write_gpl(colors, out_path)
    else:
        write_txt(colors, out_path)

    print(f"Written to {out_path}")


if __name__ == '__main__':
    main()
