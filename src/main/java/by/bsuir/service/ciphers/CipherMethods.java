package by.bsuir.service.ciphers;

public interface CipherMethods {
    byte[] encryption(byte[] content);
    byte[] decryption(byte[] bytesCipher);
    byte[] generateLfsrKey(int length);
    byte[] generateGeffeKey(int length);
    byte[][] generateArrayOfKeys(int length);
}
