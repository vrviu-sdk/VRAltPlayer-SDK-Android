/*
 * Copyright (C) 2014 Sony Mobile Communications Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.viu.vrplayer.demo;

import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final String TAG = "Util";

    public final static String PLAY_READY_SYSTEM_ID = "9A04F07998404286AB92E65BE0885F95";

    public final static String MARLIN_SYSTEM_ID = "69F908AF481646EA910CCD5DCCCB0A3A";

    public final static String MARLIN_SUBTITLE_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final Pattern XS_DATE_TIME_PATTERN = Pattern.compile(
            "(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt]"
                    + "(\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?"
                    + "([Zz]|((\\+|\\-)(\\d\\d):?(\\d\\d)))?");

    public final static String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getPath();

    // common keys
    private final static String INIT_DATA_KEY_TITLE = "title";

    private final static String INIT_DATA_KEY_PROPERTIES = "properties";

    private final static String INIT_DATA_KEY_PROP_NAME = "name";

    private final static String INIT_DATA_KEY_PROP_VERSION = "version";

    private final static String INIT_DATA_KEY_PROCESSTYPE = "process_type";

    private final static String INIT_DATA_KEY_DATATYPE = "data_type";

    // IPMP
    private final static String INIT_DATA_KEY_IPMP = "ipmp";

    private final static String INIT_DATA_KEY_SINF = "sinf";

    // CENC
    private final static String INIT_DATA_KEY_CENC = "cenc";

    private final static String INIT_DATA_KEY_PSSH = "pssh";

    private final static String INIT_DATA_KEY_KIDS = "kids";

    // Common data
    private final static String INIT_DATA_TITLE = "marlincdm_initData";

    private static final String CURRENT_VERSION = "1.0";

    private static final String PROCESS_TYPE_ANDROID = "android";

    private static final String DATA_TYPE_CENC = "cenc";

    private static final String DATA_TYPE_IPMP = "ipmp";

    private static final String PROPERTY_NAME_INIT_DATA = "getkeyRequest_initdata";

    // Playback speed
    public static final float DEFAULT_PLAYBACK_SPEED = 1.0f;

    public static final float MIN_PLAYBACK_SPEED = 0.5f;

    public static final float MAX_PLAYBACK_SPEED = 2.0f;

    public static final int DEFAULT_MESSAGE_DELAY = 10;

    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, -1);
    }

    public static String bytesToHex(byte[] bytes, int offset, int length) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        if (bytes == null) {
            return null;
        }
        if (length < 0) {
            length = bytes.length;
        }
        if (offset + length > bytes.length) {
            return null;
        }
        char[] hexChars = new char[length * 2];

        for (int j = 0; j < length; j++) {
            int v = bytes[j + offset] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    /**
     * Closes a {@link Closeable}, suppressing any {@link IOException} that may occur. Both {@link
     * java.io.OutputStream} and {@link InputStream} are {@code Closeable}.
     *
     * @param closeable The {@link Closeable} to close.
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // Ignore.
        }
    }

    /**
     * Returns whether the given character is a carriage return ('\r') or a line feed ('\n').
     *
     * @param c The character.
     * @return Whether the given character is a linebreak.
     */
    public static boolean isLinebreak(int c) {
        return c == '\n' || c == '\r';
    }

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Parses an xs:dateTime attribute value, returning the parsed timestamp in milliseconds since
     * the epoch.
     *
     * @param value The attribute value to decode.
     * @return The parsed timestamp in milliseconds since the epoch.
     * @throws  if an error occurs parsing the dateTime attribute value.
     */
    public static long parseXsDateTime(String value){
        Matcher matcher = XS_DATE_TIME_PATTERN.matcher(value);
        if (!matcher.matches()) {

        }

        int timezoneShift;
        if (matcher.group(9) == null) {
            // No time zone specified.
            timezoneShift = 0;
        } else if (matcher.group(9).equalsIgnoreCase("Z")) {
            timezoneShift = 0;
        } else {
            timezoneShift = ((Integer.parseInt(matcher.group(12)) * 60
                    + Integer.parseInt(matcher.group(13))));
            if (matcher.group(11).equals("-")) {
                timezoneShift *= -1;
            }
        }

        Calendar dateTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

        dateTime.clear();
        // Note: The month value is 0-based, hence the -1 on group(2)
        dateTime.set(Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)) - 1,
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4)),
                Integer.parseInt(matcher.group(5)),
                Integer.parseInt(matcher.group(6)));
        if (!TextUtils.isEmpty(matcher.group(8))) {
            final BigDecimal bd = new BigDecimal("0." + matcher.group(8));
            // we care only for milliseconds, so movePointRight(3)
            dateTime.set(Calendar.MILLISECOND, bd.movePointRight(3).intValue());
        }

        long time = dateTime.getTimeInMillis();
        if (timezoneShift != 0) {
            time -= timezoneShift * 60000;
        }

        return time;
    }

    public static byte[] uuidStringToByteArray(String uuidString) {
        byte[] signed = new BigInteger(uuidString, 16).toByteArray();

        if (signed.length == 16) {
            return signed;
        }

        byte[] unsigned = new byte[uuidString.length() / 2];
        //BigInteger returns a signed byte array so we need to get rid of the signing.
        System.arraycopy(signed, 1, unsigned, 0, unsigned.length);
        return unsigned;
    }

    public static String getMarlinPSSHTable(byte[] pssh, byte[][] kids) throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject property = new JSONObject();
        JSONObject cenc = new JSONObject();
        JSONArray kidsArray = new JSONArray();

        root.put(INIT_DATA_KEY_TITLE, INIT_DATA_TITLE);
        property.put(INIT_DATA_KEY_PROP_NAME, PROPERTY_NAME_INIT_DATA);
        property.put(INIT_DATA_KEY_PROP_VERSION, CURRENT_VERSION);
        property.put(INIT_DATA_KEY_PROCESSTYPE, PROCESS_TYPE_ANDROID);
        property.put(INIT_DATA_KEY_DATATYPE, DATA_TYPE_CENC);

        for (byte[] kid : kids) {
            kidsArray.put(bytesToHex(kid));
        }
        cenc.put(INIT_DATA_KEY_PSSH, bytesToHex(pssh));
        cenc.put(INIT_DATA_KEY_KIDS, kidsArray);

        property.put(INIT_DATA_KEY_CENC, cenc);
        root.put(INIT_DATA_KEY_PROPERTIES, property);

        return root.toString();
    }

    public static String getJSONIPMPData(byte[] sinfData) throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject property = new JSONObject();
        JSONObject sinfJson = new JSONObject();

        root.put(INIT_DATA_KEY_TITLE, INIT_DATA_TITLE);
        property.put(INIT_DATA_KEY_PROP_NAME, PROPERTY_NAME_INIT_DATA);
        property.put(INIT_DATA_KEY_PROP_VERSION, CURRENT_VERSION);
        property.put(INIT_DATA_KEY_PROCESSTYPE, PROCESS_TYPE_ANDROID);
        property.put(INIT_DATA_KEY_DATATYPE, DATA_TYPE_IPMP);
        sinfJson.put(INIT_DATA_KEY_SINF, bytesToHex(sinfData));
        property.put(INIT_DATA_KEY_IPMP, sinfJson);
        root.put(INIT_DATA_KEY_PROPERTIES, property);

        return root.toString();
    }

    /**
     * Tests two objects for {@link Object#equals(Object)} equality, handling the case where one or
     * both may be null.
     *
     * @param o1 The first object.
     * @param o2 The second object.
     * @return {@code o1 == null ? o2 == null : o1.equals(o2)}.
     */
    public static boolean areEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

}
