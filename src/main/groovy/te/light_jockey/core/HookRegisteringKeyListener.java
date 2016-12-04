package te.light_jockey.core;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public abstract class HookRegisteringKeyListener implements NativeKeyListener {

    public HookRegisteringKeyListener(){
        setJNativeHookLoggingLevel(Level.OFF);
        registerInputListenerHook(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        // Terminate Global Key Hook if escape is pressed
        if(nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            shutdownCallback();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        // Do nothing
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        // Do nothing
    }

    private void shutdownCallback(){
        System.out.println("Shutdown called.");
        unregisterInputListenerHook(this);
        System.exit(0);
    }

    private void setJNativeHookLoggingLevel(Level level) {
        LogManager.getLogManager().reset();

        // Get the logger for "org.jnativehook" and set the level
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(level);

        // Change the level for all handlers attached to the default logger.
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(level);
        }

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }

    public void registerInputListenerHook(NativeKeyListener keyListener) {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(keyListener);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    public void unregisterInputListenerHook(NativeKeyListener keyListener) {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeKeyListener(keyListener);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}