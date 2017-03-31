package com.edward.sanctuary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import static com.edward.sanctuary.R.layout.card;

/**
 * Created by edward on 3/30/17.
 */

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<Card> cardList;
    protected Context context;

    protected final int VIEW_ITEM = 0;
    protected final int VIEW_LOADING = 1;
    protected boolean isLoading;
    protected OnMoreLoadListener mOnLoadMoreListener;

    public CardAdapter(List<Card> cardList, Context context){
        this.context = context;
        this.cardList = cardList;
        isLoading = false;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_ITEM) {
            return onCreateViewItemHolder(viewGroup, i);
        } else if (i == VIEW_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(card, viewGroup, false);
        itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
        CardViewHolder cH = createCardViewHolder(itemView);

        return cH;
    }

    public CardViewHolder createCardViewHolder(View itemView){
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof CardViewHolder) {
            CardAdapter.CardViewHolder cardViewHolder = (CardAdapter.CardViewHolder)viewHolder;
            Card ci = cardList.get(i);
            cardViewHolder.vName.setText(ci.getCard_name());
            cardViewHolder.vDescription.setText(ci.getCard_description());
            onBindCardViewHolderExtras(viewHolder, i);
        }
        else{
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
    protected void onBindCardViewHolderExtras(RecyclerView.ViewHolder viewHolder, int i){
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

    protected class CardViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vDescription;
        protected View itemView;

        public CardViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            vName = (TextView)itemView.findViewById(R.id.textView5);
            vDescription = (TextView)itemView.findViewById(R.id.textView6);
        }
    }

}
