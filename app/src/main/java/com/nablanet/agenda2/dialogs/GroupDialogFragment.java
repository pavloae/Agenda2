package com.nablanet.agenda2.dialogs;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.nablanet.agenda2.R;
import com.nablanet.agenda2.pojos.Group;

public class GroupDialogFragment extends DialogFragment {

    private static final String TITLE = "title";

    NoticeDialogListener mListener;
    Group group;

    public CheckBox groupPublic;
    public EditText groupName, groupComment;
    public ImageView groupImage;
    public Button acceptButton, cancelButton;

    public String name, comment;
    public Bitmap image;
    public boolean publicGroup;

    public static GroupDialogFragment newInstance(String title, Group group) {
        GroupDialogFragment groupDialogFragment = new GroupDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        groupDialogFragment.setArguments(args);
        groupDialogFragment.group = group;
        return groupDialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Nuevo grupo");
        return inflater.inflate(R.layout.dialog_group, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupImage = view.findViewById(R.id.group_image);
        groupName = view.findViewById(R.id.group_name);
        groupComment = view.findViewById(R.id.group_comment);
        groupPublic = view.findViewById(R.id.group_public);
        acceptButton = view.findViewById(R.id.accept_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getImage();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    public void confirm(){
        name = groupName.getText().toString();
        comment = groupComment.getText().toString();
        publicGroup = groupPublic.isChecked();
        mListener.onDialogPositiveClick(GroupDialogFragment.this);
        dismiss();
    }


    public interface NoticeDialogListener {
        void onDialogPositiveClick(GroupDialogFragment dialog);
        void getImage();
    }


}
