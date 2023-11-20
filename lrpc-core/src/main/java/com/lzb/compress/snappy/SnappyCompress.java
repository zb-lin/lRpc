package com.lzb.compress.snappy;


import com.lzb.compress.Compress;
import lombok.SneakyThrows;
import org.xerial.snappy.Snappy;


public class SnappyCompress implements Compress {


    @Override
    @SneakyThrows
    public byte[] compress(byte[] bytes) {
        return Snappy.compress(bytes);
    }

    @Override
    @SneakyThrows
    public byte[] decompress(byte[] bytes) {
        return Snappy.uncompress(bytes);
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[]{1, 1, 99, 111, 109, 46, 108, 122, 98, 46, 72, 101, 108, 108, 111, 83,
                101, 114, 118, 105, 99, -27, 1, 104, 101, 108, 108, -17, 1, 2, 1, 13, 0, 1, 0, 91, 76, 106,
                97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103, -69, 1, 3, 1, 49, 49,
                -79, 1, 50, 50, -78, 1, 55, 97, 97, 51, 51, 50, 99, 55, 45, 102, 48, 102, 56, 45, 52, 48,
                97, 55, 45, 98, 97, 97, 51, 45, 101, 102, 97, 53, 98, 97, 97, 56, 99, 98, 54, -31};
        System.out.println(bytes.length);
        SnappyCompress snappyCompress = new SnappyCompress();
        byte[] compress = snappyCompress.compress(bytes);
        System.out.println(compress.length);
        byte[] decompress = snappyCompress.decompress(compress);
        System.out.println(decompress.length);
    }
}
