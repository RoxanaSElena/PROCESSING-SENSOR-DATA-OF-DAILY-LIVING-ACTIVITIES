package project;

import java.time.Duration;
import java.time.LocalDateTime;

public class MonitoredData {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String activity;

    public MonitoredData(LocalDateTime startTime, LocalDateTime endTime, String activity) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = new String(activity);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate(){
        return startTime.getYear() + "-" + startTime.getMonthValue() + "-" + startTime.getDayOfMonth();
    }

    public long getDuration() {
        return Duration.between(this.getStartTime(), this.getEndTime()).toMinutes();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MonitoredData other = (MonitoredData) obj;
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!getDate().equals(other.getDate())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "Activity: " + this.activity + " / Start time: " + startTime +" / End time: " + endTime;

    }
}
