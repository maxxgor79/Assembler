package ru.zxspectrum.disassembler.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;
import ru.zxspectrum.disassembler.render.command.Command;

/**
 * @author maxim
 * Date: 12/24/2023
 */
@EqualsAndHashCode
public class Row implements Render {
    @Getter
    @Setter
    private Address address;

    @Getter
    @Setter
    private Tab tab;

    @Getter
    @Setter
    private Label label;

    @Getter
    @Setter
    private Command command;

    @Getter
    @Setter
    private Comment comment;

    @Getter
    @Setter
    private Eol eol;

    public Row(Address address, Tab tab, Label label, Command command,
               Comment comment, Eol eol) {
        this.address = address;
        this.tab = tab;
        this.label = label;
        this.command = command;
        this.comment = comment;
        this.eol = eol;
    }

    @Override
    public synchronized String generate() throws RenderException {
        StringBuilder sb = new StringBuilder();
        if (address != null) {
            sb.append(address.generate());
        }
        if (label != null) {
            sb.append(label.generate());
        }
        if (tab != null) {
            sb.append(tab.generate());
        }
        if (command != null) {
            sb.append(command.generate());
        }
        if (comment != null) {
            sb.append(comment.generate());
        }
        if (eol != null) {
            sb.append(eol.generate());
        }
        return sb.toString();
    }
}
