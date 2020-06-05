public class ChangeRecorder {

    @Subscribe
    public void recordChange(ChangeEvent event) {
        System.out.println("Change Recorder: " + event.getName());
    }
}
