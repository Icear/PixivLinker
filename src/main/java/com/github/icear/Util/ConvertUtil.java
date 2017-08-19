package com.github.icear.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by icear.
 */
public class ConvertUtil {
    private static final int BUFFER_SIZE = 2048;

    /**
     * 将InputStream转换成byte数组
     * @param inputStream inputStream
     * @return byte数组
     * @throws IOException IOException
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = inputStream.read(data,0,BUFFER_SIZE)) != -1)
            byteArrayOutputStream.write(data, 0, count);
        data = null;

        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
