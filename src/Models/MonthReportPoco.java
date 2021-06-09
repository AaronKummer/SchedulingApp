package Models;

public class MonthReportPoco {
    private String monthName;
    private String type;
    private int monthNum;
    private int appointmentsNum;

    public int getAppointmentsNum() {
        return this.appointmentsNum;
    }
    public String getMonthName() {
        switch(this.monthNum) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }

    public String getType() {
        return this.type;
    }


    public void setAppointmentsNum(int num) {
        this.appointmentsNum = num;
    }

    public int getMonthNum() {
        return this.monthNum;
    }

    public MonthReportPoco(int monthNum, String type) {
        this.monthNum = monthNum;
        this.type =  type;
    }
}
