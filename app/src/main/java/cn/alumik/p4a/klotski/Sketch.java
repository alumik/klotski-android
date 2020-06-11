package cn.alumik.p4a.klotski;

import java.util.ArrayList;

import processing.core.PApplet;

public class Sketch extends PApplet {
    static final int GAME_W = 4;
    static final int GAME_H = 5;

    int SCALE;

    private ArrayList<Block> mBlocks = new ArrayList<>();
    private Block mBlock;

    boolean[][] grid = new boolean[GAME_W][GAME_H];

    public void settings() {
        final int screenWidth = displayWidth - displayWidth % 4;
        size(screenWidth, screenWidth / 4 * 5, P2D);
        SCALE = screenWidth / 4;
    }

    public void setup() {
        mBlocks.add(new Block(this, 1, 0, 2, 2, true));
        mBlocks.add(new Block(this, 0, 0, 1, 2, false));
        mBlocks.add(new Block(this, 3, 0, 1, 2, false));
        mBlocks.add(new Block(this, 0, 2, 1, 2, false));
        mBlocks.add(new Block(this, 1, 2, 2, 1, false));
        mBlocks.add(new Block(this, 3, 2, 1, 2, false));
        mBlocks.add(new Block(this, 1, 3, 1, 1, false));
        mBlocks.add(new Block(this, 2, 3, 1, 1, false));
        mBlocks.add(new Block(this, 0, 4, 1, 1, false));
        mBlocks.add(new Block(this, 3, 4, 1, 1, false));
    }

    public void draw() {
        background(38, 191, 191);
        for (final Block block : mBlocks) {
            block.show();
        }
    }

    public void touchStarted() {
        for (final Block block : mBlocks) {
            if (block.contains(touches[0].x, touches[0].y)) {
                mBlock = block;
            }
        }
        if (mBlock != null) {
            mBlock.touchStarted();
        }
    }

    public void touchMoved() {
        if (mBlock != null) {
            mBlock.touchMoved();
        }
    }

    public void touchEnded() {
        if (mBlock != null) {
            mBlock.touchEnded();
            mBlock = null;
        }
    }
}
