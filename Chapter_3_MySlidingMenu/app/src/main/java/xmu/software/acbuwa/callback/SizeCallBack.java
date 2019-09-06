package xmu.software.acbuwa.callback;

public interface SizeCallBack {

    public void onGlobalLayout();

    public void getViewSize(int idx, int width, int height, int[] dims);
}
