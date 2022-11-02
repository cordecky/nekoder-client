package eu.kordecki.service;

import eu.kordecki.model.Header;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Converter {
    public final static byte GROUP_SEPARATOR = 29;
    private final String inFilename;
    private final String outFilename;

    public Converter(String inFilename, String outFilename) {
        this.inFilename = inFilename;
        this.outFilename = outFilename;
    }

    public void saveFile() {
        System.out.println("Converting " + inFilename + " to " + outFilename);
        byte[] imageBytes = loadImage(inFilename);
        decrypt(imageBytes, outFilename);
    }

    public void decrypt(byte[] bytes, String filename) {
        Header header = getHeader(bytes);
        try (OutputStream outputStream = new FileOutputStream(filename)) {
            outputStream.write(bytes, header.getHeaderEnd() + 1, header.getFileSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Header getHeader(byte[] bytes) {
        int headerEnd = 0;
        int fileSize;

        for(int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            byte b = bytes[i];
            if(b == GROUP_SEPARATOR) {
                headerEnd = i;
                break;
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, headerEnd));

        fileSize = byteBuffer.getInt();
        return new Header(fileSize, headerEnd);
    }

    public byte[] loadImage(String filename) {
        BufferedImage image;
        Path path = Paths.get(filename);

        try {
            image = ImageIO.read(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return unpackColors(image);
    }

    public byte[] unpackColors(BufferedImage image) {
        byte[] data = new byte[image.getHeight() * image.getWidth() * 3];
        int dataIndex = 0;
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int[] colors = new int[3];
                colors[0] = color.getRed();
                colors[1] = color.getGreen();
                colors[2] = color.getBlue();
                for(int c = 0; c < 3; c++) {
                    if(colors[c] > 127) {
                        colors[c] = (byte)(colors[c] - 256);
                    }
                    data[dataIndex++] = (byte)colors[c];
                }
            }
        }

        return data;
    }
}
