; main program
    org 32768
    .include "gfx.asm"
    .def TAPE_PORT 254
    .def OUTPUT_PORT 31

    ld hl, f0ffh
    ld bc, interrupt
    ld (hl), bc
    ld a, h
    ld i, a
    ret

; interruption procedure
interrupt:
    di
    push af
    push bc
    push de
    call cls
    call rw_port
    call indicator
    pop de
    pop bc
    pop af
    ei
    reti

; read data from port and put the data into another one
rw_port:
    ld hl, rw_port_value
    ld c, (hl)
    rr c
    in a, (254)
    bit 6, a
    jr z, rw_port_zero
    set 7,c
rw_port_zero:
    ld (hl), c
    ld a, c
    out (31), a
    ret

; temporary data from port 254
rw_port_value:
     db 0
