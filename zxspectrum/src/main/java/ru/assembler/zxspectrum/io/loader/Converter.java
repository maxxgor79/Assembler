package ru.assembler.zxspectrum.io.loader;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.assembler.io.wav.WavInputStream;
import ru.assembler.zxspectrum.io.tzx.TzxWriter;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ru.assembler.zxspectrum.io.loader.Status.Signal;

/**
 * Thanks to ibancg from github for source written on C++
 */
@Slf4j
public class Converter {
    protected static final int CARRIER_COUNTER_PERIOD = 200;

    protected static final int EDGE_TRANSIT = 3;

    protected static final double[] FILTER_A = {1.000000000000000, -3.813865383597037,
            5.458723791505597, -3.474942611565117,
            0.830107707951727};
    protected static final double[] FILTER_B = {0.911102468413717, -3.644409873654869,
            5.466614810482302, -3.644409873654869,
            0.911102468413717};
    private InputStream is;

    private OutputStream os;

    @Getter
    private Status status;

    private Filter filter;

    private int sampleIndex;

    private boolean stop;

    private final double[] edges = new double[3];

    private double afterBlockPause; // signal parameters.

    @Getter
    private int sampleRate;   // soundcard sample rate.

    @Getter
    private int bps;

    private int globalBitCounter;

    private double maxTol;

    private double oldSample;

    private int oldSampleIndex;

    private boolean edgeFound;

    private double edge;

    @Getter
    @Setter
    private boolean saveInTzx;

    private TzxWriter tzxWriter;

    public Converter(final int sampleRate, final int bps, final int numChannels, @NonNull final InputStream is
            , @NonNull final OutputStream os) throws UnsupportedAudioFileException {
        if (numChannels != 1) {
            throw new UnsupportedAudioFileException("Only mono format supported");
        }
        this.sampleRate = sampleRate;
        this.bps = bps;
        this.is = is;
        this.os = os;
        status = Status.Noise;
        sampleIndex = 0;
        filter = new Filter(FILTER_A, FILTER_B);
    }

    public Converter(@NonNull WavInputStream wis, @NonNull final OutputStream os) throws UnsupportedAudioFileException {
        this(wis.getSampleRate(), wis.getBps(), wis.getNumChannels(), wis, os);
    }

    private int readSample() throws IOException {
        int sample1 = is.read();
        if (sample1 == -1) {
            throw new EOFException();
        }
        int sample2;
        switch (bps) {
            case 8:
                sample1 *= 256;
                break;
            case 16:
                sample2 = is.read();
                if ((sample1 | sample2) < 0) {
                    throw new EOFException();
                }
                sample1 = (sample2 << 8) | sample1;
                break;
        }
        return sample1;
    }

    protected double getSample() throws IOException {
        int newSample = readSample();
        double sample = filter.filter(newSample);
        sampleIndex++;
        edge = -1;
        if (edgeFound) {
            edgeFound = false;
        } else {
            if ((sample * oldSample) <= 0) { // edge
                // lineal interpolation
                edge = (sampleIndex - 1 + oldSample / (oldSample - sample));
                edgeFound = true;
            }
        }
        oldSample = sample;
        oldSampleIndex = sampleIndex;
        return sample;
    }

