package by.bsuir.service.files;

import by.bsuir.service.ciphers.CipherMethods;
import by.bsuir.service.ciphers.LFSRCipherImpl;

import java.io.*;

public class FilesHandling {

    private final CipherMethods cipherMethods;

    public FilesHandling(CipherMethods cipherMethods) {
        this.cipherMethods = cipherMethods;
    }

    /**
     *  Метод для чтения содержимого из файла
     */
    private byte[] readContentFromFile(String path) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] content = fileInputStream.readAllBytes();
        fileInputStream.close();

        return content;
    }

    /**
     *  Метод для записи содержимого в файл
     */
    private void writeContentToFile(String path, byte[] content) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        fileOutputStream.write(content);
        fileOutputStream.close();
    }

    /**
     *  Метод для записи ключей в файл
     */
    private void writeKeysToFile(String path, String[] content) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        fileOutputStream.write(convertStringsToBytes(content));
        fileOutputStream.close();
    }

    /**
     *  Конвертирование массива строк в массив байт
     */
    public byte[] convertStringsToBytes(String[] strings) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for (String str : strings) {
            byte[] strBytes = str.getBytes();
            byteArrayOutputStream.write(strBytes.length);
            byteArrayOutputStream.write(strBytes);
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     *  Получение пути для сохранения полученных ключей (разделитель ключей Геффе, LFSR1-3 — "(")
     */
    private String saveKeysToFile(String path) {
        return path + "_keys.txt";
    }

    /**
     *  Получение пути для сохранения зашифрованного файла
     */
    private String getEncryptedPath(String path) {
        return path + ".enc";
    }

    /**
     *  Получение пути для сохранения расшифрованного файла
     */
    private String getDecryptedPath(String path) {

        int lastIndex = path.lastIndexOf(".enc");

        if (lastIndex != -1) {
            return path.substring(0, lastIndex) + ".dec";
        }

        return path + ".dec";
    }

    /**
     *  Метод реализует шифрование выбранного файла (создает новый зашифрованный файл)
     */
    public String[] encryption(String path) throws IOException {
        byte[] content = readContentFromFile(path);
        byte[] bytesCipher = cipherMethods.encryption(content);

        writeContentToFile(getEncryptedPath(path), bytesCipher);

        byte[][] array = cipherMethods.generateArrayOfKeys(content.length);

        //writeArraysToBinaryFile(array, saveKeysToFile(path));
        writeKeysToFile(saveKeysToFile(path), getKeysArray(array, content.length));

        return getKeysArray(array, content.length);
    }

    /**
     *  Метод реализует расшифрование выбранного файла (создает новый расшифрованный файл)
     */
    public String[] decryption(String path) throws IOException {
        byte[] bytesCipher = readContentFromFile(path);
        byte[] content = cipherMethods.decryption(bytesCipher);

        writeContentToFile(getDecryptedPath(path), content);

        byte[][] array = cipherMethods.generateArrayOfKeys(content.length);

        writeKeysToFile(saveKeysToFile(path), getKeysArray(array, content.length));

        return getKeysArray(array, content.length);
    }

    /**
     *  Получение массива ключей (Геффе, LFSR1, LFSR2, LFSR3)
     */
    private String[] getKeysArray(byte[][] array, int length) {
        String geffe = translateKeyToString(array[0], length);
        String lfsr1 = translateKeyToString(array[1], length);
        String lfsr2 = translateKeyToString(array[2], length);
        String lfsr3 = translateKeyToString(array[3], length);

        return new String[] {geffe, lfsr1, lfsr2, lfsr3};
    }

    /**
     *  Метод для преобразования ключа в строковое представление
     */
    private String translateKeyToString(byte[] originalKey, int length) {
        return LFSRCipherImpl.translateKeyToString(originalKey, length);
    }
}
