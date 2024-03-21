package by.bsuir.service.ciphers;

import javax.swing.*;

public class GeffeCipherImpl implements CipherMethods {
    private static final int[] firstDefaultPolynomial = {3, 2};
    private static final int[] secondDefaultPolynomial = {4, 1};
    private static final int[] thirdDefaultPolynomial = {5, 3};

    //private static final int[] firstDefaultPolynomial = {23, 5};
    //private static final int[] secondDefaultPolynomial = {31, 3};
    //private static final int[] thirdDefaultPolynomial = {39, 4};
    private final int[] firstPolynomial;
    private final int[] secondPolynomial;
    private final int[] thirdPolynomial;
    private final long firstRegister;
    private final long secondRegister;
    private final long thirdRegister;

    public GeffeCipherImpl(String firstInitialRegister, String secondInitialRegister, String thirdInitialRegister,
                           int[] firstPolynomial, int[] secondPolynomial, int[] thirdPolynomial) {

        this.firstPolynomial = firstPolynomial;
        this.secondPolynomial = secondPolynomial;
        this.thirdPolynomial = thirdPolynomial;

        // обработка правильности ввода начальных регистров (по их длине)
        registerLengthHandler(firstInitialRegister.length(), firstPolynomial[0], "LFSR1");
        registerLengthHandler(secondInitialRegister.length(), secondPolynomial[0], "LFSR2");
        registerLengthHandler(thirdInitialRegister.length(), thirdPolynomial[0], "LFSR3");

        firstRegister = Long.parseLong(firstInitialRegister, 2);
        secondRegister = Long.parseLong(secondInitialRegister, 2);
        thirdRegister = Long.parseLong(thirdInitialRegister, 2);
    }

    public GeffeCipherImpl(String firstInitialRegister, String secondInitialRegister, String thirdInitialRegister) {
        this(firstInitialRegister, secondInitialRegister, thirdInitialRegister, firstDefaultPolynomial, secondDefaultPolynomial, thirdDefaultPolynomial);
    }

    /**
     *  Метод для вывода ошибки на экран пользовательского приложения
     *  и её "прокидывания" внутрь программы
     */
    private void registerLengthHandler(int initialRegisterLength, int polynomialLength, String lfsrName) {

        String message = "The length of the obtained register " + lfsrName +" (" + initialRegisterLength
                + ") exceeds the maximum degree of the polynomial (" + polynomialLength + ").";

        if (initialRegisterLength > polynomialLength) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Some happend :(",
                    JOptionPane.WARNING_MESSAGE);

            throw new RuntimeException(message);
        }
    }

    /**
     *  Реализация метода шифрования файла в массив байт
     */
    @Override
    public byte[] encryption(byte[] content) {

        byte[] geffeKey = generateGeffeKey(content.length);
        byte[] bytesCipher = new byte[content.length];

        for (int i = 0; i < content.length; i++) {
            bytesCipher[i] = (byte) (content[i] ^ geffeKey[i]);
        }

        return bytesCipher;
    }

    /**
     *  Реализация метода расшифрования файла по массиву байт
     */
    @Override
    public byte[] decryption(byte[] bytesCipher) {
        return encryption(bytesCipher);
    }

    /**
     *  ПУСТОЙ МЕТОД!
     *  Генерация LFSR-ключа
     */
    @Override
    public byte[] generateLfsrKey(int length) {
        return new byte[0];
    }

    /**
     *  Генерация ключа Геффе
     */
    @Override
    public byte[] generateGeffeKey(int length) {

        byte[] geffeKey = new byte[length];
        byte[] lfsr1Key = new LFSRCipherImpl(firstPolynomial, firstRegister).generateLfsrKey(length);
        byte[] lfsr2Key = new LFSRCipherImpl(secondPolynomial, secondRegister).generateLfsrKey(length);
        byte[] lfsr3Key = new LFSRCipherImpl(thirdPolynomial, thirdRegister).generateLfsrKey(length);

        for (int i = 0; i < length; i++) {
            //geffeKey[i] = (byte)((byte)(lfsr2Key[i] & lfsr3Key[i]) ^ (byte)(~lfsr1Key[i] ^ lfsr3Key[i]));
            geffeKey[i] = (byte)((byte)(lfsr1Key[i] & lfsr3Key[i]) ^ (byte)(~lfsr3Key[i] & lfsr2Key[i]));
        }

        return geffeKey;
    }

    /**
     *  Генерация массива ключей для вывода на экран пользовательского приложения
     */
    @Override
    public byte[][] generateArrayOfKeys(int length) {

        byte[] geffeKey = new byte[length];
        byte[] lfsr1Key = new LFSRCipherImpl(firstPolynomial, firstRegister).generateLfsrKey(length);
        byte[] lfsr2Key = new LFSRCipherImpl(secondPolynomial, secondRegister).generateLfsrKey(length);
        byte[] lfsr3Key = new LFSRCipherImpl(thirdPolynomial, thirdRegister).generateLfsrKey(length);

        for (int i = 0; i < length; i++) {
            geffeKey[i] = (byte)((byte)(lfsr1Key[i] & lfsr3Key[i]) ^ (byte)(~lfsr3Key[i] & lfsr2Key[i]));
        }

        return new byte[][] {geffeKey, lfsr1Key, lfsr2Key, lfsr3Key};
    }
}