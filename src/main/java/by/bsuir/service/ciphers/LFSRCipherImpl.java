package by.bsuir.service.ciphers;

public class LFSRCipherImpl implements CipherMethods {

    private final int[] polynomial;
    private final long register;
    private final long bitMask;
    private long currentRegister;

    public LFSRCipherImpl(int[] polynomial, long register) {
        this.polynomial = polynomial;
        this.register = register;
        this.bitMask = generateBitMask();
    }

    /**
     *  Перевод ключа в строковое представление для вывода на экран пользовательского приложения
     */
    public static String translateKeyToString(byte[] key, int counter) {

        StringBuilder stringBuilder = new StringBuilder();

        if (counter > key.length) {
            counter = key.length;
        }

        for (int i = 0; i < counter; i++) {

            StringBuilder builder = new StringBuilder(Integer.toBinaryString(key[i] & 255));

            for (int j = builder.length(); j < 8; j++) {
                builder.insert(0, "0");
            }

            stringBuilder.append(builder);
        }

        return stringBuilder.toString();
    }

    /**
     *  Вычисление бита по его позиции
     */
    private byte getBitAtPosition(int position) {
        return (byte)((byte)(currentRegister >> position - 1) & 1);
    }

    /**
     *  Генерация битовой маски
     */
    private long generateBitMask() {
        return Long.parseLong("1".repeat(Math.max(0, polynomial[0])), 2);
    }

    /**
     *  Реализация метода шифрования файла в массив байт
     */
    @Override
    public byte[] encryption(byte[] content) {

        byte[] lfsrKey = generateLfsrKey(content.length);
        byte[] bytesCipher = new byte[content.length];

        for (int i = 0; i < content.length; i++) {
            bytesCipher[i] = (byte) (content[i] ^ lfsrKey[i]);
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
     *  Генерация LFSR-ключа
     */
    @Override
    public byte[] generateLfsrKey(int length) {

        currentRegister = register;
        byte[] lfsrKey = new byte[length];

        // генерация LFSR-ключа
        for (int i = 0; i < lfsrKey.length; i++) {
            for (int j = 0; j < 8; j++) {

                byte abortedBit = getBitAtPosition(polynomial[0]);

                // запись "выкинутого" бита в LFSR-ключ
                lfsrKey[i] = (byte) (lfsrKey[i] | (abortedBit << (8 - j - 1)));

                // определение первого бита новой последовательности (XOR первого и последнего бита)
                byte firstBit = abortedBit;
                for (int m = 1; m < polynomial.length; m++) {
                    firstBit ^= getBitAtPosition(polynomial[m]);
                }

                // сдвиг регистра на одну позицию влево
                currentRegister = (currentRegister << 1) & bitMask;

                // установка первого бита новой последовательности
                currentRegister = currentRegister | firstBit;
            }
        }

        return lfsrKey;
    }

    /**
     *  ПУСТОЙ МЕТОД!
     *  Генерация ключа Геффе
     */
    @Override
    public byte[] generateGeffeKey(int length) {
        return new byte[0];
    }

    /**
     *  ПУСТОЙ МЕТОД!
     *  Генерация массива ключей для вывода на экран пользовательского приложения
     */
    @Override
    public byte[][] generateArrayOfKeys(int length) {
        return new byte[0][];
    }
}