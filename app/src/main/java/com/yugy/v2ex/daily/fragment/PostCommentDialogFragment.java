package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-3-3.
 */
public class PostCommentDialogFragment extends DialogFragment{

    private int mTopicId;
    private String mCommentContent;
    private OnCommentFinishListener mOnCommentFinishListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopicId = getArguments().getInt("topic_id");
        mCommentContent = getArguments().getString("comment_content");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mOnCommentFinishListener = (OnCommentFinishListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " should implement the OnCommentFinishListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.title_commenting));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mCommentContent.equals("")){
            CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
            commentDialogFragment.setArguments(getArguments());
            commentDialogFragment.show(getFragmentManager(), "comment");
            MessageUtils.toast(getActivity(), getString(R.string.comment_content_empty));
            dismiss();
        }else{
            V2EX.getOnceCode(getActivity(), mTopicId, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    DebugUtils.log(response);
                    if(getDialog() != null){
                        try {
                            if(response.getString("result").equals("ok")){
                                int onceCode = response.getJSONObject("content").getInt("once");
                                V2EX.postComment(getActivity(), onceCode, mTopicId, mCommentContent, new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        if(mOnCommentFinishListener != null){
                                            mOnCommentFinishListener.onCommentFinished(response);
                                        }
                                        dismiss();
                                    }
                                });
                            }else if(response.getString("result").equals("fail")){
                                MessageUtils.toast(getActivity(), getString(R.string.fail_get_once));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public static interface OnCommentFinishListener{
        public void onCommentFinished(JSONObject result);
    }
}
