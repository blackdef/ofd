package ru.astralnalog.jmeter.tcp;

import org.apache.jmeter.protocol.tcp.sampler.BinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Created by plotnikov on 11.10.2016.
 */
public class OfdTCPClient extends BinaryTCPClientImpl {

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

      String s = new String(bytes);
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

