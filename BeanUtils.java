package com.company;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by denisizmailov on 21/12/2016.
 */
public class BeanUtils {

    public static void assign(Object to, Object from) {
        Map<String, Method> getters = new HashMap<>();
        Class classFrom = from.getClass();
        Class classTo = to.getClass();
        Method[] methodsFrom = classFrom.getMethods();
        Method[] methodsTo = classTo.getMethods();
        for (Method method : methodsFrom) {
            if (isGetter(method)) getters.put(method.getName().substring(3), method);
        }
        for (Method method : methodsTo) {
            String key = method.getName().substring(3);
            if (isSetter(method) && getters.containsKey(key) && isCompatible(method, getters.get(key))) {
                try {
                    method.invoke(to, getters.get(key).invoke(from));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isGetter(Method method) {
        return method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !void.class.equals(method.getReturnType());
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set") && method.getParameterTypes().length == 1;
    }

    private static boolean isCompatible(Method setter, Method getter) {
        Class class_ = getter.getReturnType();
        while (class_ != null) {
            if (class_ == setter.getParameterTypes()[0]) return true;
            class_ = class_.getSuperclass();
        }
        return false;
    }
}
