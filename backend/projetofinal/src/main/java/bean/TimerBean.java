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
Communication communicationBean;

    @Schedule(/*second = "0", */minute = "*/30", hour = "*")
    public void automaticTimeout() {

        communicationBean.notifyContestManagersImportantActionNeeded();
        communicationBean.notifyProjectMembersContestApproachesEnding();


    }

}
