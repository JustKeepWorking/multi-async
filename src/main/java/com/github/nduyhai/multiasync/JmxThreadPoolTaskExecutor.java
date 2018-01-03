package com.github.nduyhai.multiasync;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ManagedResource
public class JmxThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @ManagedAttribute
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    @ManagedAttribute
    public void setCorePoolSize(int corePoolSize) {
        super.setCorePoolSize(corePoolSize);
    }

    @ManagedAttribute
    public int getMaxPoolSize() {
        return super.getMaxPoolSize();
    }

    @ManagedAttribute
    public void setMaxPoolSize(int maxPoolSize) {
        super.setMaxPoolSize(maxPoolSize);
    }

    @ManagedAttribute
    public int getKeepAliveSeconds() {
        return super.getKeepAliveSeconds();
    }

    @ManagedAttribute
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        super.setKeepAliveSeconds(keepAliveSeconds);
    }

    @ManagedAttribute
    public int getQueueCapacity() {
        return this.getThreadPoolExecutor().getQueue().size();
    }

    @ManagedAttribute
    public void setQueueCapacity(int queueCapacity) {
        super.setQueueCapacity(queueCapacity);
    }

    @ManagedAttribute
    public int getActiveCount() {
        return super.getActiveCount();
    }

    @ManagedAttribute
    public int getPoolSize() {
        return super.getPoolSize();
    }
}