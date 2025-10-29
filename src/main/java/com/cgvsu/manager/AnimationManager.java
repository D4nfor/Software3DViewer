package com.cgvsu.manager;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class AnimationManager {
    private final Timeline timeline;
    
    public AnimationManager(Runnable renderCallback) {
        this.timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        
        KeyFrame frame = new KeyFrame(Duration.millis(16), e -> {
            if (renderCallback != null) {
                renderCallback.run();
            }
        });
        
        timeline.getKeyFrames().add(frame);
    }
    
    public void start() { 
        timeline.play(); 
    }
    
    public void stop() { 
        timeline.stop(); 
    }
    
    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }
}