    public boolean execute() throws IOException {
        log.info("Decoding... (press CTRL-C to abort)");
        boolean result = false;
        double noiseThresholdDb = 37.0; // dB
        double noiseThreshold = Math.pow(10.0, noiseThresholdDb / 10.0);
        double pilotTolerance = 0.4;
        int pilotMinEdges = 200;
        double bitTolerance = 0.3;
        status = Status.Noise;
        stop = false;
        edges[0] = edges[1] = edges[2] = 0;
        int carrierCounter = 0;
        int edgeCounter = 0;
        final int bitCounter[] = new int[]{0, 0};
        double edgeAccum = 0;
        double mean, ratio, period, tol;
        double pilotFreq, sync1Freq, sync2Freq;
        final double bitFreq[] = new double[2];
        final double bitAccum[] = new double[2];
        int endBlock = 0;
        int flipFlop = 0; // bit selector, 0/1
        final byte[] data = new byte[131072]; // maximum of 128Kb per block
        double sample = 0;
        if (saveInTzx) {
            tzxWriter = new TzxWriter(os);
            tzxWriter.writeHeader();
        } else {
            tzxWriter = null;
        }
        while (!stop) {
            try {
                sample = getSample();
            } catch (EOFException e) {
                if (status == Status.Data) {
                    status = Status.Decode;
                } else {
                    status = Status.Noise;
                }
                log.info("END of file");
                stop = true;
            }
            if (Math.abs(sample) > noiseThreshold) {
                carrierCounter = CARRIER_COUNTER_PERIOD;
                if (status == Status.Noise) { // found carrier
                    log.info("{}: Found carrier", sampleIndex);
                    status = Signal;
                    edges[0] = edges[1] = edges[2] = 0;
                    edgeCounter = 0;
                    edgeAccum = 0;
                    continue;
                }
            } else {
                carrierCounter--;
                if (carrierCounter == 0) { // lost carrier
                    log.info("{}: Lost Carrier", sampleIndex);
                    status = (status == Status.Data) ? Status.Decode : Status.Noise;
                }
            }

// per edge analysis
            if ((status != Status.Noise) && (edge > 0)) {
                edges[2] = edges[1];
                edges[1] = edges[0];
                edges[0] = edge;     // shift edge vector.
                edgeCounter++;
                switch (status) {
                    case Signal:
                        if (edgeCounter >= EDGE_TRANSIT) {
                            edgeAccum += edges[0] - edges[1];
                            mean = edgeAccum / (edgeCounter - EDGE_TRANSIT + 1);
                            ratio = 1.0 - (edges[0] - edges[1]) / mean;
                            if (Math.abs(ratio) > pilotTolerance) {
                                edges[0] = edges[1] = edges[2] = 0;
                                edgeCounter = 0;
                                edgeAccum = 0; // reset status
                            }
                            if (edgeCounter == pilotMinEdges) {
                                pilotFreq = 0.5 * sampleRate / mean;
                                if (endBlock != 0) {
                                    afterBlockPause = 1000.0 * (sampleIndex - endBlock) / sampleRate;
                                    save(data, globalBitCounter >> 3, (int) Math.abs(afterBlockPause));
                                }
                                log.info(" {}: found PILOT f={} Hz", sampleIndex, pilotFreq);
                                endBlock = 0;
                                status = Status.Pilot;
                            }
                        }
                        continue;

                    case Pilot:
                        mean = edgeAccum / (edgeCounter - EDGE_TRANSIT + 1);
                        ratio = 1.0 - (edges[0] - edges[1]) / mean;
                        if (Math.abs(ratio) > pilotTolerance) {
                            pilotFreq = 0.5 * sampleRate / mean;
                            sync1Freq = 0.5 * sampleRate / (edges[0] - edges[1]);
                            log.info(" {}: found SYNC1 f={} Hz", sampleIndex, sync1Freq);
                            status = Status.Sync1;
                            continue;
                        }
                        edgeAccum += edges[0] - edges[1];
                        continue;
                    case Sync1:
                        sync2Freq = 0.5 * sampleRate / (edges[0] - edges[1]);
                        log.info(" {}: found SYNC2 f={} Hz", sampleIndex, sync2Freq);
                        log.info(" getting data");
                        status = Status.Data;
                        edgeCounter = 0;
                        globalBitCounter = 0;
                        bitAccum[0] = bitAccum[1] = 0.0;
                        bitCounter[0] = bitCounter[1] = 0;
                        flipFlop = 0;
                        continue;
                    case Data:
                        if ((edgeCounter & 0x01) == 0) {
                            period = edges[0] - edges[2]; // whole period
                            if ((globalBitCounter & 0x07) == 0) {// multiple of 8
                                data[globalBitCounter >>> 3] = 0;
                            }
                            if (globalBitCounter > 0) {
                                mean = bitAccum[flipFlop] / bitCounter[flipFlop];
                                tol = 1 - period / mean;
                                if (Math.abs(tol) > bitTolerance) {
                                    if (globalBitCounter <= 7) {
                                        log.info(" {}: ERROR: not uniform MARKER, tol={}",
                                                sampleIndex, tol);
                                        status = Status.Noise;
                                        continue;
                                    }
                                    if (tol > maxTol) {
                                        maxTol = tol;
                                        log.info("{}: max_tol = {}", sampleIndex, tol);
                                    }
                                    flipFlop = (flipFlop + 1) & 1; // bit change
                                    mean = bitAccum[flipFlop] / bitCounter[flipFlop];
                                    tol = 1 - period / mean;
                                    if (Math.abs(tol) > 0.8) {
                                        log.info(" {}: warning: bit error, assuming end, tol={}",
                                                sampleIndex, tol);
                                        status = Status.Decode;
                                    }
                                }
                            }
                            data[globalBitCounter >> 3] <<= 1; // shift the byte
                            data[globalBitCounter >> 3] |= flipFlop; // add the bit
                            globalBitCounter++;
                            bitCounter[flipFlop]++;
                            bitAccum[flipFlop] += period;
                            if ((globalBitCounter & 0x1fff) == 0) {
                                log.info("  {} read\n", globalBitCounter >>> 13);
                            }
                        }
                        break;
                }
            }
            if (status == Status.Decode) {
                endBlock = sampleIndex;
                globalBitCounter = (globalBitCounter >>> 3) << 3; // trunc to a 8 multiple
                int checksum = 0;
                boolean reverse = (bitAccum[0] / bitCounter[0]) > (bitAccum[1] / bitCounter[1]);
                for (int i = 0; i < (globalBitCounter >> 3); i++) {
                    checksum ^= data[i];
                    if (reverse) {
                        data[i] ^= 0xff; // reverse bits
                    }
                }
                flipFlop = reverse ? 1 : 0;
                bitFreq[0] = 1.0 * sampleRate * bitCounter[flipFlop] / bitAccum[flipFlop];
                bitFreq[1] = 1.0 * sampleRate * bitCounter[(flipFlop + 1) & 1] / bitAccum[(flipFlop + 1) & 1];
                log.info("{} bytes read ({} bits) , f0={} Hz, f1={} Hz, checksum={} ",
                        globalBitCounter >>> 3, globalBitCounter, bitFreq[0], bitFreq[1], checksum);
                if (checksum == 0) {
                    log.info("ok");
                    result = true;
                } else {
                    log.info("fail!");
                    result = false;
                }
                status = Status.Noise;
            }
        }
        if (endBlock != 0) {
            afterBlockPause = 0;
            save(data, globalBitCounter >> 3, (int) Math.abs(afterBlockPause));
        }
        log.info("{} samples read", sampleIndex);
        return result;
    }

    private void save(byte[] buf, int size, int pause) throws IOException {
        if (saveInTzx) {
            tzxWriter.writeID10(buf, size, pause);
        } else {
            os.write(buf, 0, size);
        }
    }
}
