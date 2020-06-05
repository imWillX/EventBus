import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventBus {

    private class Invoke {
        Object obj;
        Method method;
        public Invoke(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
        }

        @Override
        public String toString() {
            return ("(" + obj.getClass().getName() + "," + method.getName());
        }
    }

    // Mapping of the event to <Calling Class, Method>
    // ChangeEvent=[<ChangeRecorder, recordChange>]
    Map<Class<?>, List<Invoke>> methods;

    public EventBus() {
        methods = new ConcurrentHashMap<>();
    }

    // post(ChangeEvent) => ChangeRecorder.recordChange(ChangeEvent)
    public void post(Object object) {
        Class<?> clazz = object.getClass();
        if (methods.containsKey(clazz)) {
            methods.get(clazz).forEach(e -> {
                try {
                    e.method.invoke(e.obj, object);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    //
    public void register(Object object) {
        Class<?> clazz = object.getClass();
        List<Method> subscriptions = findSubscriptions(clazz);
        for (Method method: subscriptions) {
            Class<?> type = method.getParameterTypes()[0];
            Invoke invoke = new Invoke(object, method);
            if (methods.containsKey(type)) {
                methods.get(type).add(invoke);
            } else {
                List<Invoke> tmp = new ArrayList<>();
                tmp.add(invoke);
                methods.put(type, tmp);
            }
        }
    }

    private List<Method> findSubscriptions(Class<?> type) {
        List<Method> subscriptions = Arrays.stream(type.getMethods())
                .filter(method -> method.isAnnotationPresent(Subscribe.class))
                .collect(Collectors.toList());
        return subscriptions;
    }

    public Map<Class<?>, List<Invoke>> getMethods() {
        return methods;
    }
}
