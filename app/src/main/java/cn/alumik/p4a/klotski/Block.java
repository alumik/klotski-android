package cn.alumik.p4a.klotski;

import processing.core.PApplet;
import processing.core.PVector;

class Block {
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static final boolean HORIZONTAL = true;
    private static final boolean VERTICAL = false;
    private static final int COLOR_MAIN = 0xfffc6170;
    private static final int COLOR_NORMAL = 0xffffd747;

    private Sketch mSketch;
    private PVector mPos;
    private PVector mSize;
    private int mColor;
    private PVector mMouseOffset;
    private int[] mMoveLimits;
    private boolean mDetectionFlag;

    Block(final Sketch sketch, final int x, final int y,
          final int w, final int h, final boolean mainBlock) {
        mSketch = sketch;
        mPos = new PVector(x, y);
        mSize = new PVector(w, h);
        mColor = mainBlock ? COLOR_MAIN : COLOR_NORMAL;
        setGrid(true);
    }

    boolean contains(final float x, final float y) {
        return x >= mPos.x * mSketch.SCALE
                && x < (mPos.x + mSize.x) * mSketch.SCALE
                && y >= mPos.y * mSketch.SCALE
                && y < (mPos.y + mSize.y) * mSketch.SCALE;
    }

    void show() {
        mSketch.stroke(0);
        mSketch.strokeWeight(3 * mSketch.displayDensity);
        mSketch.fill(mColor);
        mSketch.rect(mPos.x * mSketch.SCALE, mPos.y * mSketch.SCALE,
                mSize.x * mSketch.SCALE, mSize.y * mSketch.SCALE);
    }

    private void setGrid(final boolean state) {
        for (int i = 0; i < mSize.x; i++) {
            for (int j = 0; j < mSize.y; j++) {
                mSketch.grid[(int) mPos.x + i][(int) mPos.y + j] = state;
            }
        }
    }

    private void getMoveLimit(final int start, final int size, final int loopDir,
                              final boolean orientation, final int index,
                              final int dir, final int offset) {
        for (int i = start; i >= -1 && i <= size; i += loopDir) {
            if (i < 0
                    || i >= size
                    || (orientation ? mSketch.grid[i][index] : mSketch.grid[index][i])) {
                final int move = i + offset - loopDir;
                if (loopDir > 0 == move < mMoveLimits[dir]) {
                    mMoveLimits[dir] = move;
                }
                return;
            }
        }
    }

    private void getMoveLimitPair(final int start, final int size, final int loopDir,
                                  final boolean orientation, final int indexCeil,
                                  final int indexFloor, final int dir, final int offset) {
        getMoveLimit(start, size, loopDir, orientation, indexCeil, dir, offset);
        if (indexCeil != indexFloor) {
            getMoveLimit(start, size, loopDir, orientation, indexFloor, dir, offset);
        }
    }

    private void getMoveLimits() {
        mMoveLimits = new int[]{0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE};
        for (int x = 0; x < mSize.x; x++) {
            for (int y = 0; y < mSize.y; y++) {
                final PVector relativePos = new PVector(mPos.x + x, mPos.y + y);
                final PVector ceilPos =
                        new PVector(PApplet.ceil(relativePos.x), PApplet.ceil(relativePos.y));
                final PVector floorPos =
                        new PVector(PApplet.floor(relativePos.x), PApplet.floor(relativePos.y));
                final PVector roundPos =
                        new PVector(PApplet.round(relativePos.x), PApplet.round(relativePos.y));
                this.getMoveLimitPair((int) roundPos.x, Sketch.GAME_W, -1, HORIZONTAL,
                        (int) ceilPos.y, (int) floorPos.y, LEFT, 0);
                this.getMoveLimitPair((int) roundPos.x, Sketch.GAME_W, 1, HORIZONTAL,
                        (int) ceilPos.y, (int) floorPos.y, RIGHT, 1 - (int) mSize.x);
                this.getMoveLimitPair((int) roundPos.y, Sketch.GAME_H, -1, VERTICAL,
                        (int) ceilPos.x, (int) floorPos.x, UP, 0);
                this.getMoveLimitPair((int) roundPos.y, Sketch.GAME_H, 1, VERTICAL,
                        (int) ceilPos.x, (int) floorPos.x, DOWN, 1 - (int) mSize.y);
            }
        }
    }

    private void update() {
        if (mSize.x == 1 && mSize.y == 1) {
            if (mDetectionFlag) {
                getMoveLimits();
            }
            mDetectionFlag = PApplet.abs(mPos.x - PApplet.round(mPos.x)) +
                    PApplet.abs(mPos.y - PApplet.round(mPos.y)) == 0;
        } else {
            if (mDetectionFlag) {
                getMoveLimits();
                mDetectionFlag = false;
            }
        }
    }

    void touchStarted() {
        mDetectionFlag = true;
        mMouseOffset = PVector.sub(new PVector(mSketch.touches[0].x / mSketch.SCALE,
                mSketch.touches[0].y / mSketch.SCALE), mPos);
        setGrid(false);
    }

    void touchMoved() {
        update();
        mPos.x = PApplet.constrain(mSketch.touches[0].x / mSketch.SCALE - mMouseOffset.x,
                mMoveLimits[LEFT], mMoveLimits[RIGHT]);
        update();
        mPos.y = PApplet.constrain(mSketch.touches[0].y / mSketch.SCALE - mMouseOffset.y,
                mMoveLimits[UP], mMoveLimits[DOWN]);
    }

    void touchEnded() {
        mPos.x = PApplet.round(mPos.x);
        mPos.y = PApplet.round(mPos.y);
        setGrid(true);
    }
}
