package com.edward.sanctuary;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class SelectCardsForDeckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SparseBooleanArray selectedItems;
    private List<Card> cardList;
    private HashMap<String, Card> alreadyInDeck;
    private Context context;
    private HashSet<Card> newlyUnselected;
    private boolean changed;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;
    private boolean isLoading;
    private OnMoreLoadListener mOnLoadMoreListener;


    public SelectCardsForDeckAdapter(HashMap<String, Card> inDeck, List<Card> cardList, Context context){
        alreadyInDeck = inDeck;
        this.context = context;
        this.cardList = cardList;
        this.newlyUnselected = new HashSet<Card>();
        changed = false;
        selectedItems = new SparseBooleanArray();
        isLoading = false;
    }
    public List<Card> getSelected(){
        List<Card> cL = new ArrayList<Card>();
        for(int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            cL.add(cardList.get(key));
        }
        return cL;
    }
    public HashSet<Card> getNewlyUnselected(){
        return newlyUnselected;
    }
    public boolean getChanged(){
        return changed;
    }
    public void clearSelected(){
        selectedItems.clear();
    }

    public void setCardList(List<Card> cards){
        cardList = cards;
    }

    public void setOnMoreLoadListener(OnMoreLoadListener m){
        this.mOnLoadMoreListener = m;
    }

    public OnMoreLoadListener getOnMoreLoadListener(){
        return mOnLoadMoreListener;
    }

    public void toggleSelection(int pos){
        System.out.println("Card " + pos + " added or removed from selected list");
        if(selectedItems.get(pos, false)){
            selectedItems.delete(pos);
        }
        else{
            selectedItems.put(pos, true);
        }
    }
    public boolean isLoading(){
        if(isLoading){
            return true;
        }
        return false;
    }
    public void setIsLoading(boolean bool){
        if(bool){
            isLoading = true;
        }
        else{
            isLoading = false;
        }
    }

    @Override
    public int getItemCount(){
        return cardList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof CardViewHolder) {
            System.out.println("Bound viewHolder for " + cardList.get(i).getCard_name());
            CardViewHolder cardViewHolder = (CardViewHolder)viewHolder;
            Card ci = cardList.get(i);
            cardViewHolder.vName.setText(ci.getCard_name());
            cardViewHolder.vDescription.setText(ci.getCard_description());
            if(alreadyInDeck.containsKey(ci.getCard_name())){
                selectedItems.put(i, true);
                System.out.println(ci.getCard_name() + " item pre-selected");
            }
            if (selectedItems.get(i, false)) {
                cardViewHolder.itemView.setSelected(true);
                if(Session.getInstance(context).darkModeSet()) {
                    cardViewHolder.itemView.setBackgroundColor(Color.BLACK);
                }
                else{
                    cardViewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                cardViewHolder.itemView.setSelected(false);
                if(Session.getInstance(context).darkModeSet()) {
                    cardViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
                }
                else{
                    cardViewHolder.itemView.setBackgroundColor(Color.WHITE);
                }
            }
        }
        else{
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_ITEM) {
            //System.out.println("created viewHolder for " + cardList.get(i).getCard_name());
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card, viewGroup, false);
            itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
            CardViewHolder cH = new CardViewHolder(itemView);
            /*if(alreadyInDeck.contains(cardList.get(i))){
                selectedItems.put(i, true);
                System.out.println(cardList.get(i).getCard_name() + " item pre-selected");
            }
            if (selectedItems.get(i, false)) {
                cH.itemView.setSelected(true);
                if(Session.getInstance(context).darkModeSet()) {
                    cH.itemView.setBackgroundColor(Color.BLACK);
                }
                else{
                    cH.itemView.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                cH.itemView.setSelected(false);
                if(Session.getInstance(context).darkModeSet()) {
                    cH.itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
                }
                else{
                    cH.itemView.setBackgroundColor(Color.WHITE);
                }
            }*/
            return cH;

        } else if (i == VIEW_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(cardList.get(position) == null){
            return VIEW_LOADING;
        }
        return VIEW_ITEM;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vDescription;
        protected View itemView;


        public CardViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            vName = (TextView)itemView.findViewById(R.id.textView5);
            vDescription = (TextView)itemView.findViewById(R.id.textView6);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() != -1) {
                        changed = true;
                        //-1 when the card is the "no more cards" card
                        if (v.isSelected()) {
                            System.out.println("unselected");
                            if(Session.getInstance(context).darkModeSet()) {
                                v.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
                            }
                            else{
                                v.setBackgroundColor(Color.WHITE);
                            }
                            newlyUnselected.add(cardList.get(pos));
                            if(alreadyInDeck.containsKey(cardList.get(pos).getCard_name())){
                                //System.out.println(cardList.get(pos).getCard_name() + "Added to unselected.");
                                alreadyInDeck.remove(cardList.get(pos).getCard_name());
                            }
                            toggleSelection(pos);
                            v.setSelected(false);
                        } else {
                            System.out.println("selected");
                            if(newlyUnselected.contains(cardList.get(pos))){
                                newlyUnselected.remove(cardList.get(pos));
                               // System.out.println(cardList.get(pos).getCard_name() + "Removed from unselected.");
                            }
                            if(Session.getInstance(context).darkModeSet()) {
                                v.setBackgroundColor(Color.BLACK);
                            }
                            else{
                                v.setBackgroundColor(Color.LTGRAY);
                            }
                            toggleSelection(pos);
                            v.setSelected(true);
                        }
                    }
                }
            });
        }
    }


}
