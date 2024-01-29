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
.db <byte expr0>,<expr byte1>...<byte exprN>
 db <string0>,<string1>...<stringN>
.defb <byte expr0>,<byte expr1>...<byte exprN>
 defb <string0>,<string0>...<stringN>
.dw <word expr0>,<word expr1>...<word exprN>
dw <word expr0>,<word expr1>...<word exprN>
.defw <word expr0>,<word expr1>...<word exprN>
defw <word expr0>,<word expr1>...<word exprN>
.ddw <dword expr0>,<dword expr1>...<dword exprN>
ddw <dword expr0>,<dword expr1>...<dword exprN>
.defdw <dword expr0>,<dword expr1>...<dword exprN>
defdw <dword expr0>,<dword expr1>...<dword exprN>
end - force compiler to be stopped

saveWav <text1>...<textN> save compiled data into wave format (for zxspectrum)
.saveWav <text1>...<textN> save compiled data into wave format (for zxspectrum)
saveTap <text1>...<textN> save compiled data into <tap> format (for zxspectrum)
.saveTap <text1>...<textN> save compiled data into <tap> format (for zxspectrum)
saveTzx <text1>...<textN> save compiled data into <tzx> format (for zxspectrum)
.saveTzx <text1>...<textN> save compiled data into <tzx> format (for zxspectrum)

saveWav <text1>...<textN> save compiled data into wave format (for microsha)
.saveWav <text1>...<textN> save compiled data into wave format (for microsha)
saveRkm <text1>...<textN> save compiled data into <rkm> format (for microsha)
.saveRkm <text1>...<textN> save compiled data into <rkm> format (for microsha)

label: .equ <address>
label: equ <address>


Supports number formats:
00-9  - octal number (C-style)
0x0-F - hexadecimal (C-style)
#0-F  - hexadecimal
$0-F  - hexadecimal (ZX-style)
0-Fh  - hexdecimal (Old-style)
0b0-1 - binary (Java-style)
0-1b  - binary (Old-style)
0-9   - decimal
