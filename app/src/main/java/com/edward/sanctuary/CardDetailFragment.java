package com.edward.sanctuary;


import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edward.sanctuary.database.Database;
import com.edward.sanctuary.settings.Session;

/**
 * Created by edward on 3/31/17.
 */

public class CardDetailFragment extends Fragment {

    private Card card;
    private EditText ed;
    private EditText editTitle;
    private String title;
    private boolean changed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("Card")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            card = (Card)getArguments().getSerializable("Card");
            changed = false;

            Activity activity = this.getActivity();
            final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            AppBarLayout appbarLayout = (AppBarLayout) activity.findViewById(R.id.app_bar);
            if(Session.getInstance(activity).darkModeSet()) {
                toolbarLayout.setBackgroundColor(Color.DKGRAY);
            }
            else{
                toolbarLayout.setBackgroundColor(Color.GRAY);
            }
            if (toolbarLayout != null) {
                toolbarLayout.setTitle("");
            }
            title = card.getCard_name();
            //editTitle = new EditText(getActivity());
            editTitle = (EditText)activity.findViewById(R.id.title2);
            editTitle.setEnabled(false);
            editTitle.setText(title);
            editTitle.setVisibility(View.VISIBLE);
            editTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    title = s.toString();
                    changed = true;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(verticalOffset == 0){
                        CollapsingToolbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(
                                CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, CollapsingToolbarLayout.LayoutParams.MATCH_PARENT);
                        params.setMargins(toolbarLayout.getExpandedTitleMarginStart(),
                                toolbarLayout.getExpandedTitleMarginTop(), toolbarLayout.getExpandedTitleMarginEnd(),
                                toolbarLayout.getExpandedTitleMarginBottom() - 30);
                        //this 30 is very hacky

                        params.setMarginEnd(toolbarLayout.getExpandedTitleMarginEnd());
                        params.setMarginStart(toolbarLayout.getExpandedTitleMarginStart());
                        //System.out.println(toolbarLayout.getExpandedTitleMarginTop());
                        //System.out.println(params.topMargin);
                        editTitle.setLayoutParams(params);
                        editTitle.setGravity(Gravity.BOTTOM);
                        editTitle.setVisibility(View.VISIBLE);
                        editTitle.setTypeface(toolbarLayout.getExpandedTitleTypeface());
                        toolbarLayout.setTitle("");
                        getActivity().setTitle("");
                        if (Build.VERSION.SDK_INT < 23) {
                            editTitle.setTextAppearance(getActivity(), R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
                        } else {
                            editTitle.setTextAppearance(R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
                        }
                        TypedArray theme = getActivity().getTheme().obtainStyledAttributes(new int[] {android.R.attr.editTextColor});
                        try {
                            int color = theme.getColor(0, 0);
                            editTitle.setTextColor(color);
                        }
                        finally
                        {
                            theme.recycle();
                        }
                        toolbarLayout.setTitleEnabled(false);
                    }
                    else{
                        editTitle.setVisibility(View.GONE);
                        toolbarLayout.setTitle(title);
                        toolbarLayout.setTitleEnabled(true);
                    }
                }
            });
        }
    }

    public void setEditable(boolean editable){
        if(editable){
            ed.setEnabled(true);
            editTitle.setEnabled(true);
        }else{
            editTitle.setEnabled(false);
            ed.setEnabled(false);
        }
    }
    public boolean validTitle(){
        return Database.newCard(getActivity(), Session.getInstance(getActivity()).getUserId(), editTitle.getText().toString());
    }

    public void setTitleError(){
        editTitle.setError("Duplicate Card Name!");
    }
    public boolean getChanged(){
        return changed;
    }

    public void saveChangesToDatabase(){
        Database.changeCardName(card, editTitle.getText().toString(), getContext());
        Database.changeCardDescription(card, ed.getText().toString(), getContext());
        changed = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.card_detail, container, false);
        // Show the dummy content as text in a TextView.
        if (card != null) {
            ed = ((EditText) rootView.findViewById(R.id.card_detail));
            ed.setText(card.getCard_description());
            ed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    changed = true;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ed.setEnabled(false);
            TypedArray theme = getActivity().getTheme().obtainStyledAttributes(new int[] {android.R.attr.editTextColor});
            try {
                int color = theme.getColor(0, 0);
                ed.setTextColor(color);
            }
            finally
            {
                theme.recycle();
            }

            //ed.setEnabled(false);
        }

        return rootView;
    }

}
