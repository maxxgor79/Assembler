package ru.zxspectrum.disassembler.render;

import lombok.NonNull;
import ru.zxspectrum.disassembler.render.command.Command;
import ru.zxspectrum.disassembler.render.system.Org;

/**
 * @author maxim
 * Date: 12/29/2023
 */
public final class RowFactory {
    public static final Eol EOL = new Eol();

    public static final Tab TAB = new Tab();

    public static final Comment EMPTY_COMMENT = new Comment();

    private RowFactory() {

    }

    public static Row createRow(@NonNull Command command) {
        Row row = new Row(null, TAB, null, command, null, EOL);
        return row;
    }

    public static Row createRow(@NonNull Org command) {
        Row row = new Row(null, TAB, null, command, null, EOL);
        return row;
    }
}
