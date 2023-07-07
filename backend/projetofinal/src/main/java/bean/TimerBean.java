package bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

@Singleton
public class TimerBean {

    @EJB
    ConfigurationBean configBean;
    @Inject


    @Schedule(second = "0", minute = "0", hour = "*")
    public void automaticTimeout() {
        System.out.println("Timer ");

    }

}
