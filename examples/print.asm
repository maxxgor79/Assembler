	org 40000
	ld a,6555
	ld a, l2-l1
	ld hl, text
l1:	ld a,(hl)
	push hl
	rst 8h
	pop hl
	inc hl
	jr l2
	nop
	rst 28h
l2:	ret
text:   db "23456"
end: