package com.example.shim.sosafront.DialogPackage;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.shim.sosafront.R;

public class TypeDialog extends Dialog implements View.OnClickListener {

    ImageButton popupClear;

    public TypeDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_type_dialog);
        popupClear = (ImageButton) findViewById(R.id.popupClear);
        popupClear.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
       if(v == popupClear)
           dismiss();
    }
}
