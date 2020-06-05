public class Main {
    public static void main(String[] args) {
        EventBus bus = new EventBus();

        bus.register(new ChangeRecorder());

        bus.post(new ChangeEvent("Test"));
    }
}
