package ru.astralnalog.jmeter.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by plotnikov on 27.04.2017.
 */
public class Examples {
  public static void main(String[] args) throws IOException {
    String baseDir = new File(".").getCanonicalPath();
    System.out.println(baseDir);
    File f = new File(baseDir +File.separator+ "01.bin");
    System.out.println(baseDir +File.separator+f.getName().split("[.]")[0]+".hex");

  }
}
