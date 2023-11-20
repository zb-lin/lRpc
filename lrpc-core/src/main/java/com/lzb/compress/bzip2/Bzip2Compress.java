package com.lzb.compress.bzip2;


import com.lzb.compress.Compress;
import com.lzb.exception.RpcException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;


public class Bzip2Compress implements Compress {


    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BZip2CompressorOutputStream bZip2CompressorOutputStream = new BZip2CompressorOutputStream(out)) {
            bZip2CompressorOutputStream.write(bytes);
            bZip2CompressorOutputStream.close();
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
             ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             BZip2CompressorInputStream bZip2CompressorInputStream = new BZip2CompressorInputStream(in)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = bZip2CompressorInputStream.read(buffer)) >= 0) {
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
        Bzip2Compress bzip2Compress = new Bzip2Compress();
        byte[] compress = bzip2Compress.compress(bytes);
        System.out.println(compress.length);
        byte[] decompress = bzip2Compress.decompress(compress);
        System.out.println(decompress.length);
    }
}
