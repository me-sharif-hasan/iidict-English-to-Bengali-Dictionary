package com.iishanto;

import javafx.application.Platform;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Audio player for text-to-speech functionality
 * Handles playing audio from URLs and managing playback queue
 */
public class AudioPlayer {
    private static final int MAX_CHARS_PER_REQUEST = 170;
    private Player currentPlayer;
    private boolean isPlaying = false;
    private Thread playbackThread;

    /**
     * Break text into chunks suitable for TTS, respecting sentence boundaries
     * @param text The text to break
     * @param sentenceEnders Characters that mark sentence endings for the language
     * @return List of text chunks
     */
    public List<String> breakTextIntoChunks(String text, String sentenceEnders) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        String[] sentences = text.split("(?<=[" + Pattern.quote(sentenceEnders) + "])\\s*");
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;

            // If adding this sentence exceeds the limit, save current chunk and start new one
            if (currentChunk.length() > 0 && currentChunk.length() + sentence.length() + 1 > MAX_CHARS_PER_REQUEST) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }

            // If a single sentence is too long, break it by words
            if (sentence.length() > MAX_CHARS_PER_REQUEST) {
                String[] words = sentence.split("\\s+");
                for (String word : words) {
                    if (currentChunk.length() + word.length() + 1 > MAX_CHARS_PER_REQUEST) {
                        if (currentChunk.length() > 0) {
                            chunks.add(currentChunk.toString().trim());
                            currentChunk = new StringBuilder();
                        }
                    }
                    if (currentChunk.length() > 0) {
                        currentChunk.append(" ");
                    }
                    currentChunk.append(word);
                }
            } else {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * Play text using TTS
     * @param text The text to speak
     * @param languageCode The language code (e.g., "en", "bn")
     * @param sentenceEnders Characters that mark sentence endings
     * @param onComplete Callback when playback completes
     * @param onError Callback when an error occurs
     */
    public void playText(String text, String languageCode, String sentenceEnders, Runnable onComplete, java.util.function.Consumer<String> onError) {
        if (isPlaying) {
            stop();
        }

        List<String> chunks = breakTextIntoChunks(text, sentenceEnders);
        if (chunks.isEmpty()) {
            if (onComplete != null) {
                Platform.runLater(onComplete);
            }
            return;
        }

        isPlaying = true;
        playbackThread = new Thread(() -> {
            try {
                for (String chunk : chunks) {
                    if (!isPlaying) break;

                    String url = Tools.getConfig().getTtsUrl(chunk, languageCode);
                    playChunkSync(url);

                    // Small delay between chunks
                    if (isPlaying) {
                        Thread.sleep(100);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error playing audio: " + e.getMessage());
                if (onError != null) {
                    Platform.runLater(() -> onError.accept(e.getMessage()));
                }
            } finally {
                isPlaying = false;
                if (onComplete != null) {
                    Platform.runLater(onComplete);
                }
            }
        });
        playbackThread.start();
    }

    /**
     * Play a single audio chunk synchronously using JLayer MP3 player
     */
    private void playChunkSync(String urlString) throws Exception {
        URL url = new URL(urlString);

        try (InputStream audioStream = new BufferedInputStream(url.openStream())) {
            currentPlayer = new Player(audioStream);
            currentPlayer.play();
            currentPlayer.close();
            currentPlayer = null;
        } catch (Exception e) {
            System.err.println("Error playing chunk: " + e.getMessage());
            if (currentPlayer != null) {
                try {
                    currentPlayer.close();
                } catch (Exception ex) {
                    // Ignore
                }
                currentPlayer = null;
            }
            throw e;
        }
    }

    /**
     * Stop current playback
     */
    public void stop() {
        isPlaying = false;
        if (currentPlayer != null) {
            try {
                currentPlayer.close();
                currentPlayer = null;
            } catch (Exception e) {
                System.err.println("Error stopping player: " + e.getMessage());
            }
        }
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }
    }

    /**
     * Check if audio is currently playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}
