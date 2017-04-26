package ru.astralnalog.jmeter.tcp;

import org.apache.jmeter.protocol.tcp.sampler.BinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import ru.astralnalog.jmeter.helpers.BinHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;



public class OfdTCPClient extends BinaryTCPClientImpl {

  /**
   * Перегрузка стандартного метода чтения из входного потока бинарных данных.
   * Возвращает строку в виде Hex от входного бинарного потока.
   */
  @Override
  public String read(InputStream is) throws ReadException {
    try {
      int x = 0;
      byte[] buffer = new byte[16384];
      x = is.read(buffer);
      if (x < 0) {
        x = is.read(buffer);
        if (x < 0) {
          String s = Integer.toString(x);
          return s;
        }
      }
      byte[] bytes = Arrays.copyOfRange(buffer, 0, x);

      String s = BinHelper.byteArrayToHexString(bytes);
      return s;
    }
    catch (SocketTimeoutException e) {
      return "SHIT. Socket Timeout Exception. " + e.toString();
    }
    catch (InterruptedIOException e) {
      return "SHIT. Interrupted IOException. " + e.toString();
    }
    catch (IOException e) {
      return e.toString();
    }
  }
}

