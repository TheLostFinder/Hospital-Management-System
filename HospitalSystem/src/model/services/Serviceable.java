package model.services;

/*
Serviceable interface hai jo tamam medical services ke liye
ek common contract define karta hai. Jo bhi class is interface
ko implement kare uske paas yeh sab methods hone chahiye.
Yeh polymorphism ka example hai kyunki har service apne
andaaz mein in methods ko implement karti hai.
*/
public interface Serviceable {

    String executeService();
    double getCost();
    String getServiceName();
    String getCategory();
    String getDescription();
}