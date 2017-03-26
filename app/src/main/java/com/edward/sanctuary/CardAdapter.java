package com.edward.sanctuary;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SparseBooleanArray selectedItems;
    private List<Card> cardList;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;
    private boolean isLoading;
    private OnMoreLoadListener mOnLoadMoreListener;


    public CardAdapter(List<Card> cardList){
        this.cardList = cardList;
        selectedItems = new SparseBooleanArray();
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
            CardViewHolder cardViewHolder = (CardViewHolder)viewHolder;
            Card ci = cardList.get(i);
            cardViewHolder.vName.setText(ci.getCard_name());
            cardViewHolder.vDescription.setText(ci.getCard_description());
            if (selectedItems.get(i, false)) {
                cardViewHolder.itemView.setSelected(true);
                cardViewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                cardViewHolder.itemView.setSelected(false);
                cardViewHolder.itemView.setBackgroundColor(Color.WHITE);
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
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card, viewGroup, false);
            itemView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
            CardViewHolder cH = new CardViewHolder(itemView);
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
                    if(v.isSelected()){
                        System.out.println("unselected");
                        v.setBackgroundColor(Color.WHITE);
                        toggleSelection(pos);
                        v.setSelected(false);
                    }
                    else{
                        System.out.println("selected");
                        v.setBackgroundColor(Color.LTGRAY);
                        toggleSelection(pos);
                        v.setSelected(true);
                    }

                }
            });
        }
    }


}
