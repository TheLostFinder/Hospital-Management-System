package model.users;

/*
 User ek abstract base class hai jo system ke tamam users ke liye
 common attributes aur behaviour define karta hai. Patient, Doctor
 aur AdminStaff sab is class ko extend karte hain. Tamam fields
 private hain taake direct access na ho sake, yeh encapsulation
 ka basic principle hai.
 */
public abstract class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;

    public User(String userId, String name, String email, String password, String phone, String role) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.role     = role;
    }

    /*
     Har subclass apni marzi se apni display info return karta hai.
     Yeh abstraction ka example hai.
   */
    public abstract String getDisplayInfo();

    /*
     Har subclass apne role ke mutabiq menu items return karta hai.
    */
    public abstract String[] getMenuItems();

    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getPhone()  { return phone; }
    public String getRole()   { return role; }

    /*
     Password ko directly return nahi kiya jaata, sirf verify kiya
     jaata hai. Is tarah raw password kabhi expose nahi hota.
   */
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    public void setName(String name)            { this.name     = name; }
    public void setEmail(String email)          { this.email    = email; }
    public void setPhone(String phone)          { this.phone    = phone; }
    public void setPassword(String newPassword) { this.password = newPassword; }

    @Override
    public String toString() {
        return "[" + role + "] " + name + " (ID: " + userId + ")";
    }
}
