package ru.assembler.microsha;

import ru.assembler.microsha.io.generator.SoundGenerator;
import ru.assembler.microsha.io.rkm.RkmData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Converter {
    public static void main(String []args) {
        if (args.length == 0) {
            System.out.println("No args");
            return;
        }
        if (args.length == 1) {
            System.out.println("No second argument");
            return;
        }
        RkmData rkm = new RkmData();
        rkm.setCheckCrc(false);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(args[0]);
            rkm.read(fis);
            System.out.println("CRC=" + rkm.getCrc16());
            SoundGenerator sg = new SoundGenerator(new File(args[1]));
            sg.setSampleRate(SoundGenerator.RATE_HIGH);
            sg.generateWav(rkm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {

                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {

                }
            }
        }
    }
}
