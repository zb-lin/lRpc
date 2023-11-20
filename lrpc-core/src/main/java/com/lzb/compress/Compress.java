package com.lzb.compress;


import com.lzb.serviceloader.SPI;


@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
