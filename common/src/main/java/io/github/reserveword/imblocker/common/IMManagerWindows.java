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

    @Override
    public void setState(boolean on) {
    	WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if ((himc != null) != on) {
	        if (on) {
	            himc = ImmCreateContext();
	            ImmAssociateContext(hwnd, himc);
	        	lastIMStateOnTimestamp = System.currentTimeMillis();
	        } else {
	            himc = ImmAssociateContext(hwnd, null);
	            ImmDestroyContext(himc);
	        }
            ImmReleaseContext(hwnd, himc);
        }
    }
    
    @Override
    public void setEnglishState(boolean englishState) {
    	preferredEnglishState = englishState;
    	if(!setConversionStateThread.isScheduled) {
	    	if(getConversionStatusCooldown() <= 0) {
	    		syncEnglishState();
	    	}else {
	    		setConversionStateThread.awake();
	    		setConversionStateThread.isScheduled = true;
	    	}
    	}
    }
    
    private void syncEnglishState() {
    	WinDef.HWND hwnd = u.GetActiveWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if(himc != null) {
        	ImmSetConversionStatus(himc, preferredEnglishState ? 0 : 1, 0);
        }
        ImmReleaseContext(hwnd, himc);
        setConversionStateThread.isScheduled = false;
    }
    
    private long getConversionStatusCooldown() {
    	return 60 - (System.currentTimeMillis() - lastIMStateOnTimestamp);
    }
    
    private class SetConversionStateThread extends Thread {
    	
    	private boolean isScheduled = false;
    	
    	@Override
    	public synchronized void run() {
    		while(true) {
				await(0);
				
				while(true) {
					long cooldown = getConversionStatusCooldown();
					if(cooldown <= 0) {
						MainThreadExecutor.instance.execute(() -> syncEnglishState());
						break;
					}else {
						await(cooldown);
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
