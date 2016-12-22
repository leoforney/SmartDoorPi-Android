package tk.leoforney.doorreader;

public class Door {

    Door(){}

    public String name; // Human readable name
    public String codeName; // The codename for Firebase and SmartThings like PatioDoor
    public Boolean previous, current; // The previous and current value for door
    public String doorPin; // The pin for the GPIO of the door

    public void setCanonicalName(String name) {
        this.name = name + " Door";
        this.codeName = this.name.replaceAll(" ", "");
    }

    public String removeLastChar(String str) {
        return str.substring(0,str.length()-5);
    }

}
