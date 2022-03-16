package jacobi;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Jacobi {
    private final BigInteger zero = BigInteger.valueOf(0L);
    private final BigInteger one = BigInteger.valueOf(1L);
    private final BigInteger two = BigInteger.valueOf(2L);
    private final BigInteger three = BigInteger.valueOf(3L);
    private final BigInteger four = BigInteger.valueOf(4L);

    public BigInteger getPrimeNumber(int numberOfBits, SecureRandom rand) {
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

    public int getJacobiSymbol(BigInteger a, BigInteger n) {
        a = a.mod(n);
        if (a.equals(one) || n.equals(one)) {
            return 1;
        }
        if (a.equals(zero)) {
            return 0;
        }
        int e = 0;
        while (a.remainder(two).equals(zero)) {
            e++;
            a = a.divide(two);
        }
        int s;
        if (e % 2 == 0) {
            s = 1;
        } else {
            if (n.mod(BigInteger.valueOf(8L)).equals(one) || n.mod(BigInteger.valueOf(8L)).equals(BigInteger.valueOf(7L))) {
                s = 1;
            } else {
                s = -1;
            }
        }
        if (n.mod(four).equals(three) && a.mod(four).equals(three)) {
            s = -s;
        }
        return s * getJacobiSymbol(n.mod(a), a);
    }

    public String getOutput(int length, BigInteger n) {
        StringBuilder output = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            if (getJacobiSymbol(BigInteger.valueOf(i), n) == 1)
                output.append(1);
            else
                output.append(0);
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
        try{
            FileWriter fw = new FileWriter("Joutput.txt");
            fw.write(output);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String filePath = "Joutput.txt";
        String zipPath = "Joutput-zip.zip";

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
            File fileToZip = new File(filePath);
            zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
            Files.copy(fileToZip.toPath(), zipOut);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Jacobi generatorJacobi = new Jacobi();
        SecureRandom rand = new SecureRandom();
        int numberOfBits = 1024;
        BigInteger n = generatorJacobi.generateN(numberOfBits, rand);
        //System.out.println("Value of n: " + n);
        String output = generatorJacobi.getOutput(100000, n);
        generatorJacobi.testCount(output);
        generatorJacobi.testCompress(output);
    }
}
