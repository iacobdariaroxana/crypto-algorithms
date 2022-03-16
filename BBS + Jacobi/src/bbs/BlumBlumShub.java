package bbs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BlumBlumShub {
    private static final BigInteger one = BigInteger.valueOf(1L);
    private static final BigInteger two = BigInteger.valueOf(2L);

    BlumBlumShub() {

    }

    public BigInteger getPrimeNumber(int numberOfBits, SecureRandom rand) {
        BigInteger three = BigInteger.valueOf(3L);
        BigInteger four = BigInteger.valueOf(4L);
        BigInteger primeNumber;
        do {
            primeNumber = new BigInteger(numberOfBits, 100, rand); // constructs a randomly generated positive BigInteger that is probably prime, with the specified bitLength.
        } while (!primeNumber.mod(four).equals(three));
        return primeNumber;
    }

    public BigInteger generateN(int numberOfBits, SecureRandom rand) {
        BigInteger p = getPrimeNumber(numberOfBits, rand);
        BigInteger q = getPrimeNumber(numberOfBits, rand);
        while (p.equals(q)) {
            q = getPrimeNumber(numberOfBits, rand);
        }
        return p.multiply(q);
    }

    public BigInteger generateSeed(int numberOfBits, SecureRandom rand, BigInteger n) {
        BigInteger seed = new BigInteger(numberOfBits, 100, rand);
        while (!seed.gcd(n).equals(one)) {
            seed = new BigInteger(numberOfBits, 100, rand);
        }

        return seed;
    }

    public String getBitOutput(int length, BigInteger n, BigInteger seed) {
        StringBuilder output = new StringBuilder();
        BigInteger x = seed;
        for (int i = 0; i < length; i++) {
            x = x.multiply(x).mod(n);
            output.append(x.mod(two));
        }
        return output.toString();
    }

    public void testCount(String output) {
        int numberOfZeros = 0;
        int numberOfOnes = 1;
        for (int i = 0; i < output.length(); i++) {
            if (output.charAt(i) == '0') numberOfZeros++;
            else numberOfOnes++;
        }
        System.out.println("Number of 0's : " + numberOfZeros);
        System.out.println("Number of 1's: " + numberOfOnes);
    }

    public void testCompress(String output) {
        try {
            FileWriter fw = new FileWriter("BBSoutput.txt");
            fw.write(output);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String filePath = "BBSoutput.txt";
        String zipPath = "BBSoutput-zip.zip";

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
            File fileToZip = new File(filePath);
            zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
            Files.copy(fileToZip.toPath(), zipOut);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void showRatio(String path){
        try {
            ZipFile zf = new ZipFile(path);
            Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                String name = ze.getName();
                long uncompressedSize = ze.getSize();
                long compressedSize = ze.getCompressedSize();

                System.out.println(name);
                System.out.println("Uncompressed size: " + uncompressedSize);
                System.out.println("Compressed size: " + compressedSize);
                System.out.println("Ratio = " + uncompressedSize/compressedSize);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        BlumBlumShub generatorBSB = new BlumBlumShub();
        SecureRandom rand = new SecureRandom();
        int numberOfBits = 1024;
        BigInteger n = generatorBSB.generateN(numberOfBits, rand);
//        System.out.println("Value of n: " + n);


        BigInteger seed = generatorBSB.generateSeed(numberOfBits, rand, n);

//        System.out.println("Value of s: " + seed);
        System.out.println(generatorBSB.getBitOutput(100000, n, seed));
        String bitOutput = generatorBSB.getBitOutput(100000, n, seed);
        generatorBSB.testCount(bitOutput);
        generatorBSB.testCompress(bitOutput);

        String oneSequence = "1".repeat(1000000);
        try {
            FileWriter fw = new FileWriter("oneSequence.txt");
            fw.write(oneSequence);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String filePath = "oneSequence.txt";
        String zipPath = "oneSequence-zip.zip";

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
            File fileToZip = new File(filePath);
            zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
            Files.copy(fileToZip.toPath(), zipOut);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        generatorBSB.showRatio("oneSequence-zip.zip");
        System.out.println();
        generatorBSB.showRatio("BBSoutput-zip.zip");
        System.out.println();
        generatorBSB.showRatio("Joutput-zip.zip");
    }
}
