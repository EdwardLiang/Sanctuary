package com.edward.sanctuary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by edward on 3/19/17.
 */

public class ManageDeckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SparseBooleanArray selectedItems;
    private SparseBooleanArray checkedItems;
    private List<Card> cardList;
    private Context context;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;
    private boolean isLoading;
    private OnMoreLoadListener mOnLoadMoreListener;

    public ManageDeckAdapter(List<Card> cardList, Context context){
        this.cardList = cardList;
        selectedItems = new SparseBooleanArray();
        checkedItems = new SparseBooleanArray();
        this.context = context;
        isLoading = false;
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

            if(selectedItems.get(i, false)){
                cardViewHolder.itemView.setSelected(true);
                cardViewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            else{
                cardViewHolder.itemView.setSelected(false);
                cardViewHolder.itemView.setBackgroundColor(Color.WHITE);
            }
            if(checkedItems.get(i, false)){
                cardViewHolder.switch1.setChecked(true);
            }
            else{
                cardViewHolder.switch1.setChecked(false);
            }
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
                    inflate(R.layout.card_with_checkbox, viewGroup, false);
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
        protected View itemView;
        protected Switch switch1;


        public CardViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            vName = (TextView)itemView.findViewById(R.id.textView5);
            vDescription = (TextView)itemView.findViewById(R.id.textView6);
            switch1 = (Switch)itemView.findViewById(R.id.switch1);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    Intent intent = new Intent(context, ManageCardsInDeck.class);
                    intent.putExtra("Card", cardList.get(pos));
                    context.startActivity(intent);
                }
            });
            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    System.out.println("switched");
                    int pos = getAdapterPosition();
                    if(isChecked) {
                        checkedItems.put(pos, true);
                    }
                    else{
                        if(checkedItems.get(pos, false)){
                            checkedItems.delete(pos);
                        }
                    }
                }
            });
        }
    }


}
