package com.example.sec.mymap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 保存を押したときに呼び出すダイアログフラグメント
 */
public class MyAlertDialogFragmant extends DialogFragment{

    /**
     * タイトルとメッセージを渡してダイアログフラグメントを作る
     * @param title
     * @param message
     * @return
     */
    public static MyAlertDialogFragmant newInstance(int title, int message){
        MyAlertDialogFragmant frag = new MyAlertDialogFragmant();
        Bundle args = new Bundle();
        args.putInt("title",title);
        args.putInt("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");

        return new AlertDialog.Builder(getActivity()).
                setTitle(title).
                setMessage(message).
                setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog,int whichButton){
                ((MainActivity) getActivity()).doPositiveClick();
            }
        }
        )
//        .setPositiveButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        ((MainActivity) getActivity()).doNegativeClick();
//                    }
//                }
//        )
                .create();
    }
}
