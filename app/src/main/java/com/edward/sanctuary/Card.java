package com.edward.sanctuary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class Card implements Serializable{
    private String card_name;
    private String card_description;
    private long creation_date;
    private long card_id;

    public Card(String name, String desc, long date, long id){
        card_name = name;
        card_description = desc;
        creation_date = date;
        card_id = id;
    }
    public Card(){

    }

    public long getCard_id(){
        return card_id;
    }
    public void setCard_id(long id){
        card_id = id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_description() {
        return card_description;
    }

    public void setCard_description(String card_description) {
        this.card_description = card_description;
    }

    public static List<Card> createList(int size) {

        List<Card> result = new ArrayList<Card>();
        for (int i=1; i <= size; i++) {
            Card ci = new Card();
            ci.setCard_name("Name " + String.valueOf(i));
            ci.setCard_description(String.valueOf(i) + " Description");

            result.add(ci);

        }

        return result;
    }
    public boolean equals(Object card){
        Card c = (Card)card;
        if(this.getCard_id() == c.getCard_id()){
            return true;
        }
        return false;
    }

}
