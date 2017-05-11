package ru.astralnalog.jmeter.tcp;

import org.apache.commons.io.IOUtils;
import org.apache.jmeter.protocol.tcp.sampler.BinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import org.apache.jorphan.util.JOrphanUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;



public class OfdTCPClient extends BinaryTCPClientImpl {

  /**
   * Перегрузка стандартного метода чтения из входного потока бинарных данных.
   * Возвращает строку в виде Hex от входного бинарного потока.
   */
  @Override
  public String read(InputStream is) throws ReadException {
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    try {
      int x = 0;
      byte[] buffer = new byte[4096];
      x = is.read(buffer);
      if (x < 0) {
        x = is.read(buffer);
        if (x < 0) {
          String s = Integer.toString(x);
          return s;
        }
      }
      IOUtils.closeQuietly(w);
      final String s = JOrphanUtils.baToHexString(w.toByteArray());
      //byte[] bytes = Arrays.copyOfRange(buffer, 0, x);
      //String s = BinHelper.byteArrayToHexString(bytes);
      return s;
    }
    catch (SocketTimeoutException e) {
      return "SHIT. Socket Timeout Exception. " + e.toString();
    }
    catch (InterruptedIOException e) {
      return "SHIT. Interrupted IOException. " + e.toString();
    }
    catch (IOException e) {
      throw new ReadException("", e, JOrphanUtils.baToHexString(w.toByteArray()));
    }
  }
}

