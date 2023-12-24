org 0x8000
    ld a, 2h
    call 5633
    jr main
proc:
    and a
    jr nz,one
    jr z,zero
one:
    ld a,'1'
    rst 10h
    ret
zero:
    ld a,'0'
    rst 10h
    ret
main:
    ld a,0
    call proc
    ld a,'E'
    rst 10h
    ret

