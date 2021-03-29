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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import javax.imageio.ImageIO;


public final class BibleSqr {
    static File outputDirectory = new File("out2");
    static File resultFile = new File("out3/result.png");

    static File inputFile = new File("srj/bibleSqr.txt");    // lenth: 4432781
    static int nbCharInQr = 2915;
    // calcul chunck size pour un carre
    // max 2950
    //4432781 / 2950 = 1502
    // racine-> 38,7
    // 39*39= 1521
    // 4432781 / 1521=2914
    static QrCode.Ecc verif = QrCode.Ecc.LOW;

    private static int getQrSize() {
        return verif == QrCode.Ecc.LOW ? 179 : verif == QrCode.Ecc.MEDIUM ? 159 : verif == QrCode.Ecc.MEDIUM ? 163 : 0;
    }

    static int xNbQrCode = 39;
    static int yNbQrCode = 39;
// green 0,90,0
    public static int fgColor = new Color(255,215,0).getRGB();
    public static int bgColor = new Color(255,255,255).getRGB();


    public static void main(String[] args) throws IOException {
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


//	static int intToCol(int nb)
//	{
//		int y = nb / 60;
//		int x = nb % 60;
//		System.out.println("x=" + x +" y="+y);
//		if (nb % 60 == 0){
//			y+= qrSize;
//			x=0;
//		}
////		int rgbNum = 255 - (int) ((nb/50.0)*255.0);
//		int rgbNum = 30;
//
//		return fgColor;
//	}


//	private static void doFlag() {
//		File file = new File("srj/const.txt");
//		System.out.println("fichier " + file.length()/48);
//		try {
//			String text = Files.readString(Paths.get(file.getPath()));
//			QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;
//			int chunk = 935;
//			FileUtils.cleanDirectory(new File("out2"));
//			for (int i = 0; i < StringUtils.length(text); i+= chunk){
//				System.out.println("fichier " + i + " color : " + intToCol(i));
//				generateSvg(text, "out2/flag"+i/ chunk +".svg", errCorLvl, i, i + chunk);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	private static void generateSvg(String text, String title, QrCode.Ecc errCorLvl, int start, int end) throws IOException {
//		text = StringUtils.substring(text,start, end);
//		QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
//
//		String svg = qr.toSvgString(0, BLACK, WHITE);                  // Convert to SVG XML code
//		File svgFile = new File(title);   // File path for output
//		Files.write(svgFile.toPath(), svg.getBytes(StandardCharsets.UTF_8));            // Write image to file
//	}

    private static void generatePng(String text, String title, QrCode.Ecc errCorLvl, int start, int end, int color) throws IOException {
        text = StringUtils.substring(text, start, end);
        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol

        BufferedImage img = qr.toImage(1, 1, color, bgColor);           // Convert to bitmap image
        File imgFile = new File(title);   // File path for output
        ImageIO.write(img, "png", imgFile);              // Write image to file
    }



    /*---- Demo suite ----*/

    // Creates a single QR Code, then writes it to a PNG file and an SVG file.
//	private static void doBasicDemo() throws IOException {
//		String text = "Hello, world!";          // User-supplied Unicode text
//		QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
//
//		QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
//
//		BufferedImage img = qr.toImage(10, 4);           // Convert to bitmap image
//		File imgFile = new File("hello-world-QR.png");   // File path for output
//		ImageIO.write(img, "png", imgFile);              // Write image to file
//
//		String svg = qr.toSvgString(4, WHITE, BLACK);                  // Convert to SVG XML code
//		File svgFile = new File("hello-world-QR.svg");   // File path for output
//		Files.write(svgFile.toPath(),                    // Write image to file
//			svg.getBytes(StandardCharsets.UTF_8));
//	}
//
//
//	// Creates a variety of QR Codes that exercise different features of the library, and writes each one to file.
//	private static void doVarietyDemo() throws IOException {
//		QrCode qr;
//
//		// Numeric mode encoding (3.33 bits per digit)
//		qr = QrCode.encodeText("314159265358979323846264338327950288419716939937510", QrCode.Ecc.MEDIUM);
//		writePng(qr.toImage(13, 1), "pi-digits-QR.png");
//
//		// Alphanumeric mode encoding (5.5 bits per character)
//		qr = QrCode.encodeText("DOLLAR-AMOUNT:$39.87 PERCENTAGE:100.00% OPERATIONS:+-*/", QrCode.Ecc.HIGH);
//		writePng(qr.toImage(10, 2), "alphanumeric-QR.png");
//
//		// Unicode text as UTF-8
//		qr = QrCode.encodeText("こんにちwa、世界！ αβγδ", QrCode.Ecc.QUARTILE);
//		writePng(qr.toImage(10, 3), "unicode-QR.png");
//
//		// Moderately large QR Code using longer text (from Lewis Carroll's Alice in Wonderland)
//		qr = QrCode.encodeText(
//			"Alice was beginning to get very tired of sitting by her sister on the bank, "
//			+ "and of having nothing to do: once or twice she had peeped into the book her sister was reading, "
//			+ "but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice "
//			+ "'without pictures or conversations?' So she was considering in her own mind (as well as she could, "
//			+ "for the hot day made her feel very sleepy and stupid), whether the pleasure of making a "
//			+ "daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly "
//			+ "a White Rabbit with pink eyes ran close by her.", QrCode.Ecc.HIGH);
//		writePng(qr.toImage(6, 10), "alice-wonderland-QR.png");
//	}
//
//
//	// Creates QR Codes with manually specified segments for better compactness.
//	private static void doSegmentDemo() throws IOException {
//		QrCode qr;
//		List<QrSegment> segs;
//
//		// Illustration "silver"
//		String silver0 = "THE SQUARE ROOT OF 2 IS 1.";
//		String silver1 = "41421356237309504880168872420969807856967187537694807317667973799";
//		qr = QrCode.encodeText(silver0 + silver1, QrCode.Ecc.LOW);
//		writePng(qr.toImage(10, 3), "sqrt2-monolithic-QR.png");
//
//		segs = Arrays.asList(
//			QrSegment.makeAlphanumeric(silver0),
//			QrSegment.makeNumeric(silver1));
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
//		writePng(qr.toImage(10, 3), "sqrt2-segmented-QR.png");
//
//		// Illustration "golden"
//		String golden0 = "Golden ratio φ = 1.";
//		String golden1 = "6180339887498948482045868343656381177203091798057628621354486227052604628189024497072072041893911374";
//		String golden2 = "......";
//		qr = QrCode.encodeText(golden0 + golden1 + golden2, QrCode.Ecc.LOW);
//		writePng(qr.toImage(8, 5), "phi-monolithic-QR.png");
//
//		segs = Arrays.asList(
//			QrSegment.makeBytes(golden0.getBytes(StandardCharsets.UTF_8)),
//			QrSegment.makeNumeric(golden1),
//			QrSegment.makeAlphanumeric(golden2));
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
//		writePng(qr.toImage(8, 5), "phi-segmented-QR.png");
//
//		// Illustration "Madoka": kanji, kana, Cyrillic, full-width Latin, Greek characters
//		String madoka = "「魔法少女まどか☆マギカ」って、　ИАИ　ｄｅｓｕ　κα？";
//		qr = QrCode.encodeText(madoka, QrCode.Ecc.LOW);
//		writePng(qr.toImage(9, 4), "madoka-utf8-QR.png");
//
//		segs = Arrays.asList(QrSegmentAdvanced.makeKanji(madoka));
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW);
//		writePng(qr.toImage(9, 4), "madoka-kanji-QR.png");
//	}
//
//
//	// Creates QR Codes with the same size and contents but different mask patterns.
//	private static void doMaskDemo() throws IOException {
//		QrCode qr;
//		List<QrSegment> segs;
//
//		// Project Nayuki URL
//		segs = QrSegment.makeSegments("https://www.nayuki.io/");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, QrCode.MIN_VERSION, QrCode.MAX_VERSION, -1, true);  // Automatic mask
//		writePng(qr.toImage(8, 6), "project-nayuki-automask-QR.png");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 3, true);  // Force mask 3
//		writePng(qr.toImage(8, 6), "project-nayuki-mask3-QR.png");
//
//		// Chinese text as UTF-8
//		segs = QrSegment.makeSegments("維基百科（Wikipedia，聆聽i/ˌwɪkᵻˈpiːdi.ə/）是一個自由內容、公開編輯且多語言的網路百科全書協作計畫");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 0, true);  // Force mask 0
//		writePng(qr.toImage(10, 3), "unicode-mask0-QR.png");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 1, true);  // Force mask 1
//		writePng(qr.toImage(10, 3), "unicode-mask1-QR.png");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 5, true);  // Force mask 5
//		writePng(qr.toImage(10, 3), "unicode-mask5-QR.png");
//		qr = QrCode.encodeSegments(segs, QrCode.Ecc.MEDIUM, QrCode.MIN_VERSION, QrCode.MAX_VERSION, 7, true);  // Force mask 7
//		writePng(qr.toImage(10, 3), "unicode-mask7-QR.png");
//	}



    /*---- Utilities ----*/

    // Helper function to reduce code duplication.
    private static void writePng(BufferedImage img, String filepath) throws IOException {
        ImageIO.write(img, "png", new File(filepath));
    }

}
