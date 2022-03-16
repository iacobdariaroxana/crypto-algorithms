package vigenere;
// teaching@ancamarianica.ro
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Vigenere {
    public static final int  MAX = 10;
    private static Random rand = new Random();
    private char[] letters = new char[26];
    Vigenere(){
        for(int i = 0; i < letters.length; i++){
            letters[i] = (char)('a' + i);
        }
    }

    public int findMaxPosition(int[] frequency) {
        int position = 0;
        for (int i = 1; i < frequency.length; i++) {
            if (frequency[i] > frequency[position]) {
                position = i;
            }
        }
        return position;
    }
    public String estimateKey(String ciphertext, int keyLength) {
        String splitCipher[] = new String[keyLength];
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < keyLength; i++) {
            splitCipher[i] = "";
        }
        for (int i = 0; i < ciphertext.length(); i++) {
            splitCipher[i % keyLength] += ciphertext.charAt(i);
        }
        for (int i = 0; i < keyLength; i++) {
            int[] freq = letterFrequency(splitCipher[i]);
            int pos = (findMaxPosition(freq) - 4 + 26) % 26;
            key.append(letters[pos]);
//            for(int j = 0; j < freq.length; j++){
//                System.out.print("("+j+","+freq[j]+")"+letters[j]+"  ");
//            }
//            System.out.println(letters[pos]);
        }
        return key.toString();
    }

    public String shiftCypher(String cypher, int m, int j, int s){
        String subtext = extractText(cypher, m, j);
        StringBuilder shiftedText = new StringBuilder();
        for(int i = 0; i < subtext.length(); i++){
            int charCode = mapLetter(subtext.charAt(i));
            charCode = (charCode + s) % 26;
            charCode += 'a';
            shiftedText.append((char)charCode);
        }
        return shiftedText.toString();
    }
    public double findMutualCoincidenceIndex(String text1, String text2){
        int[] frequenciesAlfa = letterFrequency(text1);
        int[] frequenciesBeta = letterFrequency(text2);
        double mic = 0;
        for(int i = 0; i < 26; i++){
            mic += (double) (frequenciesAlfa[i]*frequenciesBeta[i])/(text1.length()*text2.length());
        }
        return mic;
    }
    public String findKey(String normalText, String cypher, int m){
        int keyIndex[] = new int[m];
        StringBuilder sb = new StringBuilder();
        double marginOfError = 0.005;
        for(int j = 0; j < m; j++) {
            int s = -1;
            double micValue;
            while (true) {
                s += 1;
                String shiftedText = shiftCypher(cypher, m, j, s);
                micValue = findMutualCoincidenceIndex(normalText, shiftedText);
                //double approximation = findMutualCoincidenceIndex(normalText, );
                //System.out.println("MIC value:" + micValue);
                micValue = micValue - 0.065;
                if ((Math.abs(micValue - marginOfError) <= marginOfError) || s == 25)
                    break;
            }
            keyIndex[j] = (26 - s) % 26;
            sb.append(letters[keyIndex[j]]);
        }
        return sb.toString();
    }

    public int[] letterFrequency(String text) {
        int[] frequencies = new int[26];
        for (int i = 0; i < text.length(); i++){
            frequencies[mapLetter(text.charAt(i))]++;
        }
        return frequencies;
    }
    public double findCoincidenceIndex(String text){
        int[] frequencies = letterFrequency(text);
        double ic = 0;
        int len = text.length();
        for(int i = 0; i < 26; i++){
            int appearances = frequencies[i];
            ic += ((double) appearances*(appearances-1))/(len*(len-1));
        }
        return ic;
    }
    public String extractText(String cypher, int m, int j){
        StringBuilder sb = new StringBuilder();
        for(int i = j; i < cypher.length(); i += m){
            sb.append(cypher.charAt(i));
        }
        return sb.toString();
    }
    public boolean verifyValue(String cypher, int m, double[] indexes){
        double average = 0;
        double marginOfError = 0.002;
        for(int i = 0; i < m; i++){
            String subtext = extractText(cypher, m, i);
            double ic = findCoincidenceIndex(subtext);
            indexes[i] = ic;
            average += indexes[i];
            System.out.println("Coincidence index: "+ic);
        }
        average = average/indexes.length;
        System.out.println(average);

        return Math.abs(average - 0.067) < marginOfError;
    }
    public int findKeyLength(String cypher){
        int m = 1;
        boolean found = false;
        while(!found){
            m++;
            double[] indexes = new double[m];
            found =  verifyValue(cypher, m, indexes);
            System.out.println();
        }
        return m;
    }

    public String originalText(String cipherText, String key) {
        StringBuilder originalText= new StringBuilder();
        for (int i = 0 ; i < cipherText.length(); i++) {
            // converting in range 0-25
            int x = (mapLetter(cipherText.charAt(i)) - mapLetter(key.charAt(i)) + 26) % 26;
            // convert into alphabets(ASCII)
            x += 'a';
            originalText.append((char)x);
        }
        return originalText.toString();
    }
    public int mapLetter(char c){
        return Arrays.binarySearch(letters, c);
    }
    public String cipherText(String message, String key) {
        StringBuilder cipher= new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            // converting in range 0-25
            int x = (mapLetter(message.charAt(i)) + mapLetter(key.charAt(i))) %26;
            // convert into alphabets
            x += 'a';
            cipher.append((char)x);
        }
        return cipher.toString();
    }

    public String generateKey(String str, String key)
    {
        int len = str.length();
        StringBuilder keyBuilder = new StringBuilder(key);
        for (int i = 0; ; i++) {
            if (len == i)
                i = 0;
            if (keyBuilder.length() == str.length())
                break;
            keyBuilder.append(keyBuilder.charAt(i));
        }
        key = keyBuilder.toString();
        return key;
    }
    public String getKeyFromUser() {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter key:");
        return s.nextLine().toLowerCase();
    }
    public String generateRandomKey(String message, int len){
        int keyLength = rand.nextInt(message.length()/2);
        while(keyLength < 2){
            keyLength = rand.nextInt(message.length());
        }
        String str = "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++){
            int index = rand.nextInt(str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }

    public String getMessageFromFile(String fileName){
        StringBuilder message = new StringBuilder();
        try(FileInputStream f = new FileInputStream(fileName)){
            int x;
            while((x=f.read())!=-1)
            {
                if((x>=65 && x<=90) ||(x>=97 && x<=122))
                    message.append((char)x);
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
        return message.toString().toLowerCase();
    }

    public static void main(String[] args){
            Vigenere v = new Vigenere();

            String message = v.getMessageFromFile("message.txt");
            System.out.println("Text:");
            System.out.println(message);

            String key = v.getKeyFromUser();

            int length = rand.nextInt(2, MAX);
//            String key = v.generateRandomKey(message, length);
            System.out.println("Key: " + key);
            key = v.generateKey(message, key); // generates the key multiplied by the length of the message

            String cypher = v.cipherText(message, key);
            System.out.println("Cypher:");
            System.out.println(cypher);

//            System.out.println("Original:");
//            System.out.println(v.originalText(cypher, key));

            int keyLength = v.findKeyLength(cypher);
            System.out.println("Key length is: " + keyLength);

            String normalText = v.getMessageFromFile("normalText.txt");
            System.out.println("Key is: " + v.findKey(normalText, cypher, keyLength) + " (mutual coincidence)");
            //System.out.println("Key is: " + v.estimateKey(cypher, keyLength) + " (frequency)");

            System.out.println("Original:");
            System.out.println(v.originalText(cypher, key));

    }
}
