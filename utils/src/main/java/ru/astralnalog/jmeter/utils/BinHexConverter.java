package ru.astralnalog.jmeter.utils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.*;

/**
 * Created by plotnikov on 26.04.2017.
 */
public class BinHexConverter {

  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  @Parameter(names = {"--srcFile", "-f"}, description = "Target file name")
  public String sourceFile;

  @Parameter(names = {"--destFile", "-d"}, description = "Destination file name")
  public String destinationFile = "result.hex";

  @Parameter(names = {"--binPath", "-s"}, description = "Path to bin files")
  public String binPath;

  @Parameter(names = {"--hexPath", "-r"}, description = "Path to hex files")
  public String hexPath;

  @Parameter(names = {"--type", "-t"}, description = "Convert type")
  public String convertType = "toHex";


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

  public static void write(String fileName, String text, boolean replace) {

    File file = new File(fileName);

    try {
      if(!file.exists()){
        file.createNewFile();
      } else if (replace){
        file.createNewFile();
      }

      PrintWriter out = new PrintWriter(file.getAbsoluteFile());

      try {
        out.append(text);
      } finally {
         out.close();
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String read(String fileName) throws FileNotFoundException {
    //Этот спец. объект для построения строки
    StringBuilder sb = new StringBuilder();
    File file = new File(fileName);

    exists(fileName);

    try {
      //Объект для чтения файла в буфер
      BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
      try {
        //В цикле построчно считываем файл
        String s;
        while ((s = in.readLine()) != null) {
          sb.append(s);
          sb.append("\n");
        }
      } finally {
        //Также не забываем закрыть файл
        in.close();
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    }

    //Возвращаем полученный текст с файла
    return sb.toString();
  }
  private static void exists(String fileName) throws FileNotFoundException {
    File file = new File(fileName);
    if (!file.exists()){
      throw new FileNotFoundException(file.getName());
    }
  }

  public static void update(String nameFile, String newText) throws FileNotFoundException {
    exists(nameFile);
    StringBuilder sb = new StringBuilder();
    String oldFile = read(nameFile);
    sb.append(oldFile);
    sb.append(newText);
    write(nameFile, sb.toString(),false);
  }


  private void binFilesConvertToHexDestinationFile() throws IOException {
    BinFileFilter binFilter = new BinFileFilter();
    File binFolder = new File(binPath);
    if (!binFolder.exists()){
      System.out.println("Bin folder dose not exist!");
      return;
    }
    File[] binFiles = binFolder.listFiles(binFilter);
    if (!(new File(destinationFile).exists())){
      new File(destinationFile).createNewFile();
    }
    for (File f : binFiles) {
       update(destinationFile,getHexFromBinFile(f));
    }
  }

  private void binFilesConvertToHexFiles() throws IOException {
    BinFileFilter binFilter = new BinFileFilter();
    File binFolder = new File(binPath);
    File hexFolder = new File(hexPath);
    if (!binFolder.exists()){
      System.out.println("Bin folder dose not exist!");
      return;
    }
    if (!hexFolder.exists()){
      hexFolder.mkdir();
    }

    File[] binFiles = binFolder.listFiles(binFilter);
    for (File f : binFiles) {
      //пишем в файл с таким же именем как исходный, но на конце добавляем расширение hex
      write(hexPath+File.separator+f.getName().split("[.]")[0]+".hex",getHexFromBinFile(f),true);
    }
  }

  class BinFileFilter implements FileFilter {
    public boolean accept(File pathname)
    {
      // проверям что это файл и что он заканчивается на .bin
      return pathname.isFile() && pathname.getName().endsWith(".bin");
    }
  }
  class HexFileFilter implements FileFilter {
    public boolean accept(File pathname)
    {
      // проверям что это файл и что он заканчивается на .hex
      return pathname.isFile() && pathname.getName().endsWith(".hex");
    }
  }

  public static void main(String[] args) throws IOException
  {
    BinHexConverter binHexConverter = new BinHexConverter();
    JCommander jCommander = new JCommander(binHexConverter);
    try {
      jCommander.parse(args);
    } catch (ParameterException ex) {
      jCommander.usage();
      return;
    }
    binHexConverter.run();

  }

  private void run() throws IOException {
    if ((hexPath!=null) && (binPath!=null))
    {
      if (convertType!=null && convertType.equals("toHex"))
      {
        binFilesConvertToHexFiles();
      }

    } else {
      if ((binPath!=null)&& destinationFile!=null){
        // convertType = "toHex";
        binFilesConvertToHexDestinationFile();

      }
    }
  }


}
