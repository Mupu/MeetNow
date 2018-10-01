package me.mupu.sessionHandler;

import org.apache.http.util.Args;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyHttpContext<U> {
    private final U contextNotFound;
    private final Map<String, U> map;

    public MyHttpContext(U contextNotFound) {
        this.map = new ConcurrentHashMap<>();
        this.contextNotFound = contextNotFound;
    }

    public U getAttribute(final String id) {
        Args.notNull(id, "Id");
        U U = this.map.get(id);
        if (U == null && this.contextNotFound != null) {
            U = this.contextNotFound;
        }
        return U;
    }

    public void setAttribute(final String id, final U U) {
        Args.notNull(id, "Id");
        if (U != null) {
            this.map.put(id, U);
        } else {
            this.map.remove(id);
        }
    }

    public U removeAttribute(final String id) {
        Args.notNull(id, "Id");
        return this.map.remove(id);
    }

    public void clear() {
        this.map.clear();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

}

