;;
;; This program demonstrates converting the number in HL to
;; ASCII and outputing it.
;;


   ; Set HL to 0xffff
   ld hl, 0
   dec hl

   ; Display the value
   call DispHLhex
   halt

;
; Display a 16- or 8-bit number in hex.
; The value to be shown should be stored in HL
;
DispHLhex:

   ; show "0x" as a prefix
   ld a, '0'
   out(1),a
   ld a, 'x'
   out(1),a

   ; Show the high-value
   ld  c,h
   call  OutHex8

   ; Show the low-value
   ld  c,l
   call OutHex8

   ; Return to our caller
   ret

;
; Output the hex value of the 8-bit number stored in C
;
OutHex8:
   ld  a,c
   rra
   rra
   rra
   rra
   call  Conv
   ld  a,c
Conv:
   and  $0F
   add  a,$90
   daa
   adc  a,$40
   daa
   ; Show the value.
   out (1),a
   ret