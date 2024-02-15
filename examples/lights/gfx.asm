; Clean up the screen
    .def WHITE 7 << 3
    .def BLACK 0
screen_attr: 
    equ 16384 + 192 * 32
cls:
    ld hl, 16384
    ld bc, 32 * 192
cls_loop1: 
    xor a,a
    ld (hl), a
    inc hl
    dec bc
    or c
    or b
    jr nz, cls_loop1
    ld bc, 32 * 24
cls_loop2:
    ld a, $WHITE
    ld (hl), a
    inc hl
    dec bc
    xor a,a
    or b
    or c
    jr nz, cls_loop2
    ret

; Draw indicator on the screen
indicator:
    ld hl, screen_attr + 12 * 32 + 8
    ld c, a
    ld b, 8
indicator_loop:
    bit 7, c
    jr z, indicator_draw_white
    ld a, $BLACK
    jr indicator_draw
indicator_draw_white:
    ld a, $WHITE
indicator_draw:
    ld (hl), a
    inc hl
    inc hl
    rl c
    djnz indicator_loop
    ret