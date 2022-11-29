package com.skyblock.skyblock.utilities.sign;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class SignReflection {

    public static <T> T getValue(@NotNull Object instance, @NotNull String field) {
        return getField(findField(instance.getClass(), field), instance);
    }

    private static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, clazz);
    }

    private static Field findField(Class<?> clazz, String name, Class<?> search) {
        Field[] fields = search.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().equals(name)) return field;
        }

        Class<?> superClass = search.getSuperclass();

        if (superClass != null) return findField(clazz, name, superClass);

        throw new ReflectionException("Cannot find field " + name + " in " + clazz);
    }

    private static <T> T getField(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (ReflectiveOperationException ex) {
            throw new ReflectionException(ex);
        }
    }

    public static final class ReflectionException extends RuntimeException {

        private ReflectionException(String arg0) {
            super(arg0);
        }

        public ReflectionException(Throwable arg0) {
            super(arg0);
        }
    }

}
