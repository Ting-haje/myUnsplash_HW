package com.example.fal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class RandomDialog extends DialogFragment {

    private ImageView imageView;
    private Context mycontext;

    RandomDialog(Context context){
        mycontext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog1, container);

        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    protected void initView(View view) {
        imageView = view.findViewById(R.id.imageView2);
        Picasso.get()
                .load("https://source.unsplash.com/random")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(mycontext);
                builder.setItems(new String[]{getResources().getString(R.string.save)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SavePhoto savePhoto = new SavePhoto(mycontext);
                        savePhoto.saveImageToGallery(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                    }
                });
                builder.show();
                return true;
            }
        });

    }
}
