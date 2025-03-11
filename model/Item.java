package model;

public class Item
{
    public int nom;
    public int quality;
    //public  void upperQuality();
    public Item(int x)
    {
        nom = x;
    }

    public String getNom(){return "Item " + nom;}

}
