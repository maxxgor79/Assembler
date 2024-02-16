; main program
    org 32768
    .include "gfx.asm"
    .def LPT_PORT 0x1f
    .def TAPE_PORT 0xfe 
vector_addr:
    equ f0ffh
    ld bc, interrupt
    ld (vector_addr), bc
    ld a, vector_addr >> 8
    ld i, a
    im 2
    ret

; interruption procedure
interrupt:
    di
    push af
    push hl	
    push bc
    push de
    push ix
    push iy
    call cls
    call rw_port
    call indicator
    pop iy
    pop ix
    pop de
    pop bc
    pop hl
    pop af
    ei
    reti

; read data from port and put the data into another one
rw_port:
    ld hl, rw_port_value
    ld c, (hl)
    rr c
    in a, ($TAPE_PORT)
    bit 6, a
    jr z, rw_port_zero
    set 7,c
rw_port_zero:
    ld (hl), c
    ld a, c
    out ($LPT_PORT), a
    ret

; temporary data from port 254
rw_port_value:
     db 0
;    .defres "test1.txt" "test2.txt"
    .img "img.png"