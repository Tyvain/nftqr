/*
 * QR Code generator demo (Java)
 *
 * Run this command-line program with no arguments. The program creates/overwrites a bunch of
 * PNG and SVG files in the current working directory to demonstrate the creation of QR Codes.
 *
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/qr-code-generator-library
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package io.nayuki.qrcodegen;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;


public final class CoranSqr {
    static File outputDirectory = new File("out2");
    static File resultFile = new File("out3/result.png");

    static File inputFile = new File("srj/quranSqr.txt");
    static int nbCharInQr = 2360;
// calcul chunck size pour un carre
    // max 2950
    // 773625 / 2950 = 265
    // racine-> 17
    // 17*17= 289
    // 18*18=324
    static QrCode.Ecc verif = QrCode.Ecc.LOW;

    private static int getQrSize() {
        return 163;
    }

    static int xNbQrCode = 18;
    static int yNbQrCode = 18;
// green 0,90,0
    public static int fgColor = new Color(0,90,0).getRGB();
    public static int bgColor = new Color(255,255,255).getRGB();


    public static void main(String[] args) throws IOException {
//        System.out.println("Size: " + inputFile.length());
        doFileBible();
        BufferedImage result = new BufferedImage(xNbQrCode * getQrSize(), yNbQrCode * getQrSize(), BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();

        Collection<File> images = FileUtils.listFiles(outputDirectory, new String[]{"png"}, false);

        int x = 0;
        int y = 0;
        for (File image : images) {
            BufferedImage bi = ImageIO.read(image);
            System.out.println(image.getName() + " " + x + "/" + y + " dernier taille: " + bi.getWidth() + " x "+ bi.getHeight());
            g.drawImage(bi, x, y, null);
            x += getQrSize();
            if (x >= xNbQrCode * getQrSize()) {
                y += getQrSize();
                x = 0;
            }
        }
        ImageIO.write(result, "png", resultFile);

         }

    private static void doFileBible() {
        try {
            String text = Files.readString(Paths.get(inputFile.getPath()));
            QrCode.Ecc errCorLvl = verif;
            FileUtils.cleanDirectory(outputDirectory);
            for (int i = 0; i < StringUtils.length(text); i += nbCharInQr) {
                int nb = i / nbCharInQr;
                String fileNumber = StringUtils.leftPad("" + nb, 10, "0");
                String fileName = "/" + fileNumber + ".png";
                System.out.println("fichier " + fileName);
                generatePng(text, outputDirectory + fileName, errCorLvl, i, i + nbCharInQr, fgColor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generatePng(String text, String title, QrCode.Ecc errCorLvl, int start, int end, int color) throws IOException {
        text = StringUtils.substring(text, start, end);
        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol

        BufferedImage img = qr.toImage(1, 1, color, bgColor);           // Convert to bitmap image
        File imgFile = new File(title);   // File path for output
        ImageIO.write(img, "png", imgFile);              // Write image to file
    }

    private static void writePng(BufferedImage img, String filepath) throws IOException {
        ImageIO.write(img, "png", new File(filepath));
    }

}
