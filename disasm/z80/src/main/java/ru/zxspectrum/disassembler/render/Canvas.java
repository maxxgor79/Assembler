package ru.zxspectrum.disassembler.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.error.RenderException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author maxim
 * Date: 12/24/2023
 */
public class Canvas implements Render {
    @Getter
    @Setter
    @NonNull
    private File file;

    @Getter
    @Setter
    @NonNull
    private Row org;

    @Getter
    @Setter
    @NonNull
    private Row z80;

    @Setter
    @Getter
    @NonNull
    private String encoding = Charset.defaultCharset().name();

    private final ConcurrentHashMap<BigInteger, Row> rowsMap = new ConcurrentHashMap<>();

    @Override
    public String generate() throws RenderException {
        List<BigInteger> addressList = new LinkedList<>(rowsMap.keySet());
        Collections.sort(addressList, BigInteger::compareTo);
        StringBuilder sb = new StringBuilder();
        if (org != null) {
            sb.append(org.generate());
        }
        if (z80 != null) {
            sb.append(z80.generate());
        }
        for (BigInteger address : addressList) {
            Row row = rowsMap.get(address);
            row.setAddress(new Address(address));
            sb.append(row.generate());
        }
        return sb.toString();
    }

    public void put(@NonNull BigInteger address, @NonNull Row row) {
        rowsMap.put(address, row);
    }

    public Row get(@NonNull BigInteger address) {
        return rowsMap.get(address);
    }

    public boolean contains(BigInteger address) {
        if (address == null) {
            return false;
        }
        return rowsMap.containsKey(address);
    }

    public void flush(@NonNull OutputStream os) throws IOException, RenderException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName(encoding)));
        writer.write(generate());
        writer.flush();
    }

    public int getLineCount() {
        return rowsMap.size();
    }

    public void walkThrough(Consumer<Map.Entry<BigInteger, Row>> consumer) {
        for (Map.Entry<BigInteger, Row> entry : rowsMap.entrySet()) {
            consumer.accept(entry);
        }
    }
}
