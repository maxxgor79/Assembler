package ru.assembler.microsha.io.generator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import ru.zxspectrum.io.audio.generator.Generator;
import ru.assembler.microsha.io.rkm.RkmData;
import ru.zxspectrum.io.audio.wav.WavWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
//Thanks to Alexander Alexeev
@Slf4j
public class SoundGenerator extends Generator {
    public static final int RATE_LOWEST = 8000;

    public static final int RATE_LOW = 11050;

    public static final int RATE_NORMAL = 22050;

    public static final int RATE_HIGH = 44100;

    public static final int RATE_HIGHEST = 44100;

    private static final int DEFAULT_SAMPLE_RATE = 44100;// in Hertz

    private static final int BAUD_RATE = 700;

    private byte[] zeroBitData;

    private byte[] oneBitData;

    private byte[] buf;

    @Setter
    @Getter
    private boolean useFilter = true;


    // generated with https://fiiir.com/
    private static final double[] FIR_WEIGHTS = {
            0.000577926080672738, 0.000459465018502213, 0.000314862245890514, 0.000114058758552773,
            -0.000176232019410298, -0.000586368131836170, -0.001136970252974637, -0.001832200078313594,
            -0.002653914944386688, -0.003557457996743626, -0.004469734119871817, -0.005290040439872776,
            -0.005893881011277897, -0.006139718992073919, -0.005878331252147731, -0.004964157085603880,
            -0.003267801176075040, -0.000688684837013550, 0.002833242904185398, 0.007307810544085602,
            0.012687912264589127, 0.018866545092452867, 0.025678084518976477, 0.032903957573255177,
            0.040282515704921813, 0.047522557733791673, 0.054319636536848272, 0.060374030484608231,
            0.065409095148934615, 0.069188648369429409, 0.071532089395465556, 0.072326107924878466,
            0.071532089395465556, 0.069188648369429409, 0.065409095148934615, 0.060374030484608238,
            0.054319636536848279, 0.047522557733791680, 0.040282515704921813, 0.032903957573255184,
            0.025678084518976480, 0.018866545092452870, 0.012687912264589122, 0.007307810544085603,
            0.002833242904185400, -0.000688684837013550, -0.003267801176075043, -0.004964157085603879,
            -0.005878331252147732, -0.006139718992073923, -0.005893881011277897, -0.005290040439872781,
            -0.004469734119871819, -0.003557457996743629, -0.002653914944386688, -0.001832200078313594,
            -0.001136970252974637, -0.000586368131836170, -0.000176232019410298, 0.000114058758552773,
            0.000314862245890515, 0.000459465018502213, 0.000577926080672738};

    private int[] firWindow = new int[FIR_WEIGHTS.length];

    public SoundGenerator() {
        setSampleRate(DEFAULT_SAMPLE_RATE);
        initData();
    }

    public SoundGenerator(@NonNull File file) {
        setSampleRate(DEFAULT_SAMPLE_RATE);
        setFile(file);
        initData();
    }

    @Override
    public void generateWav(@NonNull Object data) throws IOException {
        Objects.requireNonNull(file, "File non null is required");
        if (!(data instanceof RkmData)) {
            throw new IllegalArgumentException("Unsupported argument type: " + data.getClass());
        }
        final RkmData rkmData = (RkmData) data;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Arrays.fill(firWindow, (byte) 0x80);
        // preamble. this sounds like an ~3 second long 700 Hz tone
        for (int i = 0; i < 256; i++) {
            writeByte(baos, 0);
        }
        // sync byte
        writeByte(baos, 0xE6);
        final ByteArrayOutputStream tapeDataOs = new ByteArrayOutputStream();
        rkmData.save(tapeDataOs);
        // save rkm data
        write(baos, tapeDataOs.toByteArray());
        Arrays.fill(buf, 0, buf.length - 1, (byte) 0x80);
        // some intermediate values in the end, for the FIR filter
        write(baos, Arrays.copyOf(buf, FIR_WEIGHTS.length));
        WavWriter wavFile = new WavWriter(baos.toByteArray(), sampleRate, 1);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wavFile.write(fos);
        }
    }

    protected byte[] firLowPassFilter(@NonNull final byte[] data) {
        if (!useFilter) {
            return data;
        }
        for (int i = 0; i < data.length; i++) {
            ArrayUtils.shift(firWindow, 1);
            firWindow[0] = data[i];
            double sum = 0.0;
            for (int j = 0; j < firWindow.length; j++) {
                sum += firWindow[j] * FIR_WEIGHTS[j];
            }
            buf[i] = (byte) (sum + 0x80);// convert into unsigned
        }
        return buf;
    }

    protected int writeByte(@NonNull OutputStream os, int b) throws IOException {
        int size = 0;
        for (int i = 0; i < 8; i++) {
            final byte[] data = ((b >>> (7 - i)) & 1) == 1 ? oneBitData : zeroBitData;
            os.write(firLowPassFilter(data));
            size += data.length;
        }
        return size;
    }

    protected int write(@NonNull final OutputStream os, @NonNull final byte[] data) throws IOException {
        int size = 0;
        for (byte b : data) {
            size += writeByte(os, b);
        }
        return size;
    }

    protected void initData() {
        final int samplePerBit = sampleRate / BAUD_RATE;
        buf = new byte[samplePerBit];
        for (int i = 0; i < samplePerBit; i++) {
            if (i < samplePerBit / 2) {
                buf[i] = (byte) ((255 - 96) * volume);
            } else {
                buf[i] = (byte) (96 * volume);
            }
        }
        zeroBitData = Arrays.copyOf(buf, buf.length);
        oneBitData = Arrays.copyOf(buf, buf.length);
        ArrayUtils.reverse(oneBitData);
    }

    @Override
    public void setSampleRate(int rate) {
        super.setSampleRate(rate);
        initData();
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);
        initData();
    }
}
