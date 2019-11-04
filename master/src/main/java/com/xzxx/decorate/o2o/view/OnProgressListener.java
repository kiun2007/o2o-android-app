package com.xzxx.decorate.o2o.view;

public interface OnProgressListener {
    public void moveStartProgress(float dur);

    public void durProgressChange(float dur);

    public void moveStopProgress(float dur);

    public void setDurProgress(float dur);
}
