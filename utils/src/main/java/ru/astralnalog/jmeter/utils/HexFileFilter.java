package ru.astralnalog.jmeter.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by plotnikov on 28.04.2017.
 */
class HexFileFilter implements FileFilter {
  public boolean accept(File pathname)
  {
    // проверям что это файл и что он заканчивается на .hex
    return pathname.isFile() && pathname.getName().endsWith(".hex");
  }
}
