package de.obsidiancloud.node.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SharedClassLoader extends URLClassLoader {
    private static final Set<SharedClassLoader> classLoaders = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();

    public SharedClassLoader(@NotNull ClassLoader parent, @NotNull URL... urls) {
        super(urls, parent);
        classLoaders.add(this);
    }

    public SharedClassLoader(@NotNull URL... urls) {
        super(urls);
        classLoaders.add(this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = internalLoadClass(name, resolve, true);
        classes.add(clazz);
        return clazz;
    }

    private Class<?> internalLoadClass(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException exception) {
            if (checkOther) {
                for (SharedClassLoader classLoader : classLoaders) {
                    try {
                        if (classLoader != this) {
                            return classLoader.internalLoadClass(name, resolve, false);
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
            throw exception;
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return internalGetResourceAsStream(name, true);
    }

    private @Nullable InputStream internalGetResourceAsStream(String name, boolean checkOther) {
        InputStream inputStream = super.getResourceAsStream(name);
        if (inputStream != null) {
            return inputStream;
        }
        if (checkOther) {
            for (SharedClassLoader classLoader : classLoaders) {
                if (classLoader != this) {
                    inputStream = classLoader.internalGetResourceAsStream(name, false);
                    if (inputStream != null) {
                        return inputStream;
                    }
                }
            }
        }
        return null;
    }

    public @NotNull Set<Class<?>> getClasses() {
        return classes;
    }
}
