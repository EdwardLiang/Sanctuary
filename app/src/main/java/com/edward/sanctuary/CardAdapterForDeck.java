package com.edward.sanctuary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import static com.edward.sanctuary.R.layout.card;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapterForDeck extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Card> cardList;
    private Context context;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;
    private boolean isLoading;
    private OnMoreLoadListener mOnLoadMoreListener;

    public CardAdapterForDeck(List<Card> cardList, Context context){
        this.cardList = cardList;
        this.context = context;
        isLoading = false;
    }

    public void setOnMoreLoadListener(OnMoreLoadListener m){
        this.mOnLoadMoreListener = m;
    }

    public OnMoreLoadListener getOnMoreLoadListener(){
        return mOnLoadMoreListener;
    }

    public void setCardList(List<Card> cards){
        cardList = cards;
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
            CardViewHolder cardViewHolder = (CardViewHolder)viewHolder;
            Card ci = cardList.get(i);
            cardViewHolder.vName.setText(ci.getCard_name());
            cardViewHolder.vDescription.setText(ci.getCard_description());
        }
        else{
            CardAdapter.LoadingViewHolder loadingViewHolder = (CardAdapter.LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_ITEM) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(card, viewGroup, false);
            itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
            CardViewHolder cH = new CardViewHolder(itemView);

            return cH;
        } else if (i == VIEW_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading, viewGroup, false);
            return new CardAdapter.LoadingViewHolder(view);
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

    public class CardViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vDescription;


        public CardViewHolder(View itemView) {
            super(itemView);
            vName = (TextView)itemView.findViewById(R.id.textView5);
            vDescription = (TextView)itemView.findViewById(R.id.textView6);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(cardList.get(pos).getCard_id() != -1) {
                        //-1 when the card is the "no more cards" card
                        Card card = cardList.get(pos);
                        Intent intent = new Intent(context, SelectCardsForDeck.class);
                        intent.putExtra("Card", (Serializable) card);
                        intent.putExtra("Intent", "AddDeck");
                        context.startActivity(intent);
                    }
                }
            });
        }
    }


}
