	org 40000
	ld hl, text
	ld a, end
l1:	
	ld a, (hl)
	and a
	ret z
	push hl
	rst 8
	pop hl
	inc hl
	jr l1
text:   db "Hello world!\0"
end: