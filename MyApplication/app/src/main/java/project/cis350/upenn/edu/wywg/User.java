package project.cis350.upenn.edu.wywg;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by abhaved on 2/23/17.
 */

public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email){this.email = email;}

    public String getEmail(){ return email;}

    public void setSex(String email){this.sex = sex;}

    public String getSex(){ return sex;}

    public void setPhoneNumber(String number){this.number = number;}

    public String getPhoneNumber(){ return number;}

    String password;
    String name;
    Set<Location> locations;
    String email;
    String number;
    String sex;
    Boolean showEmail;
    Boolean showNumber;

    public User(String n, String p, Set<Location> s, String se, String e, String num) {
        password = p;
        name = n;
        locations = s;
        sex =se;
        email = e;
        number = num;
    }
    public User(String n, String p, Set<Location> s, String se, String e, String num, boolean shE, boolean shN ) {
        password = p;
        name = n;
        locations = s;
        sex =se;
        email = e;
        number = num;
        showEmail = shE;
        showNumber = shN;

    }
}
