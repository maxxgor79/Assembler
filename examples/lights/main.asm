; main program
    org 32768
    .include "gfx.asm"
    .def TAPE_PORT 254
    .def OUTPUT_PORT 31

    call cls
    call read_port
    call indicator
    ret

read_port:
    ld hl, read_port_value
    ld c, (hl)
    rr c
    in a, (254)
    bit 6, a
    jr z, read_port_zero
    set 7,c
read_port_zero:
    ld (hl), c
    ld a, c
    ret
read_port_value:
     db 0

