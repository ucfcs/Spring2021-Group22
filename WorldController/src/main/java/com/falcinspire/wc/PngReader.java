package com.falcinspire.wc;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PngReader {
    public boolean[][] generateBlockedFromFile(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();

        boolean[][] grid = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >> 24) & 0xFF; 
                int r = (argb >> 16) & 0xFF; 
                int g = (argb >> 8) & 0xFF; 
                int b = (argb >> 0) & 0xFF; 
                grid[y][x] = a == 255 && r == 0 & g == 0 & b == 0;
            }
        }
        
        return grid;
    }
}