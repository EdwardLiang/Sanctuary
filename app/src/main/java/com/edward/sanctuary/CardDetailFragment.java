package com.edward.sanctuary;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edward.sanctuary.settings.Session;

/**
 * Created by edward on 3/31/17.
 */

public class CardDetailFragment extends Fragment {

    private Card card;

    public CardDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("Card")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            card = (Card)getArguments().getSerializable("Card");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if(Session.getInstance(activity).darkModeSet()) {
                appBarLayout.setBackgroundColor(Color.DKGRAY);
            }
            else{
                appBarLayout.setBackgroundColor(Color.GRAY);
            }
            if (appBarLayout != null) {
                appBarLayout.setTitle(card.getCard_name());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.card_detail, container, false);
        // Show the dummy content as text in a TextView.
        if (card != null) {
            ((TextView) rootView.findViewById(R.id.card_detail)).setText(card.getCard_description());
        }

        return rootView;
    }

}
