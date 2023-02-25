# Features:
Supports commands:
-----------include source files to be compiled-----------------------------
.include "<filename>"
 include "<filename>"
-----------define data: number, characters, strings as byte array----------
.def <variable> <value>
 def <variable> <value>
.define <variable> <value>
 define <variable> <value>
.db <value0>,<value0>...<valueN>
 db <value0>,<value0>...<valueN>
.defb <value0>,<value0>...<valueN>
 defb <value0>,<value0>...<valueN>

Supports number formats:
00-9  - octal number (C-style)
0x0-F - hexadecimal (C-style)
#0-F  - hexadecimal
$0-F  - hexadecimal (ZX-style)
0-Fh  - hexdecimal (Old-style)
0b0-1 - binary (Java-style)
0-1b  - binary (Old-style)
0-9   - decimal
