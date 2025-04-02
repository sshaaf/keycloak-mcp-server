package org.acme.experimental.formatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KeycloakRepresentationFormatter {

    public static String LIST_SEPARATOR = "---";

    public static <T> String format(T input) {
        if (input instanceof List<?> listInput) {
            return formatObjectList(listInput);
        } else if (input != null) {
            return formatObject(input);
        }
        throw new RuntimeException("Input is null.");
    }




    public static <T> String formatObjectList(List<T> objects) {
        return objects.stream()
                .map(KeycloakRepresentationFormatter::formatObject)
                .collect(Collectors.joining(LIST_SEPARATOR));
    }

    public static <T> String formatObject(T obj) {
        StringBuilder formattedOutput = new StringBuilder();
        Class<?> objClass = obj.getClass();

        Method[] methods = objClass.getMethods();

        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method method : methods) {
            String methodName = method.getName();

            // skip @JsonIgnore since its likely not interesting for us here.
            if(method.isAnnotationPresent(JsonIgnore.class)){
                continue;
            }

            if (methodName.startsWith("get") &&
                    methodName.length() > 3 &&
                    method.getParameterCount() == 0 &&
                    !method.getReturnType().equals(void.class) &&
                    !methodName.equals("getClass")) { // Skip Object.getClass()

                String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

                try {
                    Object propertyValue = method.invoke(obj);

                    // skip nulls
                    if (propertyValue != null) {
                        formattedOutput.append(propertyName).append(": ").append(propertyValue).append("\n");
                    }
                } catch (Exception e) {
                    formattedOutput.append(propertyName).append(": [error accessing]\n");
                }
            }

            else if (methodName.startsWith("is") &&
                    methodName.length() > 2 &&
                    method.getParameterCount() == 0 &&
                    (method.getReturnType().equals(boolean.class) ||
                            method.getReturnType().equals(Boolean.class))) {

                String propertyName = methodName.substring(2, 3).toLowerCase() + methodName.substring(3);

                try {
                    Object propertyValue = method.invoke(obj);

                    if (method.getReturnType().equals(boolean.class)) {
                        formattedOutput.append(propertyName).append(": ").append(propertyValue).append("\n");
                    }
                } catch (Exception e) {
                    formattedOutput.append(propertyName).append(": [error accessing]\n");
                }
            }
        }

        return formattedOutput.toString();
    }

}
