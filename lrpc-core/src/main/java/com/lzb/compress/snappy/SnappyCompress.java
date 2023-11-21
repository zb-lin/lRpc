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
}
