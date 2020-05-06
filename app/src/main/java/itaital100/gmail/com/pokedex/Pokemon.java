package itaital100.gmail.com.pokedex;

public class Pokemon {
    private String name;
    private String url;
    private int number;

    Pokemon(String name, String url, int number){
        this.name = name;
        this.url = url;
        this.number = number;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

    public int getNumber(){ return number;}

}
