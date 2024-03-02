/**
 * Copyright (c) 2008, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package biz.source_code.base64Coder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class Base64CoderTest {

  @Test
  public void testDecode() throws UnsupportedEncodingException {
    check("Aladdin:open sesame", "QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
    check("a", "YQ==");
    check("aa", "YWE=");
    check("a=", "YT0=");
    check("", "");
  }

  @Test
  public void testEncodeString(){
    List<String> key = List.of("apple","app.le", "orange", "mango");
    List<String> encodedKey = List.of("YXBwbGU=","YXBwLmxl", "b3Jhbmdl", "bWFuZ28=");

    for (int iter = 0; iter < key.size(); ++iter){
      assertEquals(Base64Coder.encodeString(key.get(iter)), encodedKey.get(iter));
    }
  }

  @Test
  public void testDecodeString(){
    List<String> encodedKey = List.of("YXBwbGU=", "b3Jhbmdl", "bWFuZ28=");
    List<String> key = List.of("apple", "orange", "mango");

    for (int iter = 0; iter < key.size(); ++iter){
      assertEquals(Base64Coder.decodeString(encodedKey.get(iter)), key.get(iter));
    }
  }

  @Test
  public void testThrowUnsupportedEncodingOnEncodeString(){

    String key = "apple";
    String providedCharSet = "UTF_8";
    Class<UnsupportedEncodingException> unsupportedEncodingExceptionClass = UnsupportedEncodingException.class;

    assertThrows(unsupportedEncodingExceptionClass, () -> {
      byte[] keyBytes = key.getBytes(providedCharSet);
      String encodedKey = Arrays.toString(Base64Coder.encode(keyBytes));
    });
  }

  @Test
  public void testFailure1() throws UnsupportedEncodingException {
    try {
      Base64Coder.decode("YQ=".toCharArray());
      fail();
    } catch (Exception e) {
      assertEquals("Length of Base64 encoded input string is not a multiple of 4.", e.getMessage());
    }
  }

  @Test
  public void testFailure2() throws UnsupportedEncodingException {
    checkInvalid("\tWE=");
    checkInvalid("Y\tE=");
    checkInvalid("YW\t=");
    checkInvalid("YWE\t");
    //
    checkInvalid("©WE=");
    checkInvalid("Y©E=");
    checkInvalid("YW©=");
    checkInvalid("YWE©");
  }


  private void checkInvalid(String encoded) {
    try {
      Base64Coder.decode(encoded.toCharArray());
      fail("Illegal chanracter.");
    } catch (Exception e) {
      assertEquals("Illegal character in Base64 encoded data.", e.getMessage());
    }
  }

  private void check(String text, String encoded) throws UnsupportedEncodingException {
    char[] s1 = Base64Coder.encode(text.getBytes(StandardCharsets.UTF_8));
    String t1 = new String(s1);
    assertEquals(encoded, t1);
    byte[] s2 = Base64Coder.decode(encoded.toCharArray());
    String t2 = new String(s2, StandardCharsets.UTF_8);
    assertEquals(text, t2);
  }
}
