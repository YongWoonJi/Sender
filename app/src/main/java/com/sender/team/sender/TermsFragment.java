package com.sender.team.sender;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TermsFragment extends Fragment {

    @BindView(R.id.text_terms)
    TextView terms1;

    @BindView(R.id.text_terms2)
    TextView terms2;

    @BindView(R.id.text_terms3)
    TextView terms3;

    @BindView(R.id.checkBox)
    CheckBox checkBoxOne;

    @BindView(R.id.checkBox2)
    CheckBox checkBoxTwo;

    @BindView(R.id.checkBox3)
    CheckBox checkBoxThree;

    public TermsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, view);

        terms1.setText(Html.fromHtml(getResources().getString(R.string.service_terms_content)));
        terms2.setText(Html.fromHtml(getResources().getString(R.string.service_terms_personal_content)));
        terms3.setText(Html.fromHtml(getResources().getString(R.string.service_terms_gps_content)));

        Button btn = (Button) view.findViewById(R.id.btn_next);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxOne.isChecked() == true
                        && checkBoxTwo.isChecked() == true
                        && checkBoxThree.isChecked() == true) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new AuthFragment()).commit();
                } else{
                    Toast.makeText(getContext(), "이용약관에 동의 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

}
