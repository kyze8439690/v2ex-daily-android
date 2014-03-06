package com.yugy.v2ex.daily.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.yugy.v2ex.daily.R;

/**
 * Created by yugy on 14-3-3.
 */
public class CommentDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{

    private EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEditText = (EditText) getActivity().getLayoutInflater().inflate(R.layout.fragment_comment_dialog, null);
        if(getArguments().containsKey("comment_content")){
            mEditText.setText(getArguments().getString("comment_content"));
            mEditText.setSelection(mEditText.getText().length());
        }
        return new AlertDialog.Builder(getActivity())
                .setView(mEditText)
                .setPositiveButton(R.string.title_comment, this)
                .setCancelable(true)
                .setNegativeButton(R.string.title_cancel, null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String commentContent;
        if(mEditText.getText().length() == 0){
            commentContent = "";
        }else{
            commentContent = mEditText.getText().toString();
        }
        PostCommentDialogFragment postCommentDialogFragment = new PostCommentDialogFragment();
        Bundle argument = getArguments();
        argument.putString("comment_content", commentContent);
        postCommentDialogFragment.setArguments(argument);
        postCommentDialogFragment.show(getFragmentManager(), "postComment");
        dismiss();
    }
}
