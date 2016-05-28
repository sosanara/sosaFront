package com.example.shim.sosafront.DialogPackage;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.shim.sosafront.R;

public class AgeTwoDialog extends Dialog implements View.OnClickListener {

    ImageButton popupClear;

    public AgeTwoDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_age_two_dialog);
        popupClear = (ImageButton) findViewById(R.id.popupClear);
        popupClear.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if(v == popupClear)
            dismiss();
    }
}
