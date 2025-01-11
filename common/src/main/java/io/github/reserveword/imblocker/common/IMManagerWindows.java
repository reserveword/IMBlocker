package io.github.reserveword.imblocker.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

final class IMManagerWindows implements IMManager.PlatformIMManager {

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);
    
    private static native boolean ImmSetConversionStatus(WinNT.HANDLE himc, int fdwConversion, int fdwSentence);
    
    public static long lastIMStateOnTimestamp = System.currentTimeMillis();
    
    private final SetConversionStateThread setConversionStateThread;
    private boolean preferredEnglishState = false;

    static {
        Native.register("imm32");
    }

    private static final User32 u = User32.INSTANCE;
    
    public IMManagerWindows() {
		setConversionStateThread = new SetConversionStateThread();
		setConversionStateThread.start();
	}

    private static void makeOnImp() {
        WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    private static void makeOffImp() {
        WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc != null) {
            ImmDestroyContext(himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    @Override
    public void setState(boolean on) {
    	boolean state = ImmGetContext(u.GetActiveWindow()) != null;
        if (state != on) {
        	lastIMStateOnTimestamp = System.currentTimeMillis();
	        if (on) {
	            makeOnImp();
	        } else {
	            makeOffImp();
	        }
        }
    }
    
    @Override
    public void setEnglishState(boolean englishState) {
    	preferredEnglishState = englishState;
    	setConversionStateThread.awake();
    }
    
    private class SetConversionStateThread extends Thread {
    	
    	@Override
    	public void run() {
    		synchronized (this) {
				while(true) {
					await(0);
					
					while(true) {
						long cooldown = 60 - (System.currentTimeMillis() - lastIMStateOnTimestamp);
						if(cooldown <= 0) {
							MainThreadExecutor.instance.execute(() -> {
								WinDef.HWND hwnd = u.GetActiveWindow();
								if(ImmGetContext(hwnd) != null) {
						            WinNT.HANDLE himc = ImmGetContext(hwnd);
						            if(himc != null) {
						            	ImmSetConversionStatus(himc, preferredEnglishState ? 0 : 1, 0);
						            }
						            ImmReleaseContext(hwnd, himc);
						    	}
							});
							break;
						}else {
							await(cooldown);
						}
					}
				}
			}
    	}
    	
    	private synchronized void awake() {
    		notifyAll();
    	}
    	
    	private void await(long milis) {
    		try {
				wait(milis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
}
