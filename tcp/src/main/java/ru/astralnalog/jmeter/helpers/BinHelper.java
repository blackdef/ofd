package ru.astralnalog.jmeter.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by plotnikov on 26.04.2017.
 */
public class BinHelper {
  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  public static String byteArrayToHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static byte[] hexStringToByteArray(String hexEncodedBinary) {
    if(hexEncodedBinary.length() % 2 != 0) {
      throw new IllegalArgumentException("Hex-encoded binary string contains an uneven no. of digits");
    } else {
      char[] sc = hexEncodedBinary.toCharArray();
      byte[] ba = new byte[sc.length / 2];

      for(int i = 0; i < ba.length; ++i) {
        int nibble0 = Character.digit(sc[i * 2], 16);
        int nibble1 = Character.digit(sc[i * 2 + 1], 16);
        if(nibble0 == -1 || nibble1 == -1) {
          throw new IllegalArgumentException("Hex-encoded binary string contains an invalid hex digit in \'" + sc[i * 2] + sc[i * 2 + 1] + "\'");
        }

        ba[i] = (byte)(nibble0 << 4 | nibble1);
      }

      return ba;
    }
  }

  public static String getHexFromBinFile(File file) throws IOException {
    String hexString = "";
    InputStream is = new FileInputStream(file);

    // Получаем размер файла
    long length = file.length();
    // Создаем массив для хранения данных
    byte[] bytes = new byte[(int)length];
    // Считываем
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length
            && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
      offset += numRead;
    }
    // Проверяем, все ли прочитано
    if (offset < bytes.length) {
      throw new IOException("Could not completely read file "+file.getName());
    }
    // Закрываем и возвращаем
    is.close();

    hexString = byteArrayToHexString(bytes);

    return hexString;
  }

  public void main()
  {

  }


}
