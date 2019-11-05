import Scheduler.SchedulerExecutor;
import config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args)  {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!START!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SchedulerExecutor.start();
    }
}
