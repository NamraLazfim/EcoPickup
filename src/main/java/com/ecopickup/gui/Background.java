package com.ecopickup.gui;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

// Runs slow work (API calls) off the Swing thread so the window does not freeze.
final class Background {

    private static final String UNEXPECTED = "Something went wrong. Please try again.";

    private Background() {
    }

    static <T> void run(Callable<T> task, Consumer<T> onSuccess, Consumer<String> onError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (ExecutionException failure) {
                    Throwable cause = failure.getCause();
                    onError.accept(cause instanceof IllegalArgumentException ? cause.getMessage() : UNEXPECTED);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    onError.accept(UNEXPECTED);
                }
            }
        }.execute();
    }
}
