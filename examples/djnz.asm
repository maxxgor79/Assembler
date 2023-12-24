org 32768
    ld a,2
    call 5633
    ld a, '0'
    ld b, 10
loop1:
    push af
    push bc
    call 16
    pop bc
    pop af
    nop
    inc a
    djnz loop1

    ld a,'9'
    ld b,10
loop4:
    djnz loop2
    jr loop3
loop2:
    push af
    push bc
    call 0x10
    pop bc
    pop af
    dec a
    jr loop4
loop3:
    ld a,'E'
    rst 10h
    ret