package ru.assembler.zxspectrum.basic;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Gorin
 */
public enum Operator {
    RND(0xa5), _INKEY(0xa6, "INKEY$"), PI(0xa7), FN(0xa8), POINT(0xa9)
    , _SCREEN(0xaa, "SCREEN$"), ATTR(0xab), AT(0xac), TAB(0xad)
    , _VAL(0xae, "VAL$"), CODE(0xaf), VAL(0xb0), LEN(0xb1), SIN(0xb2)
    , COS(0xb3), TAN(0xb4), ASN(0xb5), ACS(0xb6), ATN(0xb7), LN(0xb8), EXP(0xb9)
    , INT(0xba), SQR(0xbb), SGN(0xbc), ABS(0xbd), PEEK(0xbe), IN(0xbf), USR(0xc0)
    , _STR(0xc1, "STR$"), _CHR(0xc2, "CHR$"), NOT(0xc3), BIN(0xc4)
    , OR(0xc5), AND(0xc6), LESS_EQ(0xc7, "<="), GREAT_EQ(0xc8, ">=")
    , NOT_EQ(0xc9, "<>"), LINE(0xca), THEN(0xcb), TO(0xcc), STEP(0xcd)
    , DEF_FN(0xce, "DEF FN"), CAT(0xcf), FORMAT(0xd0), MOVE(0xd1), ERASE(0xd2)
    , _OPEN(0xd3, "OPEN #"), _CLOSE(0xd4, "CLOSE #"), MERGE(0xd5), VERIFY(0xd6)
    , BEEP(0xd7), CIRCLE(0xd8), INK(0xd9), PAPER(0xda), FLASH(0xdb), BRIGHT(0xdc)
    , INVERSE(0xdd), OVER(0xde), OUT(0xdf), LPRINT(0xe0), LLIST(0xe1), STOP(0xe2)
    , READ(0xe3), DATA(0xe4), RESTORE(0xe5), NEW(0xe6), BORDER(0xe7), CONTINUE(0xe8)
    , DIM(0xe9), REM(0xea), FOR(0xeb), GOTO(0xec, "GO TO")
    , GOSUB(0xed, "GO SUB"), INPUT(0xee), LOAD(0xef), LIST(0xf0), LET(0xf1)
    , PAUSE(0xf2), NEXT(0xf3), POKE(0xf4), PRINT(0xf5), PLOT(0xf6), RUN(0xf7)
    , SAVE(0xf8), RANDOMIZE(0xf9), IF(0xfa), CLS(0xfb), DRAW(0xfc), CLEAR(0xfd)
    , RETURN(0xfe), COPY(0xff), _INK(0x10, "INK"), _PAPER(0x11, "PAPER")
    , _FLASH(0x12, "FLASH"), _BRIGHT(0x13, "BRIGHT")
    , _INVERSE(0x14, "INVERSE"), _OVER(0x15, "OVER"), _AT(0x16, "AT")
    , _TAB(0x17, "TAB");


    Operator(int code, String nativeName) {
        this.code = code;
        this.nativeName = nativeName;
    }

    Operator(int code) {
        this(code, null);
    }

    public String getNativeName() {
        return nativeName == null ? this.name() : nativeName;
    }

    public static Operator get(int code) {
        initTable();
        return codeToOperatorMap.get(Integer.valueOf(code));
    }

    public static Operator get(@NonNull String name) {
        initTable();
        return nameToOperatorMap.get(name.toLowerCase());
    }

    private static void initTable() {
        if (codeToOperatorMap == null) {
            codeToOperatorMap = new HashMap<>();
            for (Operator op : values()) {
                codeToOperatorMap.put(Integer.valueOf(op.code), op);
            }
        }
        if (nameToOperatorMap == null) {
            nameToOperatorMap = new HashMap<>();
            for (Operator op : values()) {
                nameToOperatorMap.put(op.getNativeName().toLowerCase(), op);
            }
        }
    }

    @NonNull
    public String toString() {
        return getNativeName();
    }

    @Getter
    private final int code;

    private final String nativeName;

    private static Map<Integer, Operator> codeToOperatorMap = null;

    private static Map<String, Operator> nameToOperatorMap = null;


}
