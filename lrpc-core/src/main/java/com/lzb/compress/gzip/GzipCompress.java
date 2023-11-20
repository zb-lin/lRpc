package com.lzb.compress.gzip;


import com.lzb.compress.Compress;
import com.lzb.compress.deflate.DeflateCompress;
import com.lzb.exception.RpcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GzipCompress implements Compress {


    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip compress failed", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gunzip.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip decompress failed", e);
        }
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[]{1, 1, 99, 111, 109, 46, 108, 122, 98, 46, 72, 101, 108, 108, 111, 83,
                101, 114, 118, 105, 99, -27, 1, 104, 101, 108, 108, -17, 1, 2, 1, 13, 0, 1, 0, 91, 76, 106,
                97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103, -69, 1, 3, 1, 49, 49,
                -79, 1, 50, 50, -78, 1, 55, 97, 97, 51, 51, 50, 99, 55, 45, 102, 48, 102, 56, 45, 52, 48,
                97, 55, 45, 98, 97, 97, 51, 45, 101, 102, 97, 53, 98, 97, 97, 56, 99, 98, 54, -31};
        System.out.println(bytes.length);
        GzipCompress gzipCompress = new GzipCompress();
        byte[] compress = gzipCompress.compress(bytes);
        System.out.println(compress.length);
        byte[] decompress = gzipCompress.decompress(compress);
        System.out.println(decompress.length);
    }
}
